package com.example.whatsdown.requests;

import com.example.whatsdown.model.ChatMessage;
import com.example.whatsdown.model.CreateUser;
import com.example.whatsdown.model.SendMessageRequest;
import com.example.whatsdown.model.UpdateMessage;
import com.example.whatsdown.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
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

    @PUT("messages/edit/{id}")
    Call<ChatMessage> updateMessage(@Path("id") int messageId, @Body UpdateMessage message);

    @DELETE("messages/delete/{id}")
    Call<Void> deleteMessage(@Path("id") int messageId);

    @POST("/users")
    Call<User> createUser(@Body CreateUser user);
}
