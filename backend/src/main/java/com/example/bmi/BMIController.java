package com.example.bmi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 import org.springframework.ui.Model;
 * Author: Thomas Niestroj
 * Created: 07.11.2023
 * */
@RestController
@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false" )
public class BMIController {
	
	@Autowired // This means to get the bean called userRepository
	private BMIRepository bmiRepo;
	
	@PostMapping("/calculate-bmi")
	@ResponseBody
	public BmiBean bmiForm(@RequestBody BmiBean bmi) {
		System.err.println(bmi.getHeight());
		System.err.println(bmi.getWeight());
		try {
			bmi.setBmi(BMICalculator.calculate(bmi.getHeight(), bmi.getWeight()));
		} catch (Exception e) {
			System.err.println(e);
			bmi.setBmi(-1);
		}
		bmiRepo.save(bmi);
		return bmi;
	}
	
	// Add a new endpoint to fetch data
    @GetMapping("/bmi-data")
    public @ResponseBody Iterable<BmiBean> getAllBmiData() {
        return bmiRepo.findAll();
    }
}
