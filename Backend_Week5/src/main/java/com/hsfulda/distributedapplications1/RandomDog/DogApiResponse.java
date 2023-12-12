package com.hsfulda.distributedapplications1.RandomDog;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DogApiResponse {

    @JsonProperty("message")
    private String message;

    @JsonProperty("status")
    private String status;

    // Getters and setters (you can generate these using your IDE)
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
/*
  Jonas Wagner - 1315578 - 04.11.2023
 */
