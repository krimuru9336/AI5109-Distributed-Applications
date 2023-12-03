/**
 * @author Oshadhi Samarasinghe
 * @date 2023-11-03
 */

package com.example.bmi.controller;
import com.example.bmi.model.BmiBean;
import com.example.bmi.service.BmiService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BmiController {
    BmiService bmiService;

    public BmiController(BmiService bmiService) {
        this.bmiService = bmiService;
    }

    @PostMapping("/bmi")
    public String calculateUserBMI(@ModelAttribute("bmi") BmiBean bmiBean, Model model) {

        BmiBean input = new BmiBean();
        input.setHeight(bmiBean.getHeight());
        input.setWeight(bmiBean.getWeight());
        input.setName(bmiBean.getName());

        double bmi = bmiService.calculateBmi(input);
        bmiService.saveUser(bmiBean, bmi);
        model.addAttribute("result", bmiBean);
        return "bmiResult";
    }

    @GetMapping("/user/{id}")
    public String getUser(@PathVariable Long id, Model model) {

        BmiBean userBean = bmiService.getUser(id);
        model.addAttribute("user", userBean);
        return "userResult";
    }









}
