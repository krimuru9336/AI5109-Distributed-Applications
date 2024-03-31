package com.example.mychatapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mychatapplication.R;
import com.example.mychatapplication.activities.VideoActivity;
import com.example.mychatapplication.listeners.ConversionListener;
import com.example.mychatapplication.models.Message;
import com.example.mychatapplication.utilities.Constants;
import com.example.mychatapplication.utilities.PreferenceManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;



public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    static ConversionListener listener;
    private List<Message> messages;
    private Context context;
    private PreferenceManager preferenceManager;

    public MessageAdapter(Context context, List<Message> messages,ConversionListener listener) {
        this.context = context;
        this.messages = messages;
        preferenceManager=new PreferenceManager(context);
        this.listener=listener;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == VIEW_TYPE_SENT) {
            view = inflater.inflate(R.layout.item_container_sent_message, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.item_container_received_message, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        if (holder instanceof SentMessageViewHolder) {
            SentMessageViewHolder holder2=((SentMessageViewHolder) holder);

            String downloadUrl=message.getContent();

            if(message.getContentType().equals("text")){
                holder2.contentText.setVisibility(View.VISIBLE);
                holder2.thumbnail.setVisibility(View.GONE);
                holder2.contentText.setText(message.getContent());

            }else{
                if (message.getContentType().equals("video")) {
                    holder2.thumbnail.setImageResource(R.drawable.ic_play);
                    holder2.thumbnail.setBackgroundResource(R.drawable.bg_border);

                } else {
                    Glide.with(context).load(downloadUrl).into(holder2.thumbnail);
                    holder2.thumbnail.setBackground(null);
                }
                holder2.thumbnail.setVisibility(View.VISIBLE);
                holder2.contentText.setVisibility(View.GONE);
            }
            holder2.timestampText.setText(formatTimestamp(message.getTimestamp()));

            holder2.thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playVideo(message);
                }
            });
        } else if (holder instanceof ReceivedMessageViewHolder) {
            ReceivedMessageViewHolder holder1=((ReceivedMessageViewHolder) holder);
            String downloadUrl=message.getContent();
            if(message.getContentType().equals("text")){
                holder1.contentText.setVisibility(View.VISIBLE);
                holder1.thumbnail.setVisibility(View.GONE);
                holder1.contentText.setText(message.getContent());

            }else{
                if (message.getContentType().equals("video")) {
                    holder1.thumbnail.setImageResource(R.drawable.ic_play);
                    holder1.thumbnail.setBackgroundResource(R.drawable.bg_border);
                } else {
                    Glide.with(context).load(downloadUrl).into(holder1.thumbnail);
                    holder1.thumbnail.setBackground(null);
                }
                holder1.thumbnail.setVisibility(View.VISIBLE);
                holder1.contentText.setVisibility(View.GONE);
            }

            holder1.timestampText.setText(formatTimestamp(message.getTimestamp()));
            if(message.getSenderProfileImageUrl()!=null) {
                byte[] bytes = Base64.decode(message.getSenderProfileImageUrl(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder1.imageView.setImageBitmap(bitmap);
            }
            holder1.thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playVideo(message);
                }
            });
        }

    }

    private void playVideo(Message message){
        if(message.getContentType().equals("video")) {
            Intent intent = new Intent(context, VideoActivity.class);
            intent.putExtra("url", message.getContent());
            context.startActivity(intent);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        return message.getSenderId().equals(preferenceManager.getString(Constants.KEY_USER_ID)) ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class SentMessageViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        TextView contentText, timestampText;
        ImageView imageView,thumbnail;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            contentText = itemView.findViewById(R.id.textMessage);
            timestampText = itemView.findViewById(R.id.textDateTime);
            imageView = itemView.findViewById(R.id.imageProfile);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.action_edit_message) {
                        listener.onEdit(getAdapterPosition());
                        return true;
                    } else if (item.getItemId() == R.id.action_delete_message) {
                        listener.onDelete(getAdapterPosition());

                        return true;
                    }
                    return false;
                }
            });
            MenuInflater inflater = popupMenu.getMenuInflater();
            inflater.inflate(R.menu.message_options_menu, popupMenu.getMenu());
            popupMenu.show();
            return true;
        }

    }

    public static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView contentText, senderNameText, timestampText;
        ImageView imageView,thumbnail;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            contentText = itemView.findViewById(R.id.textMessage);
            timestampText = itemView.findViewById(R.id.textDateTime);
            imageView=itemView.findViewById(R.id.imageProfile);
            thumbnail = itemView.findViewById(R.id.thumbnail);

        }


    }

    private static String formatTimestamp(Date timestamp) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dateFormat.format(timestamp);
    }


}
