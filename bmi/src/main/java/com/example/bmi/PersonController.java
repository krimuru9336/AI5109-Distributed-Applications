package com.example.bmi;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/*
 * Author: Adrianus Jonathan Engelbracht
 * Created: 06.07.2023
 * Matrikelnummer: 1151826
 */


@Controller
public class PersonController {
	@Autowired
	private PersonRepository personRepository;
	
	// handles http GET Requests to the root url ("/") from the frontend 
    @GetMapping("/")
    public String index(Model model) {
        // Load data from database, list of person object from the personRepository 
        List<Person> persons = personRepository.findAll();
        //persons are added to the model object 
        model.addAttribute("persons", persons);   
        // Add an empty Person object for the form
        model.addAttribute("person", new Person()); 
        // locate index file (template or HTML page) and render it.
        // rendered view is the returned as http response to the client 
        return "index";
    }
	

    // Post Endpoint to receive the form data and calculate bmi 
    // create a Person object and save it to the database using the repository
	@PostMapping("/calculate-bmi")
	public String calculateBmi(@ModelAttribute Person person) {
		// calculate and set bmi
		float bmiCalculated = person.calculateBMI();
		person.setBmi(bmiCalculated);
		// save the person to the database 
		personRepository.save(person);
		// redirect to root url 
		return "redirect:/";
	}
	
}
