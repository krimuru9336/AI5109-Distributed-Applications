package Adapters;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentOnAttachListener;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.SimpleExoPlayer;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.chitchat.AppGlideModule;
import com.example.chitchat.R;
import com.google.firebase.database.DatabaseReference;

import java.io.IOException;
import java.net.URI;
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

        View view = LayoutInflater.from(context).inflate(R.layout.message_view,parent,false);
        return  new MessageAdapterViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MessageAdapterViewHolder holder, int position) {
        Message m = messages.get(position);
        holder.editText = this.editText;
        holder.setDisplayData(m);

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class MessageAdapterViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        TextView textView_time;

        LinearLayout li;
        EditText editText;
        TextView senderName;
        Message message;

        ImageView imageView;
        int length = 0;
        PlayerView videoView;
        TextureView textureView;
         LinearLayout mediaLayout;
        MediaPlayer mediaPlayer;
        FrameLayout frameLayout;



        public void setDisplayData(Message msg){
            message = msg;
            if(message.getName().equals(AllMethods.name)){
                textView.setText(message.getText());
                senderName.setText("You");
                textView_time.setText(message.getTime());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.END;
                li.setLayoutParams(params);
                li.setBackgroundColor(Color.parseColor("#7B5088"));
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showCustomDialog(v,getBindingAdapterPosition(), (String) textView.getText());
                        return true;
                    }
                });

            }else {
                textView.setText(message.getText());
                textView_time.setText(message.getTime());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.START;
                senderName.setText(message.getName());
                li.setLayoutParams(params);
//            holder.delButton.setVisibility(View.GONE);
            }

            setImageVideoText();

        }

        private void setImageVideoText() {
            imageView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            textView_time.setVisibility(View.VISIBLE);
            mediaLayout.setVisibility(View.VISIBLE);
            switch (message.getType()){
                case GIF:
                    videoView.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    textView_time.setVisibility(View.VISIBLE);
                    mediaLayout.setVisibility(View.VISIBLE);
                    if (message.getMedia_url() != null && message.getMedia_url().startsWith("https://")) {
                        Glide.with(context)
                                .asGif()
                                .load(message.getMedia_url())
                                .into(imageView); // Replace imageView with the actual ImageView in your layout
                    }
                    break;
                case Image:
                    videoView.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    textView_time.setVisibility(View.VISIBLE);
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
                                .into(imageView); // Replace imageView with the actual ImageView in your layout
                    }
                    break;
                case Video:
                    imageView.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                    mediaLayout.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.VISIBLE);
                    textView_time.setVisibility(View.VISIBLE);
                    if (message.getMedia_url() != null && message.getMedia_url().startsWith("https://")) {
                        // Build the media item.

                        ExoPlayer player = new ExoPlayer.Builder(context).build();

                        MediaItem mediaItem = MediaItem.fromUri(message.getMedia_url());
                        // Set the media item to be played.
                        player.setMediaItem(mediaItem);
                        // Prepare the player.
                        player.prepare();
                        // Start the playback.
                        player.play();
                        videoView.setPlayer(player);
                        player.setPlayWhenReady(true);


//                        Uri videoUri = Uri.parse(message.getMedia_url());
//                        videoView.setVideoURI(videoUri);
//                        videoView.start();
                    }
                    break;
                case Text:
                    imageView.setVisibility(View.GONE);
                    videoView.setVisibility(View.GONE);
                    mediaLayout.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                    textView_time.setVisibility(View.VISIBLE);
                    break;
            }
        }



        public MessageAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.tvTitle);
            textView_time = itemView.findViewById(R.id.tvTime);
            senderName = itemView.findViewById(R.id.tvSender);
            li = itemView.findViewById(R.id.l1Message);
            imageView = itemView.findViewById(R.id.tvImage);
            videoView = itemView.findViewById(R.id.tvVideo);
            mediaLayout = itemView.findViewById(R.id.mediaLayout);
//            frameLayout = itemView.findViewById(R.id.frLayout);



//            videoView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(videoView.isPlaying()){
//                        length = videoView.getCurrentPosition();
//                        videoView.pause();
//                    }else {
//                        videoView.seekTo(length);
//                        videoView.start();
//                    }
//                }
//            });

//            delButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.i("0","Deleting message : "+messages.get(getBindingAdapterPosition()).getText());
//                    reference.child(messages.get(getBindingAdapterPosition()).getKey()).removeValue();
//                }
//            });
        }
    }

    public void showCustomDialog(View v, int i, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // Inflate the custom layout for the dialog
        View view = LayoutInflater.from(context).inflate(R.layout.hold_dialog, null);
        EditText editText1 = view.findViewById(R.id.menuEditText);

        editText1.setText(message);
        // Set the custom view to the builder
        builder.setView(view)
                .setTitle("Edit Message")
                .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle positive button click
                        reference.child(messages.get(i).getKey()).child("text").setValue(editText1.getText().toString());
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle negative button click
                        reference.child(messages.get(i).getKey()).removeValue();
                        dialog.dismiss();
                    }
                });

        // Create and show the dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }





}
