package com.example.bmi;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class BmiController {
  @Autowired
  private UserRepository userRepository;
  
  @GetMapping("/")
  public String bmiIndex(Model model) {
	model.addAttribute("users", userRepository.findAll());
	return "index";
  }
  
  @GetMapping("/bmi")
  public String bmiRedirect(Model model) {
	model.addAttribute("users", userRepository.findAll());
	return "index";
  }
	  
  @PostMapping("/bmi")
  public String bmiSubmit(@ModelAttribute BmiBean bmi, Model model) {
	if (bmi != null) {
		model.addAttribute("bmi", bmi);
	} else {
		model.addAttribute("bmi", new BmiBean());
	}
	
    BmiBean user = new BmiBean(bmi.getName(), bmi.getHeight(), bmi.getWeight());
    userRepository.save(user);
    
    model.addAttribute("users", userRepository.findAll());
    
    return "index";
  }

}