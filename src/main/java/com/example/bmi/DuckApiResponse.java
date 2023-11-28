package com.example.bmi;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DuckApiResponse {
    @JsonProperty("message")
    private String message;

    @JsonProperty("url")
    private String url;

    public DuckApiResponse() {
    }

    public String getMessage() {
        return message;
    }

    public String getUrl() {
        return url;
    }
}
