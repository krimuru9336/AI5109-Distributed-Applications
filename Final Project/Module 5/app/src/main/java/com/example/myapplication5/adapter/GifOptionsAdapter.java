package com.example.myapplication5.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.myapplication5.R;

import java.util.List;

public class GifOptionsAdapter extends RecyclerView.Adapter<GifOptionsAdapter.ViewHolder> {

    public interface OnGifClickListener {
        void onGifClick(String gifUrl);
    }


    private List<String> gifUrls;

    private OnGifClickListener listener;

    public GifOptionsAdapter(List<String> gifUrls, OnGifClickListener listener) {
        this.gifUrls = gifUrls;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gif_option, parent, false);
        return new ViewHolder(view, listener); // Pass the listener here
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String gifUrl = gifUrls.get(position);
        Glide.with(holder.itemView.getContext())
                .load(gifUrl)
                .into(holder.gifImageView);
    }

    @Override
    public int getItemCount() {
        return gifUrls.size();
    }

    public void updateGifOptions(List<String> newGifUrls) {
        this.gifUrls.clear();
        this.gifUrls.addAll(newGifUrls);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView gifImageView;

        private OnGifClickListener listener;

        public ViewHolder(@NonNull View itemView, OnGifClickListener listener) {
            super(itemView);
            gifImageView = itemView.findViewById(R.id.gif_option_imageview);
            this.listener = listener;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                        listener.onGifClick(gifUrls.get(getAdapterPosition()));
                    }

                }
            });
        }




    }
}
