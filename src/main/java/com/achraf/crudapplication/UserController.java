/* class purpose:
 * This is a Spring MVC controller that handles HTTP requests and interacts with the UserRepository
 */
package com.achraf.crudapplication;

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
		model.addAttribute("users", userRepository.findAll());
		model.addAttribute("user", new User());
		return "users";
	}

	@PostMapping("/add")
	public String userSubmit(User user) {
		userRepository.save(user);
		return "redirect:/";
	}

	@GetMapping("/edit/{id}")
	public String editUser(@PathVariable Long id, Model model) {
		model.addAttribute("user", userRepository.findById(id).get());
		return "form";
	}

	@GetMapping("/delete/{id}")
	public String deleteUser(@PathVariable Long id) {
		userRepository.deleteById(id);
		return "redirect:/";
	}
}

/*
 * Author: Achraf Boudabous created: 28/10/2023
 */