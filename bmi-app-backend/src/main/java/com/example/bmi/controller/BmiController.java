/**
 * @author Oshadhi Samarasinghe
 * @date 2023-11-03
 */

package com.example.bmi.controller;
import com.example.bmi.dto.BmiBeanDto;
import com.example.bmi.model.BmiBean;
import com.example.bmi.service.BmiService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins= {"*"}, maxAge = 4800)
public class BmiController {
    BmiService bmiService;

    public BmiController(BmiService bmiService) {
        this.bmiService = bmiService;
    }

    @PostMapping("/bmi")
    public BmiBean calculateUserBMI(@RequestBody  BmiBeanDto bmiBean) {

        BmiBean input = new BmiBean();
        input.setHeight(bmiBean.getHeight());
        input.setWeight(bmiBean.getWeight());
        input.setName(bmiBean.getName());

        double bmi = bmiService.calculateBmi(input);
        return bmiService.saveUser(input, bmi);
    }

    @GetMapping("/user/{id}")
    public BmiBean getUser(@PathVariable Long id) {

        return bmiService.getUser(id);
    }

    @GetMapping("/users")
    public List<BmiBean> getUsers( ) {
        return bmiService.getUsers();
    }


}
