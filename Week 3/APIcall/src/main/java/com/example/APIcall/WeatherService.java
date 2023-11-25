package com.example.APIcall;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


/* Author: Kimiya Fazlali - 1440906
 *  Created: 15 Nov 20223 */


@Service
public class WeatherService {

    @Value("${api.url}")
    private String apiUrl;

    public ResponseEntity<String> getWeather() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
        return response;
    }
}

