package com.example.Spring_Week1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpHeaders;


import java.util.Map;
import java.util.Objects;

@Controller   //means this class is controller class
//@EnableAutoConfiguration
public class controller {


    @Autowired // This means to get the bean called userRepository
    private UserRepository userRepository;

    @Autowired
    private WebClient webClient;


    @PostMapping(path="/addUser") // Map ONLY POST Requests
        public @ResponseBody String addUser(@RequestBody User user) {
        //Week3: check if entered phone number is valid.If valid - then store in DB, else not.
        String number = user.getNumber();  //get phone number from user input
        String responseJSON= getData(number); // get response from API
        ObjectMapper mapper = new ObjectMapper();
        Map responseData = null;
        try {
            responseData = mapper.readValue(responseJSON, Map.class);
            boolean isValid = (boolean) responseData.get("valid"); //Get value of key "valid", returns a bool whether phone number is valid or not
            if (isValid) { userRepository.save(user);} //Saves user data into DB
//            System.out.println("Phone number Valid? :\n" + isValid );
            return String.valueOf(isValid);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e); }
    }
    public String getData( String number){
        String url = "https://phonevalidation.abstractapi.com/v1/?api_key=425857de27b74d6db72207693854cfa6&phone="+number;
        WebClient webClient = WebClient.create();

        Mono<ResponseEntity<String>> responseMono;
        responseMono = webClient
                .get()
                .uri(url)
                .retrieve()
                .toEntity(String.class);

        ResponseEntity<String> responseEntity = responseMono.block();

        if (responseEntity != null) {

            System.out.println("Status code: " + responseEntity.getStatusCode());
            System.out.println("Headers: " + responseEntity.getHeaders());
            System.out.println("Response body: " + responseEntity.getBody());
            return responseEntity.getBody();
        } else{return "error";}

    }

    @GetMapping(path="/all") // Map ONLY GET Requests
    public @ResponseBody Iterable<User> getAllUsers() {
        return userRepository.findAll();}  //fetches all users from DB
    @GetMapping(value = "/index")
    public String home(){  return "index";}  //returns index.html
    @GetMapping(value = "/")
    public String go2home(){  return "redirect:/index";} //redirects to index.html when url has only '/'
}

