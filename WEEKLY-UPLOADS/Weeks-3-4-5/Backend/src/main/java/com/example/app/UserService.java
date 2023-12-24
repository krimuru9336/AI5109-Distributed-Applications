package com.example.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class UserService {

    private final WebClient webClient;

    @Autowired
    public UserService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://ipinfo.io").build();
    }

    public Map<String, Object> getLocation() {
        Map<String, Object> responseMap = new LinkedHashMap<>();

        ClientResponse responseData = webClient.get()
                .uri("/json")
                .exchange()
                .block();

        HttpStatus statusCode = responseData.statusCode();
        HttpHeaders headers = responseData.headers().asHttpHeaders();
        String response = responseData.bodyToMono(String.class).block();

        responseMap.put("Headers", headers);
        responseMap.put("Body", response);
        responseMap.put("StatusCode", statusCode.value());
        responseMap.put("StatusMessage", statusCode.getReasonPhrase());

        return responseMap;
    }
}
