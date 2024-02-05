package com.example.BMI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ApiService {
	
	private static final Logger logger = LoggerFactory.getLogger(ApiService.class);

    private final WebClient webClient;

    public ApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://ron-swanson-quotes.herokuapp.com/v2/quotes").build();
    }

    public String callApi() {
        // Use WebClient to make a GET request
        String responseBody = webClient.get()
                .uri("/api-call")
                .retrieve()
                .onStatus(
                        status -> !status.isError(),
                        response -> {
                            logger.info("API Request successful with status code: {}", response.statusCode());
                            return Mono.empty();
                        }
                )
                .bodyToMono(String.class)
                .doOnSuccess(response -> {
                	logger.info("API Response: {}", response);
                })
                .block(); // block() for simplicity, consider using subscribe() in a reactive application

        return responseBody;
    }
}

//Author : Bhavin Vadhiya 
//Created on : 16/11/2023
