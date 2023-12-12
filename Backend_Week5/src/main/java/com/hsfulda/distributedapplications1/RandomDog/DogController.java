package com.hsfulda.distributedapplications1.RandomDog;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Objects;

@RestController
public class DogController {

    @Autowired
    private RestTemplate restTemplate;
    @Value("${dog.api.url}")
    private String dogApiUrl;

    @GetMapping("/randomDog")
    public void getRandomDog(HttpServletResponse response) throws IOException {

        System.out.println("Trying to call random dog API");

        ResponseEntity<DogApiResponse> apiResponse = restTemplate.getForEntity(dogApiUrl, DogApiResponse.class);
        String imageUrl = Objects.requireNonNull(apiResponse.getBody()).getMessage();

        int statusCode = apiResponse.getStatusCode().value();
        String headers = apiResponse.getHeaders().toString();

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(apiResponse.getBody());

        String link = "<a href='" + imageUrl + "' target='_blank'>Click here to view the image</a>";
        String htmlContent = "<html><body>" + link + "<br><br>Response: " + jsonResponse + "<br><br>Status Code: " +
                statusCode + "<br><br>Headers: " + headers + "</body></html>";
        response.setContentType("text/html");

        response.getWriter().write(htmlContent);

        System.out.println("API call successful. Result send to frontend");
    }
}
/*
  Jonas Wagner - 1315578 - 04.11.2023
 */
