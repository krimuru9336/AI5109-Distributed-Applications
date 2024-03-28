package com.da.chitchat.services;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;

import androidx.core.content.FileProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import com.bumptech.glide.Glide;
import com.da.chitchat.activities.MessageActivity;
import com.da.chitchat.interfaces.MessageListener;
import com.da.chitchat.singletons.AppContextSingleton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class MediaConverter {
    MessageListener messageListener;
    private final Map<UUID, TreeMap<Long, String>> chunkMap;
    // UUID - <ChunkSize, MimeType>
    private final HashMap<UUID, MediaMessageData> finishedMap;

    public static class MediaMessageData {
        private final String target;
        private final int chunkCounts;
        private final String mimeType;
        private final boolean isGroup;

        public MediaMessageData(String target, int chunkCounts, String mimeType, boolean isGroup) {
            this.target = target;
            this.chunkCounts = chunkCounts;
            this.mimeType = mimeType;
            this.isGroup = isGroup;
        }

        public String getTarget() {
            return target;
        }

        public int getChunkCounts() {
            return chunkCounts;
        }

        public String getMimeType() {
            return mimeType;
        }

        public boolean isGroup() {
            return isGroup;
        }
    }

    public MediaConverter() {
        chunkMap = new HashMap<>();
        finishedMap = new HashMap<>();
    }

    public void setMessageListener(MessageListener listener) {
        messageListener = listener;
    }

    public String convertBitmapToBase64(Context ctx, Uri imageUri) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            // Get the MIME type of the image
            String mimeType = ctx.getContentResolver().getType(imageUri);

            Bitmap bitmap;
            byteArrayOutputStream = new ByteArrayOutputStream();

            // Check if the image is a GIF
            if (isGif(ctx, imageUri)) {
                try (InputStream inputStream = ctx.getContentResolver().openInputStream(imageUri)) {
                    if (inputStream != null) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            byteArrayOutputStream.write(buffer, 0, bytesRead);
                        }
                    } else {
                        return "";
                    }
                }
                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                return Base64.encodeToString(imageBytes, Base64.DEFAULT);
            } else {
                // For other image formats (e.g., JPEG), compress the bitmap
                bitmap = MediaStore.Images.Media.getBitmap(ctx.getContentResolver(), imageUri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                byte[] imageBytes = byteArrayOutputStream.toByteArray();
                return Base64.encodeToString(imageBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (byteArrayOutputStream != null)
                    byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public void showImage(Context ctx, Uri selectedImageUri, ImageView imageView) {
        // Check if the selected image is a GIF
        boolean isGif = isGif(ctx, selectedImageUri);

        if (isGif) {
            // Display the selected GIF using Glide
            Glide.with(ctx)
                    .asGif()
                    .load(selectedImageUri)
                    .into(imageView);
        } else {
            // Display the selected image using Glide
            Glide.with(ctx)
                    .asBitmap()
                    .load(selectedImageUri)
                    .into(imageView);
        }
    }

    public void showVideo(Uri selectedVideoUri, ExoPlayer exoPlayer) {
        MediaItem mediaItem = MediaItem.fromUri(selectedVideoUri);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.prepare();
    }

    private boolean isGif(Context ctx, Uri selectedImageUri) {
        String mimeType = ctx.getContentResolver().getType(selectedImageUri);
        return mimeType != null && mimeType.endsWith("gif");
    }

    public void addChunk(Context ctx, String imageBase64Chunk, UUID id, long offset) {
        TreeMap<Long, String> idMap = chunkMap.computeIfAbsent(id, k -> new TreeMap<>());
        idMap.put(offset, imageBase64Chunk);
        if (finishedMap.containsKey(id)) {
            MediaMessageData mmd = finishedMap.get(id);
            if (mmd != null) {
                saveMedia(ctx, mmd.getTarget(), id, mmd.getChunkCounts(), mmd.getMimeType(), mmd.isGroup());
            }
        }
    }

    public void saveMedia(Context ctx, String target, UUID id, int chunkCount, String mimeType,
                          boolean isGroup) {
        if (checkIfFinished(target, id, chunkCount, mimeType, isGroup)) {
            finishedMap.remove(id);
            String base64Media = mergeMediaChunks(id);
            saveImageFromBase64(ctx, target, id, base64Media, mimeType, isGroup);
        }
    }

    private String mergeMediaChunks(UUID id) {
        TreeMap<Long, String> idMap = chunkMap.get(id);
        if (idMap != null) {
            StringBuilder mergedChunks = new StringBuilder();

            for (String chunk : idMap.values()) {
                mergedChunks.append(chunk);
            }

            return mergedChunks.toString();
        } else {
            return "";
        }
    }

    private boolean checkIfFinished(String target, UUID id, int chunkCounts, String mimeType,
                                    boolean isGroup) {
        if (chunkMap.containsKey(id)) {
            TreeMap<Long, String> idMap = chunkMap.get(id);
            if (idMap != null) {
                // Transmission not finished, add to queue
                if (idMap.size() != chunkCounts) {
                    finishedMap.put(id, new MediaMessageData(target, chunkCounts, mimeType, isGroup));
                    return false;
                }
            }
        }
        return true;
    }

    public void saveImageFromBase64(Context ctx, String target, UUID id, String base64ImageData,
                                    String mimeType, boolean isGroup) {
        // Decode base64 image data to binary
        byte[] imageData = Base64.decode(base64ImageData, Base64.DEFAULT);

        // Determine file extension based on MIME type
        String fileExtension = getFileExtensionFromMimeType(mimeType);
        if (fileExtension == null) {
            // Unsupported MIME type
            return;
        }

        // Save image to external storage
        File directory = ctx.getExternalFilesDir("Media");
        File file = new File(directory, "image_" + System.currentTimeMillis() + fileExtension);

        Log.d("FilePath", "File path: " + file.getAbsolutePath());

        Uri fileUri = FileProvider.getUriForFile(ctx, ctx.getApplicationContext().getPackageName() + ".provider", file);
        ctx.grantUriPermission(ctx.getApplicationContext().getPackageName(), fileUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


        try (OutputStream outputStream = ctx.getContentResolver().openOutputStream(fileUri)) {
            if (outputStream == null) return;
            outputStream.write(imageData);

            // Add image to MediaStore
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, file.getName());
            values.put(MediaStore.Images.Media.MIME_TYPE, mimeType);
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
            values.put(MediaStore.Images.Media.SIZE, imageData.length);
            values.put(MediaStore.Images.Media.IS_PENDING, 1);

            Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri imageUri = ctx.getContentResolver().insert(contentUri, values);

            if (imageUri != null) {
                try {
                    OutputStream os = ctx.getContentResolver().openOutputStream(imageUri);
                    if (os != null) {
                        os.write(imageData);
                        os.close();
                    }
                    values.clear();
                    values.put(MediaStore.Images.Media.IS_PENDING, 0);
                    ctx.getContentResolver().update(imageUri, values, null, null);
                } catch (IOException e) {
                    ctx.getContentResolver().delete(imageUri, null, null);
                }
            }

            if (messageListener != null) {
                messageListener.onMediaReceived(target, id, imageUri, isGroup);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFileExtensionFromMimeType(String mimeType) {
        switch (mimeType) {
            case "image/jpeg":
            case "image/jpg":
                return ".jpg";
            case "image/png":
                return ".png";
            case "image/gif":
                return ".gif";
            default:
                return null; // Unsupported MIME type
        }
    }
}
