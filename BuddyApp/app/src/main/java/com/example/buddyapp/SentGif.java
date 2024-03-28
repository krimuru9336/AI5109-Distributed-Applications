package com.example.buddyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;

import pl.droidsonroids.gif.GifImageView;

public class SentGif extends AppCompatActivity {
    private ImageView gifImageView;
    private Button sendGifButton;
    private TextView dontPressTextView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_gif);

        gifImageView = findViewById(R.id.gifImageView);
        sendGifButton = findViewById(R.id.btn_sendgif_group);
        dontPressTextView = findViewById(R.id.tv_dont_group);
        progressBar = findViewById(R.id.pb_sendgif_group);

        sendGifButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show progress bar and hide other views
                progressBar.setVisibility(View.VISIBLE);
                dontPressTextView.setVisibility(View.VISIBLE);
                gifImageView.setVisibility(View.INVISIBLE);
                sendGifButton.setVisibility(View.INVISIBLE);

                // Simulate uploading process (replace with actual upload logic)
                // For demonstration purposes, I'm using a simple delay
                uploadGif();
            }
        });
    }

    private void uploadGif() {
        // Simulate uploading process
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Simulate a 5-second delay
                    Thread.sleep(5000);

                    // After the upload is finished, hide progress bar and show other views
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.INVISIBLE);
                            dontPressTextView.setVisibility(View.INVISIBLE);
                            gifImageView.setVisibility(View.VISIBLE);
                            sendGifButton.setVisibility(View.VISIBLE);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}