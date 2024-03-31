package com.example.myapplication5;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

//import com.example.myapplication5.adapter.ChatsAdapter;
import com.example.myapplication5.utils.AndroidUtil;
import com.example.myapplication5.utils.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Field;
public class ChatsFragment extends Fragment {
    private RecyclerView recyclerView;
    private ChatsAdapter adapter;
    private List<Object> combinedItems = new ArrayList<>();

    private Map<String, String> userIdToUsernameMap = new HashMap<>();

//    private Map<String, String> groupIdToMemberMap = new HashMap<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView = view.findViewById(R.id.recyler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatsAdapter(combinedItems, getContext());
        recyclerView.setAdapter(adapter);

        combinedItems.clear();

        fetchChats();
        fetchGroupChats();

        System.out.println("combinedItems after fetch" + combinedItems);

        return view;
    }
    private void fetchChats() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            // Fetch chatrooms where the current user is part of
            db.collection("chatrooms")
                    .whereArrayContains("userIds", currentUserId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Convert DocumentSnapshot to ChatroomModel
                                ChatroomModel chatroom1 = document.toObject(ChatroomModel.class);
                                combinedItems.add(chatroom1);
                                System.out.println("in fetch chats - combined items, doc" + combinedItems + chatroom1.getUserIds());


                                // Fetch usernames for each user ID in the chatroom
                                for (String userId : chatroom1.getUserIds()) {
                                    if (!userId.equals(currentUserId)) {
                                        fetchUsername(userId);
                                    }
                                }


                            }
                            // Notify adapter of data changes
                            adapter.notifyDataSetChanged();
                            System.out.println("Successful fetching chats" + combinedItems );
                        } else {

                            System.out.println("Error fetching chatrooms" );
                        }
                    });
        }
    }


    private void fetchGroupChats() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            // Fetch group chatrooms where the current user is part of
            db.collection("groupChats")
                    .whereArrayContains("memberIDs", currentUserId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Convert DocumentSnapshot to ChatroomModel
                                GroupChatroomModel chatroom = document.toObject(GroupChatroomModel.class);
                                combinedItems.add(chatroom);

                            }
                            // Notify adapter of data changes
                            adapter.notifyDataSetChanged();
                            System.out.println("Successful fetching group chats");



                        } else {
                            System.out.println("error in fetching gchatrooms");
                        }
                    });
        }
    }




    private void fetchUsername(String userId) {
        FirebaseUtil.getUsernameFromUserId(userId, new FirebaseUtil.UsernameCallback() {
            @Override
            public void onUsernameFound(String username) {
                userIdToUsernameMap.put(userId, username); // Store the username
                adapter.notifyDataSetChanged(); // Notify adapter to refresh the UI
            }

            @Override
            public void onUsernameNotFound() {
                // Handle case where username is not found
            }
        });
    }





    private class ChatsAdapter extends RecyclerView.Adapter<ChatViewHolder> {
        private List<Object> items;

        private Context context; // Define a Context variable

        public ChatsAdapter(List<Object> items, Context context) { // Update constructor to accept Context
            this.items = items;
            this.context = context; // Initialize the Context variable
        }

        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_chats_row, parent, false);
            return new ChatViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
            Object item = items.get(position);



            System.out.println("items list  " + items);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            String currentUserId = currentUser.getUid();

            if (item instanceof ChatroomModel) {
                ChatroomModel chatroom = (ChatroomModel) item;
                List<String> userIds = new ArrayList<>(chatroom.getUserIds()); // Create a copy of the list
                userIds.remove(currentUserId);

                // Check if there are other users in the chatroom
                if (userIds.size() > 1) {

                    return;
                } else if (userIds.isEmpty()) {

                    return;
                }

                String otherUserId = userIds.get(0);
                String username = userIdToUsernameMap.get(otherUserId); // Retrieve username from HashMap

                if (username !=null) {
                    holder.userNameText.setText(username);

                    holder.lastMessageTimeText.setText(FirebaseUtil.timestampToString(chatroom.lastMessageTimestamp));  //set timestamp of last message

//                    holder.userNameText.setText("User not found");
                }


                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(v.getContext(), ChatActivity.class); // Use the context from the view
                    intent.putExtra("userId", otherUserId); // Pass the user ID
                    intent.putExtra("username", username); // Pass the username
                    intent.putExtra("isGroupChat", false);
                    v.getContext().startActivity(intent); // Start the activity
                });

            }
            //for groups
            else if (item instanceof GroupChatroomModel) {
                GroupChatroomModel groupChatroom = (GroupChatroomModel) item;

                String groupName = groupChatroom.getGroupName();

//                System.out.println("in fragments -->>   groupChatroom\n" + groupChatroom.getId() +   );



                String groupID = groupChatroom.getId();

                System.out.println("groupid" + groupID);



                List<Map<String, String>> members = groupChatroom.getMembers();

                List<String> memberIDs = groupChatroom.getMemberIDs();

                System.out.println("grp members \t"+members);


                holder.userNameText.setText(groupName);
                holder.lastMessageTimeText.setText(" ");


                holder.itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(v.getContext(), ChatActivity.class); // Use the context from the view
                    intent.putExtra("id", groupID); // Pass the user ID
                    intent.putExtra("groupName", groupName); // Pass the username
                    intent.putExtra("isGroupChat", true);

                    // Convert the members list to a Serializable object
                    ArrayList<HashMap<String, String>> serializableMembers = new ArrayList<>();
                    for (Map<String, String> member : members) {
                        serializableMembers.add(new HashMap<>(member));
                    }
                    intent.putExtra("members", serializableMembers);

                    // Assuming memberIDs is a List<String> that you have access to
                    ArrayList<String> memberIDsList = new ArrayList<>(memberIDs);
                    intent.putStringArrayListExtra("memberIDs", memberIDsList);

                    System.out.println("in passing activity->"+ members + memberIDsList);

                    v.getContext().startActivity(intent); // Start the activity
                });



            }


        }

        @Override
        public int getItemCount() {
            return items.size();
        }


    }


    public class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView userNameText;
        TextView lastMessageTimeText;


        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);


            userNameText = itemView.findViewById(R.id.user_name_text);
            lastMessageTimeText = itemView.findViewById(R.id.last_message_time_text);


        }



    }
}
