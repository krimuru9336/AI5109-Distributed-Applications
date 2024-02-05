package com.example.whatsdown.requests;

import com.example.whatsdown.model.ChatMessage;
import com.example.whatsdown.model.CreateUser;
import com.example.whatsdown.model.UpdateMessage;
import com.example.whatsdown.model.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    /*
     * Jonas Wagner - 1315578
     */
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

    @Multipart
    @POST("/messages/send")
    Call<Void> sendMessage(@Part("senderId") RequestBody senderId,
                           @Part("receiverId") RequestBody receiverId,
                           @Part("content") RequestBody content,
                           @Part MultipartBody.Part media);

    @GET("/users")
    Call<List<User>> getUsers();

    @PUT("messages/edit/{id}")
    Call<ChatMessage> updateMessage(@Path("id") int messageId, @Body UpdateMessage message);

    @DELETE("messages/delete/{id}")
    Call<Void> deleteMessage(@Path("id") int messageId);

    @POST("/users")
    Call<User> createUser(@Body CreateUser user);
}
