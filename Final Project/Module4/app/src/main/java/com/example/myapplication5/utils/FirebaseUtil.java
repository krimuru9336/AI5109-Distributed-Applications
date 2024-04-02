package com.example.myapplication5.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication5.ChatMessageModel;
import com.example.myapplication5.HelperClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
//        System.out.println("in firebaseutil "+userId1 +"   "+ userId2);
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
        // Find the message by timestamp
        Query query = chatroomRef.whereEqualTo("timestamp", updatedMessage.getTimestamp());

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
                    break;  // Assuming timestamp is unique, we can break after finding the first match
                }
            }
        });
    }

    public static void deleteChatMessage(String chatroomId, ChatMessageModel messageToDelete) {
        CollectionReference chatroomRef = getChatroomMessageReference(chatroomId);
    System.out.println("firebase util delete " + messageToDelete.getTimestamp());
        // Find the message by timestamp
        Query query = chatroomRef.whereEqualTo("timestamp", messageToDelete.getTimestamp());

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
                             System.out.println("firebase util delete2 " + e);
                            Log.e("FirebaseUtil", "Error deleting message", e);
                        }
                    });

                    break;  // Assuming timestamp is unique, we can break after finding the first match
                }
            }
        });
    }

    public static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("dd/MM, HH:mm", Locale.getDefault()).format(timestamp.toDate());
    }

    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }




//    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//    public static void checkUsernameAvailability(final String username, final OnUsernameCheckListener listener) {
//        db.collection("users")
//                .whereEqualTo("username", username)
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        if (documentSnapshot.exists()) {
//                            // Username is already taken
//                            listener.onUsernameCheck(false);
//                        } else {
//                            // Username is unique
//                            listener.onUsernameCheck(true);
//                        }
//                    }
//                });
//    }

//    public interface OnUsernameCheckListener {
//        void onUsernameCheck(boolean isUnique);
//    }


//    public static String currentUsername(){
//
//
////        return FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        System.out.println(currentUser + "in adapter");
//
//        if (currentUser != null) {
//            String displayName = currentUser.getDisplayName();
//
//            System.out.println(currentUser.getUid() + "in adapter line 2");
//
//            if (displayName != null && !displayName.isEmpty()) {
//                return displayName;
//            } else {
//                // Display name not set
//                return "Unknown User";
//            }
//        } else {
//            // User not authenticated
//            return "Not Authenticated";
//        }
//
//    }

//
//    public static DocumentReference currentUserDetails(){
//
//        return FirebaseFirestore.getInstance().collection("users").document(currentUsername());
//    }
//
//    public static CollectionReference allUserCollectionReference(){
//        return FirebaseFirestore.getInstance().collection("users");
//    }
//
//    public static DocumentReference getChatroomReference(String chatroomId){
//        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
//    }
//
//    public static CollectionReference getChatroomMessageReference(String chatroomId){
//        return getChatroomReference(chatroomId).collection("chats");
//    }
//
//    public static String getChatroomId(String userId1,String userId2){
//        if(userId1.hashCode()<userId2.hashCode()){
//            return userId1+"_"+userId2;
//        }else{
//            return userId2+"_"+userId1;
//        }
//    }
//
//    public static CollectionReference allChatroomCollectionReference(){
//        return FirebaseFirestore.getInstance().collection("chatrooms");
//    }
//
//    public static DocumentReference getOtherUserFromChatroom(List<String> userIds){
//        if(userIds.get(0).equals(FirebaseUtil.currentUsername())){
//            return allUserCollectionReference().document(userIds.get(1));
//        }else{
//            return allUserCollectionReference().document(userIds.get(0));
//        }
//    }
//
//    public static String timestampToString(Timestamp timestamp){
//        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
//    }
//
//    public static void logout(){
//        FirebaseAuth.getInstance().signOut();
//    }


}