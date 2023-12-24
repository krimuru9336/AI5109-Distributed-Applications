package com.example.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

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

    //For saving the user
    @PostMapping("/submit")
    public void submitUser(@RequestBody User user) {
        userRepository.save(user);
    }

    //For displaying list of users
    @GetMapping("/users")
    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }
}
