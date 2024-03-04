/* class purpose:
 * This is a Spring MVC controller that handles HTTP requests and interacts with the UserRepository
 */
package com.example.CRUD;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {
	@Autowired
	private UserRepository userRepository;

	@GetMapping("/")
	public String listUsers(Model model) {
		model.addAttribute("index", userRepository.findAll());
		model.addAttribute("user", new User());
		return "index";
	}

	@PostMapping("/add")
	public String userSubmit(User user) {
		userRepository.save(user);
		return "redirect:/";
	}

	@GetMapping("/delete/{id}")
	public String deleteUser(@PathVariable Long id) {
		userRepository.deleteById(id);
		return "redirect:/";
	}
}

/*
 * Author: louay ben hadj said created: 06/11/2023
 */