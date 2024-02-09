package com.example.BMI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

	 private final ApiService apiService;
     private UserRepository userRepository;
	 
    @Autowired
    public ApiController(ApiService apiService, UserRepository userRepository) {
        this.apiService = apiService;
        this.userRepository = userRepository;
    }

    @GetMapping("/call-api")
    public String callApi() {
        return apiService.callApi();
    }

    @GetMapping("/users")
    @CrossOrigin(origins = "*")
    @ResponseBody // Add this annotation to directly return the response body
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users;
    }

    @PostMapping("/saveUser")
    @CrossOrigin(origins = "*")
    public ResponseEntity<String> saveUser(@RequestParam("name") String name,
                           @RequestParam("phone") String phone) {
        try {
            // Create a new User object with the provided name and phone number
            User user = new User();
            user.setName(name);
            user.setPhone(phone);

            // Save the user to the database
            userRepository.save(user);

            return new ResponseEntity<>("User saved successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to save user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

//Author : Bhavin Vadhiya 
//Created on : 08/02/2024