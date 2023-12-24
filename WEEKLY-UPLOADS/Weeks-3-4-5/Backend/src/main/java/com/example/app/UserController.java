package com.example.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Week 1 - Basic Spring Boot Setup
 * Created By: Suhaila Kondappilly Aliyar
 * Created on: 3rd November 2023
 * Matriculation Number:1492822
 */
@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    //For saving the user
    @PostMapping("/submit")
    public void submitUser(@RequestBody User user) {

        userRepository.save(user);
    }

    //For displaying list of users
    @GetMapping(value = "/users", produces = "application/json")
    public ResponseEntity<Iterable<User>> getUserNames() {
        // List of Users from DB
        Iterable<User> responseData =  userRepository.findAll();

        // Create HttpHeaders
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");

        // Create ResponseEntity with status code, headers, and response data
        ResponseEntity<Iterable<User>> responseEntity = new ResponseEntity<>(responseData, headers, HttpStatus.OK);

        // Print status code, headers, and response
        System.out.println("Status Code: " + responseEntity.getStatusCode());
        System.out.println("Headers: " + responseEntity.getHeaders());
        System.out.println("Response: " + responseEntity.getBody());

        return responseEntity;
    }

    // Use WebClient to make a GET request to an external API to get Location
    @GetMapping("/location")
    public ResponseEntity<Object> getUserLocation() {
        Object responseData = userService.getLocation();
        return ResponseEntity.ok(responseData);
    }
    


}

/**
 * Week 2 - Calling APIs
 * Created By: Suhaila Kondappilly Aliyar
 * Created on: 16th November 2023
 * Matriculation Number:1492822
 */
