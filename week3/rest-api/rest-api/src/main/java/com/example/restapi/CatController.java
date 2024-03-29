package com.example.restapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
        import org.springframework.ui.Model;
        import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Arrays;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
@Controller
public class CatController {

    private static final WebClient webClient = WebClient.create("https://cat-fact.herokuapp.com");
    // v3 using webclient
    @GetMapping("/cat-facts")
    public Mono<String> getCatFacts(@RequestParam(defaultValue = "1") int amount, Model model) {
        String baseUrl = "https://cat-fact.herokuapp.com";
        String uri = "/facts/random?animal_type=cat&amount=" + amount;

        Class<?> responseType = amount == 1 ? CatFact.class : CatFact[].class;

        return WebClientUtil.getForResponse(baseUrl, uri)
                .flatMap(response -> {
                    // Extract status code
                    int statusCode = response.statusCode().value();
                    // Extract headers
                    HttpHeaders headers = response.headers().asHttpHeaders();

                    // Extract and log the entire response
                    return response.bodyToMono(responseType)
                            .doOnNext(catFacts -> {
                                model.addAttribute("catFacts", amount == 1 ? catFacts : Arrays.asList(catFacts));
                                System.out.println("Retrieved cat facts: " + catFacts);
                                System.out.println("Response Status Code: " + statusCode);
                                System.out.println("Response Headers: " + headers);
                                // log the response as JSON String
                                String jsonResponse = null;
                                try {
                                    jsonResponse = new ObjectMapper().writeValueAsString(catFacts);
                                    System.out.println("Retrieved cat facts (JSON): " + jsonResponse);
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .thenReturn("cat-facts");
                });
    }

    /*
    @GetMapping("/cat-facts")
    public Mono<String> getCatFacts(@RequestParam(defaultValue = "1") int amount, Model model) {
        String baseUrl = "https://cat-fact.herokuapp.com";
        String uri = "/facts/random?animal_type=cat&amount=" + amount;

        Class<?> responseType = amount == 1 ? CatFact.class : CatFact[].class;

        return WebClientUtil.getForObject(baseUrl, uri, responseType)
                .doOnNext(catFacts -> {
                    model.addAttribute("catFacts", amount == 1 ? catFacts : Arrays.asList(catFacts));

                    System.out.println("Retrieved cat facts: " + catFacts);

                    // Log headers
                    System.out.println("Response Headers: " + headers);

                    // If you want to log the response as JSON, you can convert it to JSON string
                    String jsonResponse = null;
                    try {
                        jsonResponse = new ObjectMapper().writeValueAsString(catFacts);
                        System.out.println("Retrieved cat facts (JSON): " + jsonResponse);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }



                })
                .thenReturn("cat-facts");
    }
     */

    // v2: with RestTemplate Objects
    /*
    @GetMapping("/cat-facts")
    public String getCatFacts(@RequestParam(defaultValue = "1") int amount, Model model) {
        String apiUrl = "https://cat-fact.herokuapp.com/facts/random?animal_type=cat&amount=" + amount;

        // Make a Get Request
        RestTemplate restTemplate = new RestTemplate();

        // Case needed, when response is a single object or an array.
        if (amount == 1) {
            // single cat object
            ResponseEntity<CatFact> responseEntity = restTemplate.getForEntity(apiUrl, CatFact.class);
            CatFact catFact = responseEntity.getBody();
            model.addAttribute("catFacts", catFact);

            String rawResponse = restTemplate.getForObject(apiUrl, String.class);
            System.out.println("Response Status: " + responseEntity.getStatusCode());
            System.out.println("Response Body: " + catFact);
            System.out.println("Raw Response Body: " + rawResponse);

        } else {
            // array of CatFacts
            ResponseEntity<CatFact[]> responseEntity = restTemplate.getForEntity(apiUrl, CatFact[].class);
            CatFact[] catFacts = responseEntity.getBody();

            String rawResponse = restTemplate.getForObject(apiUrl, String.class);
            model.addAttribute("catFacts", Arrays.asList(catFacts));
            System.out.println("Response Status: " + responseEntity.getStatusCode());
            System.out.println("Response Body: " + Arrays.toString(catFacts));
        }
     */


        //v1: no cases
        //ResponseEntity<CatFact> responseEntity = restTemplate.getForEntity(apiUrl, CatFact.class);
        //CatFact catFact = responseEntity.getBody();
        //System.out.println("Response Status: " + responseEntity.getStatusCode());
        //System.out.println("Response Body: " + catFact);
        // Add the cat facts to the model
        //model.addAttribute("catFacts", catFact);

        // return html
        //return "cat-facts"
}
