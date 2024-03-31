package com.example.mychatapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychatapplication.R;
import com.example.mychatapplication.activities.ConversationActivity;
import com.example.mychatapplication.models.Chat;
import com.example.mychatapplication.utilities.Constants;
import com.example.mychatapplication.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {

    private Context context;
    private List<Chat> chatList;
    private FirebaseFirestore db;
    private PreferenceManager preferenceManager;


    public ChatListAdapter(Context context, List<Chat> chatList) {
        this.context = context;
        this.chatList = chatList;
        db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(context);
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_container_recent_conversion, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);

        Log.v("MainActivity","onBindViewHolder ");

        // Check if it's a group chat
        if (chat.getType().equals("group")) {
            holder.textViewChatName.setText(chat.getName());
            holder.imageView.setImageBitmap(getConversionImage(chat.getImage())); // Placeholder image
        } else { // One-to-one chat
            // Get the other user ID
            String otherUserId = getOtherUserId(chat);

            // Fetch the other user's information and update the UI
            fetchOtherUserInfo(otherUserId, holder.textViewChatName, holder.imageView);
        }

        // Bind other chat information
        String lastMsg=chat.getLastMessage();
        holder.textViewLastMessage.setText(lastMsg);
        //holder.timestampTextView.setText(chat.getTimestamp());

        // Set click listener for chat item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle chat item click
                openConversationActivity(chat);
            }
        });
    }

    private String getOtherUserId(Chat chat) {
        for (String participant : chat.getParticipants()) {
            if (!participant.equals(preferenceManager.getString(Constants.KEY_USER_ID))) { // Assuming currentUser is the current user object
                return participant;
            }
        }
        return null;
    }

    private void fetchOtherUserInfo(String userId, TextView nameTextView, ImageView imageImageView) {

        Log.v("MainActivity","fetchOtherUserInfo "+userId);
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String userName = documentSnapshot.getString("name");
                    String userProfileImageUrl = documentSnapshot.getString("image");

                    // Update UI with user information
                    nameTextView.setText(userName);
                    // Load user image using Glide or Picasso
                   imageImageView.setImageBitmap(getConversionImage(userProfileImageUrl));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void openConversationActivity(Chat chat) {
        Intent intent = new Intent(context, ConversationActivity.class);
        intent.putExtra("chatId", chat.getId());
        intent.putExtra("otherUserId", getOtherUserId(chat)); // Pass other user ID
        intent.putExtra("chatType", chat.getType());
        if (chat.getType().equals("group")) {
            intent.putExtra("groupName", chat.getName());
            intent.putExtra("groupImage", chat.getImage());
        }
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewChatName;
        private TextView textViewLastMessage;
        private ImageView imageView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageProfile);
            textViewChatName = itemView.findViewById(R.id.textName);
            textViewLastMessage = itemView.findViewById(R.id.textRecentMessage);

        }

    }


    private Bitmap getConversionImage(String encodedImage){
        byte[] bytes= Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0, bytes.length);
    }

    private boolean isVideoUrl(String messageContent) {
        // Add logic to determine if the message contains a video URL
        return messageContent.startsWith("http"); // For demonstration, check if it starts with "http"
    }

}
