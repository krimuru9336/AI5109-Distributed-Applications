// Sahan Wijesinghe - 05.12.2023 - Mtr Nr 1453575
package com.bmi.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bmi")
@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false" )
public class BmiController {

    @Autowired
    private BmiRepository bmiRepository;
    
    @GetMapping
    public List<BmiBean> getBmis() {
        List<BmiBean> bmis = bmiRepository.findAll();

        StringBuilder stringBuilder = new StringBuilder();
        for (BmiBean bmi : bmis) {
            stringBuilder.append("Name: ").append(bmi.getName()).append(", \n");
            stringBuilder.append("Gender: ").append(bmi.getGender()).append("\n");
            stringBuilder.append("Rib cage: ").append(bmi.getRibCage()).append(", \n");
            stringBuilder.append("Leg length: ").append(bmi.getLegLength()).append("\n");
            stringBuilder.append("BMI: ").append(bmi.getBmi()).append("\n");
        }
        String bmiString = stringBuilder.toString();

        System.out.println("Fetching all BMIs: " + bmiString);
        return bmis;
    }

    @PostMapping
    public BmiBean addBmi(@RequestBody BmiBean bmi) {
    	double ribCage = bmi.getRibCage();
    	double legLength = bmi.getLegLength();
    	
        // Step 1: Divide rib cage measurement by 0.7062 and subtract leg length
        double step1Result = ribCage / 0.7062 - legLength;

        // Step 2: Divide step 1 result by 0.9156
        double step2Result = step1Result / 0.9156;

        // Step 3: Subtract leg length from step 2 result to get FBMI
        double bmiValue = step2Result - legLength;
    	
        bmi.setBmi(bmiValue);
        
        System.out.println(
            "Adding BMI to database: " +
            " Name: " + bmi.getName() + 
            ", Gender: " + bmi.getGender() +
            ", Rib cage: " + bmi.getRibCage() + 
            ", Leg length: " + bmi.getLegLength() +
            ", BMI: " + bmi.getBmi()
         );

        return bmiRepository.save(bmi);
    }
}