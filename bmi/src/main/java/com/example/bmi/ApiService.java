/*
 * Author: Christian Jumtow
 * Created: 09.11.2023
 * MNr.: 1166358
 */

package com.example.bmi;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiService {
    private final String apiUrl = "https://meowfacts.herokuapp.com/?count=3";

    private final RestTemplate restTemplate;

    public ApiService() {
        this.restTemplate = new RestTemplate();
    }

    public ResponseEntity<String> makeApiCall() {
        try {
            // API-Call ausführen
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
            
            return response;
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .headers(e.getResponseHeaders())
                    .body(e.getResponseBodyAsString());
        }
    }
}
