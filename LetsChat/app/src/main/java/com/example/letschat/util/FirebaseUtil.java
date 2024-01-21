package com.example.letschat.util;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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

}
