package com.example.whatsdown.utils;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;

public class FirebaseUtil {

    public static String currentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }

    public static boolean isLoggedIn(){
        if(currentUserId()!=null){
            return true;
        }
        return false;
    }

    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static DocumentReference getChatroomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    public static CollectionReference getChatroomMessageReference(String chatroomId){
        return getChatroomReference(chatroomId).collection("chats");
    }

    public static String getChatroomId(String userId1,String userId2){
        if(userId1.hashCode()<userId2.hashCode()){
            return userId1+"_"+userId2;
        }else{
            return userId2+"_"+userId1;
        }
    }

    public static CollectionReference allChatroomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    public static DocumentReference getOtherUserFromChatroom(List<String> userIds){
        if(userIds.get(0).equals(FirebaseUtil.currentUserId())){
            return allUserCollectionReference().document(userIds.get(1));
        }else{
            return allUserCollectionReference().document(userIds.get(0));
        }
    }

    public static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
    }

    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }

    public static StorageReference  getCurrentProfilePicStorageRef(){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(FirebaseUtil.currentUserId());
    }

    public static StorageReference  getOtherProfilePicStorageRef(String otherUserId){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(otherUserId);
    }

    public static void updateChatMessage(String chatroomId, String userId, Timestamp timestamp, String newMessage) {
        CollectionReference chatroomMessagesRef = getChatroomMessageReference(chatroomId);

        // Query for the specific message based on userId and timestamp
        Query query = chatroomMessagesRef
                .whereEqualTo("senderId", userId)
                .whereEqualTo("timestamp", timestamp);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Update the message
                        DocumentReference messageRef = chatroomMessagesRef.document(document.getId());
                        messageRef.update("message", newMessage)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Successfully updated the message
                                            Log.e("EDIT DELETE", "MESSAGE UPDATED");

                                        } else {
                                            // Handle the error
                                            Log.e("EDIT DELETE", "MESSAGE UPDATED ERROR");

                                        }
                                    }
                                });
                    }
                } else {
                    // Handle the error
                }
            }
        });
    }

    public static void deleteChatMessage(String chatroomId, String userId, Timestamp timestamp) {
        CollectionReference chatroomMessagesRef = getChatroomMessageReference(chatroomId);

        // Query for the specific message based on userId and timestamp
        Query query = chatroomMessagesRef
                .whereEqualTo("senderId", userId)
                .whereEqualTo("timestamp", timestamp);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Delete the message
                        DocumentReference messageRef = chatroomMessagesRef.document(document.getId());
                        messageRef.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            // Successfully deleted the message
                                            Log.e("EDIT DELETE", "MESSAGE DELETED");

                                        } else {
                                            // Handle the error
                                            Log.e("EDIT DELETE", "MESSAGE DELETED ERROR");

                                        }
                                    }
                                });
                    }
                } else {
                    // Handle the error
                }
            }
        });
    }


}










