package com.example.distributedapplicationsproject.firebase;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;
import androidx.annotation.NonNull;
import com.example.distributedapplicationsproject.db.MediaCachingService;
import com.example.distributedapplicationsproject.models.chat.Chat;
import com.example.distributedapplicationsproject.models.chat.ChatInfo;
import com.example.distributedapplicationsproject.utils.DataShare;
import com.example.distributedapplicationsproject.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.*;

import java.io.*;
import java.util.UUID;

import static com.example.distributedapplicationsproject.firebase.DatabaseService.GROUP_CHATS_PATH;
import static com.example.distributedapplicationsproject.firebase.DatabaseService.PRIVATE_CHATS_PATH;

public class StorageService {
    private StorageReference storageReference;

    private StorageService() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Get reference to the root of your storage bucket or specify a child path
        storageReference = storage.getReference();
    }

    private static StorageService instance;

    // Method to get the singleton instance
    public static StorageService getInstance() {
        if (instance == null) {
            // Create the instance if it doesn't exist
            instance = new StorageService();
        }
        return instance;
    }

    /**
     * upload a media file to firebase storage
     */
    public void uploadMedia(ChatInfo chatInfo, ContentResolver contentResolver, Uri mediaUri, OnMediaUploadListener listener) {
        StorageReference mediaReference = getStorageReferenceMediaPath(chatInfo, contentResolver, mediaUri);
        long totalBytes = getMediaSize(contentResolver, mediaUri);
        // Upload the file to the specified storage location
        try {
            UploadTask uploadTask = mediaReference.putStream(contentResolver.openInputStream(mediaUri));
            // Register observers to listen for upload success or failure
            uploadTask.addOnProgressListener(taskSnapshot -> {
                // Get the bytes transferred and total bytes
                long bytesTransferred = taskSnapshot.getBytesTransferred();

                // Calculate progress percentage
                int progress = (int) (100.0 * ((double) bytesTransferred / totalBytes));
                // Notify Listener
                listener.onMediaProgression(progress);
            }).addOnSuccessListener(taskSnapshot -> {
                // Notify Listener
                listener.onMediaUploaded();
                // Notify Listener
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(listener::onMediaDownloadUrl);
            }).addOnFailureListener(listener::onMediaFailed); // Notify Listener
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void downloadMedia(String mediaUrl, OnMediaDownloadListener listener) {
        // Try getting media from cache
        String localMediaUri = DataShare.getInstance().getMediaCachingService().getLocalMediaUri(mediaUrl);
        if (localMediaUri != null) {
            listener.onMediaDownloaded();
            listener.onMediaLocalUrl(Uri.parse(localMediaUri));
            return;
        }

        StorageReference mediaReference = storageReference.getStorage().getReferenceFromUrl(mediaUrl);

        try {
            File localFile = File.createTempFile("Random", "Random");
            mediaReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created, save to cache and notify listener
                    Uri localFileUri = Uri.parse(localFile.getAbsolutePath());
                    DataShare.getInstance().getMediaCachingService().addMedia(mediaUrl, localFileUri.toString());
                    listener.onMediaDownloaded();
                    listener.onMediaLocalUrl(localFileUri);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteMedia(String mediaUrl) {
        if (mediaUrl == null) {
            return;
        }
        StorageReference mediaReference = storageReference.getStorage().getReferenceFromUrl(mediaUrl);
        DataShare.getInstance().getMediaCachingService().removeMedia(mediaUrl);

        mediaReference.delete();
    }

    private String getMediaFileUrl() {
        return "";
    }

    private StorageReference getStorageReferencePath(ChatInfo chatInfo) {
        StorageReference mediaReference = storageReference.child(PRIVATE_CHATS_PATH);
        if (chatInfo.type == Chat.ChatType.GROUP){
            mediaReference = storageReference.child(GROUP_CHATS_PATH);
        }
        mediaReference = mediaReference.child("/" + chatInfo.id);
        return mediaReference;
    }

    private StorageReference getStorageReferenceMediaPath(ChatInfo chatInfo, ContentResolver contentResolver, Uri mediaUri) {
        StorageReference mediaReference = getStorageReferencePath(chatInfo);

        // go into media specific folder, /image or /video etc.
        mediaReference = mediaReference.child("/" + getMediaType(contentResolver, mediaUri));
        // Create a reference to the file you want to upload
        mediaReference = mediaReference.child("/" + generateMediaName(contentResolver, mediaUri));

        return mediaReference;
    }

    private String generateMediaName(ContentResolver contentResolver, Uri mediaUri) {
        return UUID.randomUUID() + "." + getMediaExtension(contentResolver, mediaUri);
    }

    private String getMediaType(ContentResolver contentResolver, Uri mediaUri) {
        return contentResolver.getType(mediaUri).split("/")[0];
    }

    private String getMediaExtension(ContentResolver contentResolver, Uri mediaUri) {
        // Get file extension
        Cursor cursor = contentResolver.query(mediaUri, null, null, null, null);
        cursor.moveToFirst();
        @SuppressLint("Range")
        String mediaExtension = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)).split("\\.")[1];
        cursor.close();
        return mediaExtension;
    }

    private long getMediaSize(ContentResolver contentResolver, Uri mediaUri) {
        // Get file size
        Cursor cursor = contentResolver.query(mediaUri, null, null, null, null);
        cursor.moveToFirst();
        @SuppressLint("Range")
        long mediaSize = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE));
        cursor.close();

        return mediaSize;
    }

    public interface OnMediaUploadListener {
        void onMediaUploaded();
        void onMediaFailed(Exception e);
        void onMediaProgression(int progression);
        void onMediaDownloadUrl(Uri mediaDownloadUri);
    }

    public interface OnMediaDownloadListener {
        void onMediaDownloaded();
        void onMediaFailed(Exception e);
        void onMediaProgression(int progression);
        void onMediaLocalUrl(Uri mediaDownloadUri);
    }
}
