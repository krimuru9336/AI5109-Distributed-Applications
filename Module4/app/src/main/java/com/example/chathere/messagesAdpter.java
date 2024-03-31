package com.example.chathere;

import static com.example.chathere.chatWindow.reciverIImg;
import static com.example.chathere.chatWindow.senderImg;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class messagesAdpter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    String reciverUid,SenderUID;
    Context context;
    ArrayList<msgModelclass> messagesAdpterArrayList;
    int ITEM_SEND = 1;
    int ITEM_RECEIVE = 2;
    FirebaseDatabase database;
    FirebaseAuth firebaseAuth;

    String senderRoom;

    private ClickListener listener;
    public messagesAdpter(Context context, ArrayList<msgModelclass> messagesAdpterArrayList, String senderRoom,ClickListener listener) {
        this.context = context;
        this.messagesAdpterArrayList = messagesAdpterArrayList;
        this.senderRoom = senderRoom;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEND) {
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout, parent, false);
            return new senderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.reciver_layout, parent, false);
            return new receiverViewHolder(view);
        }
    }

    // Inside onBindViewHolder method of messagesAdapter
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        msgModelclass messages = messagesAdpterArrayList.get(position);
//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                new AlertDialog.Builder(context).setTitle("Delete")
//                        .setMessage("Are you sure you want to delete this message?")
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                                if (senderRoom != null) {
//                                    deleteMessage(messages);
//                                } else {
//                                    Toast.makeText(context, "Sender room is null", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                            }
//                        }).show();
//
//                return false;
//            }
//        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!((Activity) context).isFinishing()) { // Check if activity is not finishing
                    new AlertDialog.Builder(context)
                            .setTitle("Delete")
                            .setMessage("Are you sure you want to delete this message?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (senderRoom != null) {
                                        deleteMessage(messages);
                                    } else {
                                        Toast.makeText(context, "Sender room is null", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();
                }
                return false;
            }
        });

        if(holder instanceof  senderViewHolder){
            ((senderViewHolder) holder).editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (senderRoom != null) {
                        listener.onEdit(messages);
                    } else {
                        Toast.makeText(context, "Sender room is null", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

        if (holder instanceof senderViewHolder) {
            senderViewHolder viewHolder = (senderViewHolder) holder;
            viewHolder.msgtxt.setText(messages.getMessage());
            viewHolder.timestamp.setText(getFormattedTime(messages.getTimeStamp()));
            Picasso.get().load(senderImg).into(viewHolder.circleImageView);
        } else if (holder instanceof receiverViewHolder) {
            receiverViewHolder viewHolder = (receiverViewHolder) holder;
            viewHolder.msgtxt.setText(messages.getMessage());
            viewHolder.timestamp.setText(getFormattedTime(messages.getTimeStamp()));
            Picasso.get().load(reciverIImg).into(viewHolder.circleImageView);
        }
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
            return ITEM_RECEIVE;
        }
    }

    class senderViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView msgtxt, timestamp;
        ImageView editBtn, deleteBtn;

        public senderViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.profilerggg);
            msgtxt = itemView.findViewById(R.id.msgsendertyp);
            timestamp = itemView.findViewById(R.id.timestamp_sender);
            editBtn = itemView.findViewById(R.id.edit_btn);
//            deleteBtn = itemView.findViewById(R.id.delete_btn);

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Implement edit message functionality here
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        // Get the message from messagesAdpterArrayList using position
                        msgModelclass message = messagesAdpterArrayList.get(position);

                        // Implement edit message functionality as needed
                        // For example, you can show an edit dialog or start an edit activity
                    }
                }
            });

//            // Set click listener for delete button
//            deleteBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    // Implement delete message functionality here
//                    int position = getAdapterPosition();
//                    if (position != RecyclerView.NO_POSITION) {
//                        // Get the message from messagesAdpterArrayList using position
//                        msgModelclass message = messagesAdpterArrayList.get(position);
//
//                        // Show confirmation dialog before deleting the message
//                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                        builder.setTitle("Confirm Delete");
//                        builder.setMessage("Are you sure you want to delete this message?");
//                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                // User clicked Yes, delete the message
//                                // You can call a method to delete the message from the database or update the UI
//                                deleteMessage(message);
//                            }
//                        });
//                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                // User clicked No, dismiss the dialog
//                                dialogInterface.dismiss();
//                            }
//                        });
//                        builder.create().show();
//                    }
//                }
//            });
        }
    }

    private void deleteMessage(msgModelclass message) {
        // Get a reference to the database
        database = FirebaseDatabase.getInstance();
        DatabaseReference messageRef = database.getReference()
                .child("chats")
                .child(senderRoom)
                .child("messages")
                .child(message.getMessageKey());

        // Remove the message from the database

        Log.e("MEES","SS"+message.getSenderid());

        messageRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Message deleted successfully
                    // You can also update the UI if needed
                    Toast.makeText(context, "Message deleted "+task.toString(), Toast.LENGTH_LONG).show();
                } else {
                    // Failed to delete message
                    Toast.makeText(context, "Failed to delete message", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    class receiverViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView msgtxt, timestamp;
        public receiverViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.pro);
            msgtxt = itemView.findViewById(R.id.recivertextset);
            timestamp = itemView.findViewById(R.id.timestamp_receiver);
        }
    }

    private String getFormattedTime(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public interface ClickListener{
        void onEdit(msgModelclass message);
    }


}
