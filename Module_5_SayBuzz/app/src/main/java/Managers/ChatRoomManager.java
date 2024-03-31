package Managers;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import Models.AllMethods;
import Models.ChatRoom;
import Models.ChatRoomRes;
import Models.User;

public class ChatRoomManager {

    private DatabaseReference chatRoomsRef;
    private  DatabaseReference UserRef;

    public ChatRoomManager() {
        // Initialize Firebase Database reference
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        chatRoomsRef = firebaseDatabase.getReference("chat_rooms");
        UserRef = firebaseDatabase.getReference("Users");
    }

    public void searchOrCreateChatRoom(String enteredUsername, Consumer<ChatRoom> success) {
        chatRoomsRef.addValueEventListener(

        new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isFound = false;
                for(DataSnapshot sp : dataSnapshot.getChildren()){
                    ChatRoomRes room = sp.getValue(ChatRoomRes.class);
                    assert room != null;
                    room.setKey(sp.getKey());
                    for (ChatRoom.ChatRoomUser user: room.users
                    ) {
                        if(user.getName().equals(enteredUsername)){
                            success.accept(room.getRoom());
                            isFound = true;
                        }
                    }

                }
                if(!isFound)
                    createNewChatRoom(enteredUsername, success);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }


    private void createNewChatRoom(String enteredUsername, Consumer<ChatRoom> success) {
        // Create a new chat room

        String roomId = chatRoomsRef.push().getKey();
        ChatRoom room = new ChatRoom(enteredUsername);
        room.setKey(roomId);
        AllMethods.chatroomKey = roomId;

        room.users.add(new ChatRoom.ChatRoomUser(AllMethods.name,AllMethods.uId));

        UserRef.orderByChild("name").equalTo(enteredUsername).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot s:snapshot.getChildren()
                    ) {
                        room.users.add(new ChatRoom.ChatRoomUser(enteredUsername,s.getKey()));

                        assert roomId != null;
                        chatRoomsRef.child(roomId).setValue(room);

                        success.accept(room);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void createNewChatGroup(ArrayList<String> groupMembers, String groupName, Consumer<ChatRoom> success){
        String roomId = chatRoomsRef.push().getKey();
        ChatRoom room = new ChatRoom();
        room.setKey(roomId);
        room.setGroupName(groupName);
        AllMethods.chatroomKey = roomId;
        room.users.add(new ChatRoom.ChatRoomUser(AllMethods.name,AllMethods.uId));

        for (String name:groupMembers
             ) {
            UserRef.orderByChild("name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        for (DataSnapshot s:snapshot.getChildren()
                        ) {
                            room.users.add(new ChatRoom.ChatRoomUser(name,s.getKey()));
                            if(groupMembers.indexOf(name) == groupMembers.size() - 1){
                                assert roomId != null;
                                chatRoomsRef.child(roomId).setValue(room);

                                success.accept(room);
                            }

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

}

class  UserRes{
    public Map<String, User> getUser() {
        return user;
    }
    public  UserRes(){}
    public UserRes(Map<String, User> user) {
        this.user = user;
    }
    public void setUser(Map<String, User> user) {
        this.user = user;
    }
    Map<String,User> user = new HashMap<>();
}

