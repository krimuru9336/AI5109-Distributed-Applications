package com.example.letschat.util;

import android.net.Uri;

import com.google.firebase.Firebase;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class FirebaseUtil {
    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static DocumentReference currentUserDetails() {
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    public static boolean isLoggedIn() {
        return currentUserId() != null;
    }

    public static CollectionReference allUserCollectionReference() {
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static CollectionReference allChatRoomCollectionReference() {
        return FirebaseFirestore.getInstance().collection("chatRooms");
    }


    public static DocumentReference getChatRoomReference(String chatRoomId) {
        return FirebaseFirestore.getInstance().collection("chatRooms").document(chatRoomId);

    }

    public static CollectionReference getChatMessageReference(String chatRoomId) {
        return getChatRoomReference(chatRoomId).collection("chats");

    }

    public static String getChatRoomId(String userId1, String userId2) {
        if (userId1.hashCode() < userId2.hashCode()) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }

    }

    public static String createMessageId() {
        return UUID.randomUUID().toString();
    }

    public static DocumentReference getOtherUserFromChatRoom(List<String> userIds) {
        if (userIds.get(0).equals(FirebaseUtil.currentUserId())) {
            return allUserCollectionReference().document(userIds.get(1));
        } else {
            return allUserCollectionReference().document(userIds.get(0));
        }

    }

    public static String timestampToString(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(timestamp.toDate());
    }

    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }

    public static StorageReference getCurrentProfilePicStorageRef(String userId){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(userId);
    }

    public interface OnImageUploadListener {
        void onImageUploadSuccess(Uri imageUrl);

        void onImageUploadFailure(Exception e);
    }

    public static void uploadImage(Uri imageUri, OnImageUploadListener listener) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images").child(imageUri.getLastPathSegment());
        UploadTask uploadTask = storageRef.putFile(imageUri);
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }
            return storageRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                listener.onImageUploadSuccess(downloadUri);
            } else {
                listener.onImageUploadFailure(task.getException());
            }
        });
    }

    public interface OnVideoUploadListener {
        void onVideoUploadSuccess(Uri videoUrl);

        void onVideoUploadFailure(Exception e);
    }

    public static void uploadVideo(Uri videoUri, OnVideoUploadListener listener) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("videos").child(videoUri.getLastPathSegment());
        UploadTask uploadTask = storageRef.putFile(videoUri);
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }
            return storageRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                listener.onVideoUploadSuccess(downloadUri);
            } else {
                listener.onVideoUploadFailure(task.getException());
            }
        });
    }

    public interface OnGifUploadListener {
        void onGifUploadSuccess(Uri gifUrl);

        void onGifUploadFailure(Exception e);
    }

    public static void uploadGif(Uri gifUri, OnGifUploadListener listener) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("gifs").child(gifUri.getLastPathSegment());
        UploadTask uploadTask = storageRef.putFile(gifUri);
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }
            return storageRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                listener.onGifUploadSuccess(downloadUri);
            } else {
                listener.onGifUploadFailure(task.getException());
            }
        });
    }


}
