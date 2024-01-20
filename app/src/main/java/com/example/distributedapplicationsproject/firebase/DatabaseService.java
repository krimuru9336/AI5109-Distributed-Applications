package com.example.distributedapplicationsproject.firebase;

import com.example.distributedapplicationsproject.models.Message;
import com.example.distributedapplicationsproject.models.User;
import com.example.distributedapplicationsproject.models.chat.Chat;
import com.example.distributedapplicationsproject.models.chat.ChatInfo;
import com.example.distributedapplicationsproject.models.chat.GroupChat;
import com.example.distributedapplicationsproject.models.chat.PrivateChat;
import com.example.distributedapplicationsproject.utils.DataShare;
import com.example.distributedapplicationsproject.utils.Utils;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.*;

import java.util.*;
import java.util.stream.Collectors;

public class DatabaseService {

    private StorageService storageService = StorageService.getInstance();
    private final DatabaseReference databaseReference;

    static final String USER_PATH = "users";
    static final String CHATS_PATH = "chats";

    static final String PRIVATE_CHATS_PATH = CHATS_PATH + "/private";

    static final String GROUP_CHATS_PATH = CHATS_PATH + "/group";

    public static User currentUser;

    private DatabaseService() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Enable persistent data, even on restart and stop of app
        database.setPersistenceEnabled(true);
        // Get reference to the root of your Realtime Database or specify a child path
        databaseReference = database.getReference();

        initTestUsers();
    }

    private static DatabaseService instance;

    // Method to get the singleton instance
    public static DatabaseService getInstance() {
        if (instance == null) {
            // Create the instance if it doesn't exist
            instance = new DatabaseService();
        }
        return instance;
    }

    private void initTestUsers() {
        this.createUser(new User("9d9fc91f-27c6-4f00-a556-5b7b343410d2", "Test"));
        this.createUser(new User("489a15d3-2eae-4e8a-9cec-a46a5a6df16e", "Nick"));
        this.createUser(new User("0542195a-fea0-4108-b48b-a4ec4caad71e", "Admin"));
        this.createUser(new User("4d9637e9-1a68-486c-8498-2229ad7d41ba", "Olaf"));
    }

    public void createUser(User user) {
        databaseReference.child(USER_PATH).child(user.getId()).setValue(user);
    }

    public void createPrivateChat(User senderUser, User receiverUser) {

        this.hasChatMembers(Chat.ChatType.PRIVATE, Arrays.asList(senderUser.getId(), receiverUser.getId()), hasMembers -> {
            if (hasMembers) {
                return;
            }
            PrivateChat privateChat = new PrivateChat();

            privateChat.setMemberIdList(Arrays.asList(senderUser.getId(), receiverUser.getId()));

            databaseReference.child(PRIVATE_CHATS_PATH).child(privateChat.getId()).setValue(privateChat);
        });
    }

    public GroupChat createGroupChat(String title, User senderUser, User receiverUser) {
        return createGroupChat(title, senderUser, Arrays.asList(senderUser, receiverUser));
    }

    public GroupChat createGroupChat(String title, User senderUser, List<User> receiverUserList) {
        List<String> receiverUserIdList = new ArrayList<>();
        for (User user : receiverUserList) {
            receiverUserIdList.add(user.getId());
        }
        return createGroupChatByIds(title, senderUser, receiverUserIdList);
    }

    public GroupChat createGroupChatByIds(String title, User senderUser, List<String> receiverUserIdList) {
        GroupChat groupChat = new GroupChat();
        groupChat.setCreatorId(senderUser.getId());
        groupChat.setMemberIdList(receiverUserIdList);
        groupChat.setTitle(title);

        databaseReference.child(GROUP_CHATS_PATH).child(groupChat.getId()).setValue(groupChat);

        return groupChat;
    }

    //    public void addUsersToGroupChat(GroupChat groupChat, List<User> userList) {
//        for(User user : userList) {
//            addUserToGroupChat(groupChat, user);
//        }
//    }
//
//    public void addUserToGroupChat(GroupChat groupChat, User user) {
//
//    }
    public void hasChatMembers(Chat.ChatType type, List<String> userIdList, OnResultBool callback) {
        DatabaseReference usersRef = databaseReference.child(PRIVATE_CHATS_PATH);
        if (type == Chat.ChatType.GROUP) {
            usersRef = databaseReference.child(GROUP_CHATS_PATH);
        }

        Task<DataSnapshot> dataSnapshotTask = usersRef.get();
        dataSnapshotTask.addOnSuccessListener(dataSnapshot -> {
            List<PrivateChat> chatList = new ArrayList<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Chat chat = snapshot.getValue(PrivateChat.class);
                if (chat != null) {
                    if (new HashSet<>(userIdList).containsAll(chat.getMemberIdList())) {
                        callback.onResultBool(true);
                        return;
                    }
                }
            }
            callback.onResultBool(false);
        });
    }


    public void getUsers(OnUsersDataListener listener) {
        DatabaseReference usersRef = databaseReference.child(USER_PATH);

        Task<DataSnapshot> dataSnapshotTask = usersRef.get();
        dataSnapshotTask.addOnSuccessListener(dataSnapshot -> {
            List<User> userList = new ArrayList<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    userList.add(user);
                }
            }
            DataShare.getInstance().setUserList(userList);
            listener.onUsersDataLoaded(userList);
        }).addOnFailureListener(listener::onUsersDataError);
    }

    public void getChatsOfCurrentUser(OnChatsDataListener listener) {
        List<Chat> accumulatedChatList = new ArrayList<>();

        getChats(Chat.ChatType.PRIVATE, new OnChatsDataListener() {
            @Override
            public void onChatsDataLoaded(List<Chat> chatList) {
                accumulatedChatList.addAll(chatList);

                getChats(Chat.ChatType.GROUP, new OnChatsDataListener() {
                    @Override
                    public void onChatsDataLoaded(List<Chat> chatList) {
                        accumulatedChatList.addAll(chatList);
                        // Filter out other chats
                        listener.onChatsDataLoaded(accumulatedChatList.stream().filter(chat -> chat.getMemberIdList().contains(DataShare.getInstance().getCurrentUser().getId())).collect(Collectors.toList()));
                    }
                });
            }
        });
    }

    public void getChats(Chat.ChatType type, OnChatsDataListener listener) {
        DatabaseReference chatsRef = databaseReference.child(PRIVATE_CHATS_PATH);

        if (type == Chat.ChatType.GROUP) {
            chatsRef = databaseReference.child(GROUP_CHATS_PATH);
        }

        Task<DataSnapshot> dataSnapshotTask = chatsRef.get();
        dataSnapshotTask.addOnSuccessListener(dataSnapshot -> {
            List<Chat> chatList = new ArrayList<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Chat chat = null;
                if (type == Chat.ChatType.PRIVATE) {
                    chat = snapshot.getValue(PrivateChat.class);
                } else if (type == Chat.ChatType.GROUP) {
                    chat = snapshot.getValue(GroupChat.class);
                }

                if (chat != null) {
                    chatList.add(chat);
                }
            }
            listener.onChatsDataLoaded(chatList);
        });
    }

    public void addMessageEventListener(ChatInfo chatInfo, ChildEventListener childEventListener) {
        DatabaseReference messagesRef = databaseReference.child(PRIVATE_CHATS_PATH + "/" + chatInfo.id + "/messageList");
        if (chatInfo.type == Chat.ChatType.GROUP){
            messagesRef = databaseReference.child(GROUP_CHATS_PATH + "/" + chatInfo.id + "/messageList");
        }
        
        messagesRef.addChildEventListener(childEventListener);
    }

    public void removeMessageEventListener(ChatInfo chatInfo, ChildEventListener childEventListener) {
        DatabaseReference messagesRef = databaseReference.child(PRIVATE_CHATS_PATH + "/" + chatInfo.id + "/messageList");
        if (chatInfo.type == Chat.ChatType.GROUP){
            messagesRef = databaseReference.child(GROUP_CHATS_PATH + "/" + chatInfo.id + "/messageList");
        }

        messagesRef.removeEventListener(childEventListener);
    }

    public void sendMessage(ChatInfo chatInfo, Message msg, int index) {
        DatabaseReference messagesRef = databaseReference.child(PRIVATE_CHATS_PATH + "/" + chatInfo.id + "/messageList");
        if (chatInfo.type == Chat.ChatType.GROUP){
            messagesRef = databaseReference.child(GROUP_CHATS_PATH + "/" + chatInfo.id + "/messageList");
        }

        messagesRef.child(String.valueOf(index)).setValue(msg);
    }

    public void deleteMessage(ChatInfo chatInfo, Message msg) {
        msg.setDeleted(true);
        msg.setEdited(false);
        msg.setMessage("");
        storageService.deleteMedia(msg.getMediaUrl());
        msg.setLastEdited(Utils.generateCreatedAt());

        this.sendMessage(chatInfo, msg, msg.getIndex());
    }

    public void editMessage(ChatInfo chatInfo, Message editedMessage) {
        editedMessage.setEdited(true);
        editedMessage.setLastEdited(Utils.generateCreatedAt());

        this.sendMessage(chatInfo, editedMessage, editedMessage.getIndex());
    }

    public interface OnResultBool {
        void onResultBool(boolean result);
    }

    public interface OnUsersDataListener {
        void onUsersDataLoaded(List<User> userList);

        void onUsersDataError(Exception e);
    }

    public interface OnChatsDataListener {
        void onChatsDataLoaded(List<Chat> chatList);
    }
}
