package com.example.bmi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1/bmi")
public class BmiController {
    /*
        Author: Azamat Afzalov
        Matriculation number: 1492864
        Date: 05.11.2023
     */
    final private BmiService bmiService;
    @Autowired
    public BmiController(BmiService bmiService) {
        this.bmiService = bmiService;
    }

    @GetMapping
    public List<Bmi> getAll() {
        return this.bmiService.getAll();
    }
    @PostMapping
    public Bmi create(@RequestBody Bmi body) {

        Bmi bmi = new Bmi();
        bmi.setHeight(body.getHeight());
        bmi.setWeight(body.getWeight());
        bmi.setName(body.getName());
        bmi.setPhone(body.getPhone());
        bmi.setBmi(this.bmiService.calculateBmi(body.getHeight(), body.getWeight()));
        return this.bmiService.create(bmi);
    }
}
