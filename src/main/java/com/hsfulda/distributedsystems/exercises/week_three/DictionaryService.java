package com.hsfulda.distributedsystems.exercises.week_three;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

/*
 * Author : Nick Stolbov, Matrikel Nr.: 1269907
 * Created: 10.11.2023
 */
@Service
public class DictionaryService {

    private final WebClient webClient;

    DictionaryService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.dictionaryapi.dev/api/v2/entries/en")
                .build();
    }

    public ClientResponse getDictionaryResponse(String word) {
        return webClient.get()
                .uri("/" + word)
                .exchange()
                .block();
    }
}
