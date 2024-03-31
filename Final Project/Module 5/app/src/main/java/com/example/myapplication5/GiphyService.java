package com.example.myapplication5;

import java.util.List;
import java.util.ArrayList;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;



public class GiphyService {
    private static final String API_KEY = "i6sMGOB5uFCew7256jt3ilZa8KDhBQrL";

    public void searchGifs(String query, Callback callback) {
        String url = "https://api.giphy.com/v1/gifs/search?api_key=" + API_KEY + "&q=" + query + "&limit=5&offset=0&rating=g&lang=en";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(callback);
    }


    public List<String> parseJsonResponse(String jsonResponse) {
        List<String> gifUrls = new ArrayList<>();
        JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonArray dataArray = jsonObject.getAsJsonArray("data");

        for (JsonElement element : dataArray) {
            JsonObject gifObject = element.getAsJsonObject();
            JsonObject imagesObject = gifObject.getAsJsonObject("images");
            JsonObject fixedHeightObject = imagesObject.getAsJsonObject("fixed_height");
            String url = fixedHeightObject.get("url").getAsString();
            gifUrls.add(url);
        }

        return gifUrls;
    }


}
