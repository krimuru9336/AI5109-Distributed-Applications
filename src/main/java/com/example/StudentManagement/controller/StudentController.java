package com.example.StudentManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.StudentManagement.model.Student;
import com.example.StudentManagement.service.StudentService;

@Controller


public class StudentController {
	
	@Autowired
	private StudentService studentservice;
	
	@GetMapping("/")
	public String viewPage(Model model) {
		model.addAttribute("listStudents", studentservice.getAllStudents());
		
		return "index";
	}
	
	 @GetMapping("/showNewStudentForm")
	 public String showNewStudentForm(Model model) {
	     // create model attribute to bind form data
	     Student student = new Student();
	     model.addAttribute("student", student);
	     return "new_student";
	 }
	
	@PostMapping("/AddStudent")
	public String AddStudent(@ModelAttribute("student") Student student) {
		studentservice.AddStudent(student);
		return "redirect:/";
		
	}

}
