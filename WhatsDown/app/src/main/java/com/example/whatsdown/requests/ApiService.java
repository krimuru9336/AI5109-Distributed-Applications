package com.example.whatsdown.requests;

import com.example.whatsdown.model.ChatMessage;
import com.example.whatsdown.model.SendMessageRequest;
import com.example.whatsdown.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @GET("/messages/retrieve")
    Call<List<ChatMessage>> getChatMessages(
            @Query("user1") String user1,
            @Query("user2") String user2
    );

    @GET("/messages/retrieve")
    Call<List<ChatMessage>> getChatMessagesLastFetchedTimestamp(
            @Query("user1") String user1,
            @Query("user2") String user2,
            @Query("lastFetchedTimestamp") String lastFetchedTimestamp
    );

    @POST("/messages/send")
    Call<Void> sendMessage(@Body SendMessageRequest message);

    @GET("/users")
    Call<List<User>> getUsers();
}
