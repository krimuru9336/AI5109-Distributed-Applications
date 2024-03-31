package com.example.myapplication5.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication5.ChatMessageModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class FirebaseUtil {

//    public static String oneOneRoom = "chatrooms";
//    public static String groupRoom = "groupChats";

    public static String currentUserId(){
        System.out.println("Firebase util line 1" +FirebaseAuth.getInstance().getUid());
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

    public static void updateChatMessage(String chatroomId, ChatMessageModel updatedMessage) {

        CollectionReference chatroomRef = getChatroomMessageReference(chatroomId);

        // Find the message by messageId
        Query query = chatroomRef.whereEqualTo("messageId", updatedMessage.getMessageId());

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    // Update the message in Firestore
                    chatroomRef.document(document.getId()).set(updatedMessage.toMap())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Update successful
                                }
                            });
                    break;  // Assuming messageId is unique, we can break after finding the first match
                }
            }
        });
    }

    public static void deleteChatMessage(String chatroomId, ChatMessageModel messageToDelete) {
        CollectionReference chatroomRef = getChatroomMessageReference(chatroomId);

        // Find the message by messageId
        Query query = chatroomRef.whereEqualTo("messageId", messageToDelete.getMessageId());

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    // Delete the message from Firestore
                    chatroomRef.document(document.getId()).delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Deletion successful
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Log the error if deletion fails
                                    Log.e("FirebaseUtil", "Error deleting message", e);
                                }
                            });

                    break;  // Assuming messageId is unique, we can break after finding the first match
                }
            }
        });
    }

//    public static String generateMessageId(String chatroomId) {
//
//        return getChatroomMessageReference(chatroomId, FirebaseUtil.oneOneRoom).document().getId();
//
//
//    }

    public static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("dd/MM, HH:mm", Locale.getDefault()).format(timestamp.toDate());
    }

    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }


    public interface UsernameCallback {
        void onUsernameFound(String username);
        void onUsernameNotFound();
    }

    public static String getUsernameFromUserId(String userId, UsernameCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Extract the username from the document
                            String username = document.getString("username");
                            callback.onUsernameFound(username);
                        } else {
                            callback.onUsernameNotFound();
                        }
                    } else {
                        callback.onUsernameNotFound();
                    }
                });
        return userId;
    }


}