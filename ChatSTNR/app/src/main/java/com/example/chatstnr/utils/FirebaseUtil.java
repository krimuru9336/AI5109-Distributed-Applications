package com.example.chatstnr.utils;

import android.net.Uri;

import com.example.chatstnr.models.UserModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FirebaseUtil {

    public static String currentUserid(){
        return FirebaseAuth.getInstance().getUid();
    }

    public  static boolean isLoggedin(){
        if(currentUserid()!= null){
            return true;
        }
        return false;
    }
    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("users").document(currentUserid());
    }

    public static DocumentReference getUserDetails(String userId){
        return FirebaseFirestore.getInstance().collection("users").document(userId);
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
        if(userIds.get(0).equals(FirebaseUtil.currentUserid())){
            return allUserCollectionReference().document(userIds.get(1));
        }else{
            return allUserCollectionReference().document(userIds.get(0));
        }
    }

    public static String timestampToString(Timestamp timestamp){
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String formattedDate = sdf.format(date);

        return formattedDate;
    }

    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }

    public static StorageReference  getCurrentProfilePicStorageRef(){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(FirebaseUtil.currentUserid());
    }

    public static StorageReference  getOtherProfilePicStorageRef(String otherUserId){
        return FirebaseStorage.getInstance().getReference().child("profile_pic")
                .child(otherUserId);
    }


    public static StorageReference  getCurrentChatRoomStorageRef(String chatroomId, String messageId){
        return FirebaseStorage.getInstance().getReference().child("chat_room")
                .child(chatroomId).child(messageId);
    }

    public static CollectionReference getAllGroupDetails(){
        return FirebaseFirestore.getInstance().collection("groups");
    }

    public static CollectionReference getGroupChatroomReference(String groupId) {
        return FirebaseFirestore.getInstance().collection("groups").document(groupId).collection("chats");
    }

    public static DocumentReference getGroupReference(String groupId){
        return FirebaseFirestore.getInstance().collection("groups").document(groupId);
    }

    public static StorageReference  getCurrentGroupChatStorageRef(String groupId, String messageId){
        return FirebaseStorage.getInstance().getReference().child("group_chats")
                .child(groupId).child(messageId);
    }

    public static CollectionReference allGroupsCollectionReference() {
        return FirebaseFirestore.getInstance().collection("groups");
    }

    // This method fetches all groups where the given userId is a member
    public static Query getGroupsOfUser(String userId) {
        // Assuming you have a field in each group document called "members" which is an array
        // containing the IDs of all members
        return allGroupsCollectionReference().whereArrayContains("userIds", userId);
    }
}
