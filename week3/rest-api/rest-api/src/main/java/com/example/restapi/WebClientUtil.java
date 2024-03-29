package com.example.restapi;

import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class WebClientUtil {

    private static final WebClient webClient = WebClient.create();

    public static Mono<ClientResponse> getForResponse(String baseUrl, String uri) {
        return webClient
                .mutate()
                .baseUrl(baseUrl)
                .build()
                .get()
                .uri(uri)
                .exchange();
    }

   /*
    public static <T> Mono<T> getForObject(String baseUrl, String uri, Class<T> responseType) {
        return getForResponse(baseUrl, uri)
                .flatMap(response -> response.bodyToMono(responseType));
    }
    */

    /*
    public static <T> Mono<T> getForObject(String baseUrl, String uri, Class<T> responseType) {
        return webClient
                .mutate()
                .baseUrl(baseUrl)
                .build()
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(responseType);
    }

     */
    /*
    private static final WebClient webClient = WebClient.create("https://cat-fact.herokuapp.com");


    public static <T> Mono<T> getForObject(String uri, Class<T> responseType) {
        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(responseType);
                //.cast(responseType); // explicit cast to resolve generic type
    }
    */

}
