package com.example.bmi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bmi")
public class BmiController {
    private final BMIRepository bmiRepository;

    @Autowired
    public BmiController(BMIRepository bmiRepository) {
        this.bmiRepository = bmiRepository;
    }
    @CrossOrigin("${frontend.ip}")
    @GetMapping("/calcbmi")
    public List<BmiBean> showAllEntries() {
    	System.out.println("showAllEntries");
        return bmiRepository.findAll();
    }
    @CrossOrigin("${frontend.ip}")
    @PostMapping("/calcbmi")
    public List<BmiBean> calculateBmi(@RequestBody BmiBean bmi) {
        bmi.setBmi(bmi.getWeight() / (bmi.getHeight() * bmi.getHeight() / 10000));
        System.out.println("Save new entry --> Weight: "+bmi.getWeight()+" Height: "+bmi.getHeight()+" BMI: "+bmi.getBmi());
        bmiRepository.save(bmi);
        return bmiRepository.findAll();
    }
}
