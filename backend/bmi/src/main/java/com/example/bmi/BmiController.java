package com.example.bmi;

/*
 * Author: Christian Jumtow
 * Created: 03.11.2023
 * MNr.: 1166358
 */

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class BmiController {

    @Autowired
    BmiEntityRepository repository;

    @CrossOrigin("${frontend.ip}")
    @GetMapping("/bmi")
    public List<BmiEntity> getBmi() {
        return repository.findAll();
    }
    @CrossOrigin("${frontend.ip}")
    @PostMapping("/bmi")
    public BmiEntity calculateBmi(@RequestBody BmiEntity bmi) {
        bmi.calculateBMI();
        System.out.println("Save new entry --> Weight: "+bmi.getWeight()+" Height: "+bmi.getHeight()+" BMI: "+bmi.getBmi());
        return repository.save(bmi);
    }
}
