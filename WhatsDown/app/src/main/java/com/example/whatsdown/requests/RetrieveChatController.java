package com.example.whatsdown.requests;

import static com.example.whatsdown.Constants.BASE_URL;

import com.example.whatsdown.model.ChatMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RetrieveChatController implements Callback<List<ChatMessage>> {
    private MessageCallback messageCallback;
    private List<ChatMessage> messages = new ArrayList<>();

    public RetrieveChatController(MessageCallback messageCallback) {
        this.messageCallback = messageCallback;
    }

    public void start(int userId1, int userId2) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL + "/messages/retrieve/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        String userId1String = String.valueOf(userId1);
        String userId2String = String.valueOf(userId2);

        Call<List<ChatMessage>> call = apiService.getChatMessages(userId1String, userId2String);
        call.enqueue(this);
    }

    public void startLastFetchedTimestamp(int userId1, int userId2) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL + "/messages/retrieve/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, -10);
        Date lastFetchedDate = calendar.getTime();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        String lastFetchedTimestamp = formatter.format(lastFetchedDate);

        System.out.println("Last fetched timestamp: " + lastFetchedTimestamp);

        String userId1String = String.valueOf(userId1);
        String userId2String = String.valueOf(userId2);

        Call<List<ChatMessage>> call = apiService.getChatMessagesLastFetchedTimestamp(userId1String, userId2String, lastFetchedTimestamp);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<List<ChatMessage>> call, Response<List<ChatMessage>> response) {
        if (response.isSuccessful()) {
            messages = response.body();
            if (messageCallback != null) {
                messageCallback.onMessagesReceived(messages);
            }
        } else {
            if (messageCallback != null) {
                messageCallback.onFailure(new Exception("Error: " + response.code()));
            }
        }
    }

    @Override
    public void onFailure(Call<List<ChatMessage>> call, Throwable t) {
        t.printStackTrace();
        if (messageCallback != null) {
            messageCallback.onFailure(t);
        }
    }
}
