package com.da.excercise1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.da.excercise1.model.StudentModel;
import com.da.excercise1.service.StudentService;

@Controller
public class StudentController {
	
	@Autowired
	private StudentService studentservice;
	
	@GetMapping("/")
	public String viewPage(Model model) {
	    StudentModel student = new StudentModel();
		model.addAttribute("listStudents", studentservice.getStudents());
	    model.addAttribute("student", student);
		return "index";
	}
	
	
	@PostMapping("/addStudent")
	public String addStudent(@ModelAttribute("student") StudentModel student) {
		studentservice.addStudent(student);
		return "redirect:/";
		
	}

}