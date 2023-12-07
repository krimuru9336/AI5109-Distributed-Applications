package com.example.bmi.controller;

import com.example.bmi.BmiBean;
import com.example.bmi.service.BmiService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;

//author - Seshenya Weerasinghe
//date - 07.12.2023-->
//matriculation number - 1454176

@Controller
@RequestMapping("/")
@CrossOrigin(origins= {"*"}, maxAge = 4800, allowCredentials = "false" )
public class BmiApplicationController {
    BmiService bmiService;
    public BmiApplicationController(BmiService bmiService) {
        this.bmiService = bmiService;
    }

    @GetMapping
    @ResponseBody
    public List<BmiBean> getBmis() {
        List<BmiBean> bmis = bmiService.getAllBmiData();

        StringBuilder stringBuilder = new StringBuilder();
        for (BmiBean bmi : bmis) {
            stringBuilder.append("Name: ").append(bmi.getName()).append(", \n");
            stringBuilder.append("Weight: ").append(bmi.getWeight()).append("\n");
            stringBuilder.append("Height: ").append(bmi.getHeight()).append(", \n");
            stringBuilder.append("BMI Value: ").append(bmi.getBmiValue()).append("\n");
        }
        String bmiString = stringBuilder.toString();

        System.out.println("Fetching all BMIs: " + bmiString);
        return bmis;
    }

    @PostMapping
    public BmiBean addBmi(@RequestBody BmiBean bmi) {
        double bmiValue  = calculateBmiValue(bmi.getHeight(), bmi.getWeight());
        bmi.setBmiValue(bmiValue);
        bmiService.saveUser(bmi);
        bmi.setBmiValue(bmiValue);

        System.out.println(
                "Adding BMI to database: " +
                        " Name: " + bmi.getName() +
                        ", Weight: " + bmi.getWeight() +
                        ", Height: " + bmi.getHeight() +
                        ", BMI Value: " + bmi.getBmiValue()
        );

        return bmiService.saveUser(bmi);
    }

    private double calculateBmiValue(double height, double weight) {
        // BMI formula: weight (kg) / (height (m) * height (m))
        return weight / (height * height);
    }

}
