package com.example.Spring_Week1;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Controller
public class controller {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebClient webClient;


    @PostMapping(value = "/addUser", produces = MediaType.TEXT_PLAIN_VALUE)
    public @ResponseBody String addUser(@RequestBody String requestBody) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            User user = mapper.readValue(requestBody, User.class);
            return processUser(user);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String processUser(User user) {
        System.out.println("User Input:");
        System.out.println(user.getname() + "\n" + user.getNumber());

        String number = user.getNumber();
        String responseJSON = getData(number);
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<?, ?> responseData = mapper.readValue(responseJSON, Map.class);
            boolean isValid = (boolean) responseData.get("valid");
            if (isValid) {
                userRepository.save(user);
            }
            System.out.println("Phone number Valid? :\n" + isValid);
            // Returning a JSON string as response
            return String.valueOf(isValid);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    public String getData (String number){
            String url = "https://phonevalidation.abstractapi.com/v1/?api_key=425857de27b74d6db72207693854cfa6&phone=" + number;
            WebClient webClient = WebClient.create();

            Mono<ResponseEntity<String>> responseMono;
            responseMono = webClient
                    .get()
                    .uri(url)
                    .retrieve()
                    .toEntity(String.class);

            ResponseEntity<String> responseEntity = responseMono.block();

            if (responseEntity != null) {

                System.out.println("API response:");
                System.out.println("Status code: " + responseEntity.getStatusCode());
                System.out.println("Headers: " + responseEntity.getHeaders());
                System.out.println("Response body: " + responseEntity.getBody());
                return responseEntity.getBody();
            } else {
                return "error";
            }

        }

        /*Author : Sheikh Zubeena Shireen      ScreenCaptured Date : 17/11/2023     Matriculation Number : 1492765*/
        @GetMapping(path = "/all") // Map ONLY GET Requests
        public @ResponseBody Iterable<User> getAllUsers () {
            return userRepository.findAll();
        }  //fetches all users from DB
        @GetMapping(value = "/index")
        public String home () {
            return "index";
        }  //returns index.html
        @GetMapping(value = "/")
        public String go2home () {
            return "redirect:/index";
        } //redirects to index.html when url has only '/'
    }

