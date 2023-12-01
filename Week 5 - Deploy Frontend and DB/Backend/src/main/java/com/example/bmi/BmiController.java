package com.example.bmi;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api")
public class BmiController {
    @Autowired
    private UserRepository userRepository;

    @CrossOrigin(origins = "${da.bmi.cors.allowed-origin}")
    @GetMapping("/entries")
    public List<BmiBean> getAllEntries() {
    	List<BmiBean> entries = userRepository.findAll();
    	System.out.println("Get Entries:");
    	for (BmiBean bmi : entries) {
        	System.out.println("Name: " + bmi.getName() + " - Height: " + bmi.getHeight() + 
        			" - Weight: " + bmi.getWeight());
        	System.out.println("Calculated BMI: " + bmi.getBmi() + "\n");
    	}
    	return entries;
    }
    
    @CrossOrigin(origins = "${da.bmi.cors.allowed-origin}")
    @PostMapping("/entries")
    public BmiBean createEntry(@RequestBody BmiBean bmi) {
    	bmi.calcBmi();
    	System.out.println("Save new entry:");
    	System.out.println("Name: " + bmi.getName() + " - Height: " + bmi.getHeight() + 
    			" - Weight: " + bmi.getWeight());
    	System.out.println("Calculated BMI: " + bmi.getBmi() + "\n");
        userRepository.save(bmi);
        return bmi;
    }
}