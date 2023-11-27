/*
 * Author: Sahan Wijesinghe (1453575) 
 * Created: 07.11.2023
 */
package com.example.bmi200;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BmiController {

    @Autowired
    private BmiRepository bmiRepository;
    
    @GetMapping("/")
    public String getBmis(Model model) {
        model.addAttribute("Bmi", new BmiBean());
        model.addAttribute("listBmis", bmiRepository.findAll());
        return "index";
    }

    @PostMapping("/")
    public String saveBmi(@ModelAttribute("Bmi") BmiBean bmi) {
    	double ribCage = bmi.getRibCage();
    	double legLength = bmi.getLegLength();
    	
        // Step 1: Divide rib cage measurement by 0.7062 and subtract leg length
        double step1Result = ribCage / 0.7062 - legLength;

        // Step 2: Divide step 1 result by 0.9156
        double step2Result = step1Result / 0.9156;

        // Step 3: Subtract leg length from step 2 result to get FBMI
        double bmiValue = step2Result - legLength;
    	
        bmi.setBmi(bmiValue);
        bmiRepository.save(bmi);
        return "redirect:/";
    }
}