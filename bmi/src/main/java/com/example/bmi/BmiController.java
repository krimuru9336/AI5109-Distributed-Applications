//Simon Keller, Matrikelnummer 1165562, 03.11.2023
package com.example.bmi;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BmiController {
	private final BMIRepository bmiRepository;

    @Autowired
    public BmiController(BMIRepository bmiRepository) {
        this.bmiRepository = bmiRepository;
    }
	@GetMapping("/")
	public String showIndex(Model model) {
		List<BmiBean> bel = bmiRepository.findAll();
		model.addAttribute("entries",bel);
		return "index";
	}
	
	@PostMapping("/calcbmi")
	public String m_calcbmi(@ModelAttribute BmiBean bmi, Model model) {
		model.addAttribute("bmi",bmi);
		bmi.setBmi(bmi.getWeight()/(bmi.getHeight()*bmi.getHeight()/10000));
		bmiRepository.save(bmi);
		
		List<BmiBean> bel = bmiRepository.findAll();
		model.addAttribute("entries",bel);
		return "index";
	}
}