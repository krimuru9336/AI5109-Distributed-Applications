package com.da.chitchat;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class SocketHelper {

    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder().url("ws://your-server-ip:3000").build();

    WebSocket webSocket = client.newWebSocket(request, new WebSocketListener() {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            // Handle the WebSocket connection open event
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            // Handle incoming messages from the server
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            // Handle the WebSocket connection closed event
        }
    });

}
