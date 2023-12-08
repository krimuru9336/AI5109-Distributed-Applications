package com.rahulpatil.bmi.services;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CatService {
    private final RestTemplate restTemplate;

    public CatService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public String fetchFactsFromApi() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("https://cat-fact.herokuapp.com/facts",
                String.class);
        String jsonResponse = responseEntity.getBody();
        return jsonResponse;
    }
}

/*
 * Author: Rahul Patil
 * Matriculation Number: 1478745
 * Created: 09.11.2023
 */