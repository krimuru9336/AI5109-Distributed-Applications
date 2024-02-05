package com.example.BMI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class UserController {


    
    private UserRepository userRepository;
    
    @Autowired
    public UserController(UserRepository userRepository) {
    	this.userRepository = userRepository;
    }

    @PostMapping("/saveUser")
    public String saveContact(@RequestBody MultiValueMap<String, String> formData) {
    	User userData = new User();
    	
    	
    	System.out.println(formData);
    	userData.setName(formData.getFirst("name"));
    	userData.setPhone(formData.getFirst("phone"));

    	userRepository.save(userData);
        return "redirect:/user-form";
    }

    @GetMapping("/user-form")
    public String getUsers(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "user-form"; //
    }
}

//Author : Bhavin Vadhiya 
//Created on : 06/11/2023