    package com.example.messenger;





    import static com.example.messenger.chatwindo.reciverIImg;
    import static com.example.messenger.chatwindo.senderImg;

    import android.app.AlertDialog;
    import android.content.Context;
    import android.content.DialogInterface;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.recyclerview.widget.RecyclerView;

    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.Query;
    import com.google.firebase.database.ValueEventListener;
    import com.google.firebase.storage.FirebaseStorage;
    import com.google.firebase.storage.StorageReference;
    import com.squareup.picasso.Picasso;

    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Date;
    import java.util.HashMap;
    import java.util.Locale;
    import java.util.Map;

    import de.hdodenhof.circleimageview.CircleImageView;

    public class messagesAdpter extends RecyclerView.Adapter {
        Context context;
        ArrayList<msgModelclass> messagesAdpterArrayList;
        String SenderUID,senderRoom,reciverRoom;


        FirebaseDatabase database;
        FirebaseAuth firebaseAuth;
        int ITEM_SEND=1;
        int ITEM_RECIVE=2;


        public messagesAdpter(Context context, ArrayList<msgModelclass> messagesAdpterArrayList) {
            this.context = context;
            this.messagesAdpterArrayList = messagesAdpterArrayList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == ITEM_SEND){
                View view = LayoutInflater.from(context).inflate(R.layout.sender_layout, parent, false);
                return new senderVierwHolder(view);
            }else {
                View view = LayoutInflater.from(context).inflate(R.layout.reciver_layout, parent, false);
                return new reciverViewHolder(view);
            }

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            msgModelclass messages = messagesAdpterArrayList.get(position);
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {


                @Override
                public boolean onLongClick(View view) {
                    new AlertDialog.Builder(context).setTitle("Delete")
                            .setMessage("Sure you want to delete this message?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String ChatID;
                                    if (messages.getSenderid() != null) {

                                        FirebaseDatabase database = FirebaseDatabase.getInstance();

                                        // Reference to the messages node
                                        DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference("chats");

                                        chatReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                                                    // Extract the chat ID using getKey()
                                                    String chatId = chatSnapshot.getKey();

                                                    // Print or use the extracted chat ID as needed
                                                    Log.d("Chat ID: " , chatId);

                                                    DatabaseReference messagesRef = database.getReference("chats/"+chatId+"/messages");

                                                    messagesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                                                                // Retrieve each message ID dynamically
                                                                String messageId = messageSnapshot.getKey();
                                                                msgModelclass messageValue =  messageSnapshot.getValue(msgModelclass.class);

                                                                Log.d("Retrieved Message ID: " , messageValue.getMessage());
                                                                Log.d("My Message ID: " , messages.getMessage());
                                                                if(messageValue.getMessage().equals(messages.getMessage())){
                                                                    Log.d("Into Delete " ,chatId);
                                                                    deleteMessage(chatId, messageId);

                                                                }

                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                            System.err.println("Error retrieving message IDs: " + databaseError.getMessage());
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                // Handle errors if any
                                                System.err.println("Error reading data: " + databaseError.getMessage());
                                            }
                                        });


                                        dialogInterface.dismiss();


                                    }
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();

                    return false;
                }
            });
            if (holder.getClass()==senderVierwHolder.class){
                senderVierwHolder viewHolder = (senderVierwHolder) holder;
                String formattedDate = formatDate(messages.getTimeStamp());
                viewHolder.msgtxt.setText(formattedDate);
                viewHolder.timestp.setText(messages.getMessage());
                Picasso.get().load(senderImg).into(viewHolder.circleImageView);
            }else { reciverViewHolder viewHolder = (reciverViewHolder) holder;

                String formattedDate = formatDate(messages.getTimeStamp());
                viewHolder.msgtxt.setText(formattedDate);
                viewHolder.timestp.setText(messages.getMessage());
                Log.d("Timestamp", "Sender Timestamp: " + messages.getMessage()+"hello");
                Picasso.get().load(reciverIImg).into(viewHolder.circleImageView);


            }
        }

        private String formatDate(long timestamp) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date date = new Date(timestamp);
            String formattedTime = sdf.format(date);

            // Log the formatted timestamp
            Log.d("FormattedTime", "Formatted Time: " + formattedTime);
            return formattedTime;
        }

        private static void deleteMessage(String chatId, String messageId) {
            // Delete the message using the provided chatId and messageId
            FirebaseDatabase.getInstance().getReference("chats/" + chatId + "/messages/" + messageId)
                    .removeValue()
                    .addOnSuccessListener(aVoid -> System.out.println("Message deleted successfully."))
                    .addOnFailureListener(e -> System.err.println("Error deleting message: " + e.getMessage()));
        }

        @Override
        public int getItemCount() {
            return messagesAdpterArrayList.size();
        }

        @Override
        public int getItemViewType(int position) {
            msgModelclass messages = messagesAdpterArrayList.get(position);
            if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderid())) {
                return ITEM_SEND;
            } else {
                return ITEM_RECIVE;
            }
        }

        class  senderVierwHolder extends RecyclerView.ViewHolder {
            CircleImageView circleImageView;
            TextView msgtxt,timestp;
            public senderVierwHolder(@NonNull View itemView) {
                super(itemView);
                circleImageView = itemView.findViewById(R.id.profilerggg);
                msgtxt = itemView.findViewById(R.id.msgsendertyp);
                timestp = itemView.findViewById(R.id.timestrap);

            }
        }
        class reciverViewHolder extends RecyclerView.ViewHolder {
            CircleImageView circleImageView;
            TextView msgtxt,timestp;
            public reciverViewHolder(@NonNull View itemView) {
                super(itemView);
                circleImageView = itemView.findViewById(R.id.pro);
                msgtxt = itemView.findViewById(R.id.recivertextset);
                timestp = itemView.findViewById(R.id.timestrap);
            }
        }
    }
