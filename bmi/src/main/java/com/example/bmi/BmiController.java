package com.example.bmi;

/*
 * Author: Christian Jumtow
 * Created: 03.11.2023
 * MNr.: 1166358
 */

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class BmiController {
	
	@Autowired
	BmiEntityRepository repository;

	@GetMapping("/")
	public String getBmi(Model model){
		List<BmiEntity> bmis = repository.findAll();
		model.addAttribute("bmis", bmis);
		return "index";
	}

	@PostMapping("/bmi")
	public String m_calcbmi(@ModelAttribute BmiEntity bmi, Model model) {

		bmi.calculateBMI();
		repository.save(bmi);

		model.addAttribute("bmi",bmi);

		List<BmiEntity> bmis = repository.findAll();
		model.addAttribute("bmis", bmis);

		return "index";
	}
}