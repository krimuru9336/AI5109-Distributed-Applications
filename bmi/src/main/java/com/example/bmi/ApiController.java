//Simon Keller, Matrikelnummer 1165562, 09.11.2023
package com.example.bmi;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@RestController
public class ApiController {
    private final WebClient webClient;
    public ApiController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://dog-api.kinduff.com").build();
    }
    @GetMapping("/fetch-dogs")
    public Mono<Object> fetchData() {
        return  webClient.get()
            .uri("/api/facts")
            .retrieve()
            .toEntity(String.class)
            .map(responseEntity -> {
                HttpHeaders headers = responseEntity.getHeaders();
                HttpStatusCode statusCode = responseEntity.getStatusCode();
                String responseBody = responseEntity.getBody();

                return headers.toString()+"<br>"+statusCode.toString()+"<br>"+responseBody.toString();
            });
    }
}
