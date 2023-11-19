package com.example.week_01_bmi;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BmiController {

    @RequestMapping("/")
    public String bmiForm() {
        return "bmiForm";
    }

    @PostMapping("/calculate-bmi")
    public String calculateBMI(@RequestParam("weight") double weight, @RequestParam("height") double height, Model model) {
        // BMI-Berechnung
        double bmi = weight / (height * height);

        // Ergebnis an das HTML-Template senden
        model.addAttribute("bmiResult", bmi);

        return "bmiForm";
    }
}
