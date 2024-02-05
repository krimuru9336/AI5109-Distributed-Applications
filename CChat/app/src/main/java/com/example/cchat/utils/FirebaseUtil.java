package com.example.cchat.utils;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;

public class FirebaseUtil {

    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    public static  Boolean isLoggedIn() {
        if(currentUserId() != null) {
            return true;
        }
        return false;
    }
    public static DocumentReference currentUserDetails() {
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    public static CollectionReference allUserCollectionReference() {
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static DocumentReference getChatroomReference(String chatroomId) {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    public static String getChatroomId(String senderId, String receiverId) {
        if(senderId.hashCode() < receiverId.hashCode()) {
            return senderId+"_"+receiverId;
        } else {
            return receiverId+"_"+senderId;
        }
    }

    public static CollectionReference getChatroomMessageReference(String chatroomId) {
        return getChatroomReference(chatroomId).collection("chats");
    }

    public static DocumentReference getChatMessageReference(String chatroomId, String messageId) {
        return getChatroomMessageReference(chatroomId).document(messageId);
    }

    public static CollectionReference allChatRoomCollectionReference() {
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    public static CollectionReference secretsReference() {
        return FirebaseFirestore.getInstance().collection("secrets");
    }

    public static DocumentReference getSecrets() {
        return secretsReference().document("apiKeys");
    }
    public static DocumentReference getOtherUserFromChatroom(List<String> userIds) {
        if(userIds.get(0).equals(FirebaseUtil.currentUserId())) {
            return allUserCollectionReference().document(userIds.get(1));
        } else {
            return allUserCollectionReference().document(userIds.get(0));
        }
    }

    public static String timestampToString(Timestamp timestamp) {
        return new SimpleDateFormat("HH:mm").format(timestamp.toDate());
    }

    public static void logout() {
        FirebaseAuth.getInstance().signOut();
    }

    public static StorageReference getCurrentProfilePicStorageRef() {
        return FirebaseStorage.getInstance().getReference().child("profile_picture")
                .child(FirebaseUtil.currentUserId());
    }

    public static StorageReference getOtherProfilePicStorageRef(String userId) {
        return FirebaseStorage.getInstance().getReference().child("profile_picture")
                .child(userId);
    }


}
