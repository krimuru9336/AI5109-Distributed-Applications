package de.lorenz.da_exam_project.utils;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import de.lorenz.da_exam_project.Constants;

public class FirebaseUtil {

    /**
     * Returns the current user id or null if no user is logged in.
     */
    public static String getCurrentUserId() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return null;
        return FirebaseAuth.getInstance().getUid();
    }

    /**
     * Returns the current user details or null if no user is logged in.
     */
    public static DocumentReference getCurrentUserDetails() {
        String currentUserId = getCurrentUserId();
        if (currentUserId == null) return null;
        return FirebaseFirestore.getInstance().collection(Constants.FB_USERS_COLLECTION).document(currentUserId);
    }

    /**
     * Registers a user anonymously.
     */
    public static Task<AuthResult> registerUserAnonymously() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // if no firebaseId is set => make him able to "register"
        return mAuth.signInAnonymously();
    }

    /**
     * Returns true if a user is logged in.
     */
    public static boolean isLoggedIn() {
        return getCurrentUserId() != null;
    }

    /**
     * Returns a reference to the users collection.
     */
    public static CollectionReference getAllUsers() {
        return FirebaseFirestore.getInstance().collection(Constants.FB_USERS_COLLECTION);
    }

    /**
     * Returns a reference to the chat rooms collection.
     */
    public static DocumentReference getChatRoomReference(String chatRoomId) {
        return FirebaseFirestore.getInstance().collection(Constants.FB_CHATS_COLLECTION).document(chatRoomId);
    }

    /**
     * Returns a reference to the chat room messages collection.
     */
    public static CollectionReference getChatRoomMessagesReference(String chatRoomId) {
        return FirebaseUtil.getChatRoomReference(chatRoomId).collection(Constants.FB_MESSAGES_COLLECTION);
    }

    public static Task<QuerySnapshot> getLastMessage(String chatRoomId) {
        return FirebaseUtil.getChatRoomReference(chatRoomId).collection(Constants.FB_MESSAGES_COLLECTION).orderBy("timestamp", Query.Direction.DESCENDING).limit(1).get();
    }
}
