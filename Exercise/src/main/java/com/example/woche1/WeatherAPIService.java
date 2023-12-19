package com.example.woche1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class WeatherAPIService {
  private final WebClient webClient;

  @Autowired
  public WeatherAPIService(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.baseUrl("https://api.open-meteo.com").build();
  }

  public Map<String, Object> callAPI() {
    Map<String, Object> responseMap = new LinkedHashMap<>();

    ClientResponse response = webClient.get()
            .uri("/v1/forecast?latitude=50.56525393509598&longitude=9.685194271641944&current=temperature_2m")
            .exchange()
            .block();

    HttpStatus statusCode = (HttpStatus) response.statusCode();
    HttpHeaders headers = response.headers().asHttpHeaders();
    String responseBody = response.bodyToMono(String.class).block();

    responseMap.put("statusCode", statusCode.value());
    responseMap.put("statusMessage", statusCode.getReasonPhrase());
    responseMap.put("headers", headers);
    responseMap.put("body", responseBody);

    return responseMap;
  }
}