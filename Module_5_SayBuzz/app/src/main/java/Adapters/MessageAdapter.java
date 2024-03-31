package Adapters;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.chitchat.R;
import com.google.firebase.database.DatabaseReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



import java.util.ArrayList;

import Models.AllMethods;
import Models.Message;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageAdapterViewHolder> {

    Context context;
    ArrayList<Message> messages;
    DatabaseReference reference;
    EditText editText;

    public  MessageAdapter(Context c, ArrayList<Message> msgs,DatabaseReference db){
        context = c;
        messages = msgs;
        reference = db;
    }
    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public void onViewRecycled(@NonNull MessageAdapterViewHolder holder) {
        super.onViewRecycled(holder);
        releasePlayer(holder);
    }

    private void releasePlayer(MessageAdapterViewHolder holder) {
        if (holder.videoView.getPlayer() != null) {
            holder.videoView.getPlayer().release();
            holder.videoView.setPlayer(null);
        }
    }

    @NonNull
    @Override
    public MessageAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.message_layout,parent,false);
        return  new MessageAdapterViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MessageAdapterViewHolder holder, int position) {
        Message m = messages.get(position);
        holder.editText = this.editText;
        holder.setDisplayData(m);

    }



    public class MessageAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        TextView timeTextView;
        int length = 0;
        PlayerView videoView;
        LinearLayout mediaLayout;
        LinearLayout linearLayout;
        EditText editText;
        TextView senderName;
        Message message;

        ImageView imageView;



        private void setImageVideoText() {
            imageView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            timeTextView.setVisibility(View.VISIBLE);
            mediaLayout.setVisibility(View.VISIBLE);
            switch (message.getType()){
                case GIF:
                    videoView.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    timeTextView.setVisibility(View.VISIBLE);
                    mediaLayout.setVisibility(View.VISIBLE);
                    if (message.getMedia_url() != null && message.getMedia_url().startsWith("https://")) {
                        Glide.with(context)
                                .asGif()
                                .load(message.getMedia_url())
                                .into(imageView);
                    }
                    break;
                case Image:
                    videoView.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    timeTextView.setVisibility(View.VISIBLE);
                    mediaLayout.setVisibility(View.VISIBLE);
                    if (message.getMedia_url() != null && message.getMedia_url().startsWith("https://")) {
                        Glide.with(context)
                                .load(message.getMedia_url()).listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        Log.i("e",e.getMessage());
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        return false;
                                    }
                                })
                                .into(imageView);
                    }
                    break;
                case Video:
                    imageView.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                    mediaLayout.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.VISIBLE);
                    timeTextView.setVisibility(View.VISIBLE);
                    if (message.getMedia_url() != null && message.getMedia_url().startsWith("https://")) {

                        ExoPlayer player = new ExoPlayer.Builder(context).build();

                        MediaItem mediaItem = MediaItem.fromUri(message.getMedia_url());
                        player.setMediaItem(mediaItem);
                        player.prepare();
                        player.play();
                        videoView.setPlayer(player);
                        player.setPlayWhenReady(true);

                    }
                    break;
                case Text:
                    imageView.setVisibility(View.GONE);
                    videoView.setVisibility(View.GONE);
                    mediaLayout.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                    timeTextView.setVisibility(View.VISIBLE);
                    break;
            }
        }



        public MessageAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.MessageLayout);
            imageView = itemView.findViewById(R.id.imageView);
            videoView = itemView.findViewById(R.id.videoView);
            mediaLayout = itemView.findViewById(R.id.mediaLayout);
            textView = itemView.findViewById(R.id.titleView);
            timeTextView = itemView.findViewById(R.id.timeView);
            senderName = itemView.findViewById(R.id.senderView);
        }

        public void setDisplayData(Message msg){
            message = msg;
            if(message.getName().equals(AllMethods.name)){
                textView.setText(message.getText());
                senderName.setText("You");
                timeTextView.setText(message.getTime());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.END;
                linearLayout.setLayoutParams(params);
                linearLayout.setBackgroundColor(Color.parseColor("#7B5088"));
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showCustomDialog(v,getBindingAdapterPosition(), (String) textView.getText());
                        return true;
                    }
                });

            }else {
                textView.setText(message.getText());
                timeTextView.setText(message.getTime());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.START;
                senderName.setText(message.getName());
                linearLayout.setLayoutParams(params);
            }

            setImageVideoText();

        }
    }

    public void showCustomDialog(View v, int i, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.edit_text_layout, null);
        EditText editText1 = view.findViewById(R.id.menuEditText);

        editText1.setText(message);
        builder.setView(view)
                .setTitle("Edit Message")
                .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive click
                        reference.child(messages.get(i).getKey()).child("text").setValue(editText1.getText().toString());
//                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative click
                        reference.child(messages.get(i).getKey()).removeValue();
                        dialog.dismiss();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }





}
