package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.entity.Contact;
import com.example.demo.repo.ContactRepo;

@RestController class ContactController {
	@Autowired
	public ContactRepo crepo;

	
	@CrossOrigin(origins = "http://localhost:5500", allowedHeaders = "Accept")
	@RequestMapping("/insert")
	public String Insert() {
		System.out.println("hello");
		return "start";
	}

	@RequestMapping(value = "/")
	public ModelAndView insert() {
		ModelAndView modelAndView = new ModelAndView("start"); // You can add model attributes
		return modelAndView;
	}
	@PostMapping("/saveContact")
    public ModelAndView saveContact(@RequestParam("name") String name, @RequestParam("phone") String phone) {
        Contact contact = new Contact(name,phone);
        crepo.save(contact);
		List<Contact> contactlist = crepo.findAll();
		ModelAndView modelAndView=new ModelAndView("start");
        modelAndView.addObject("contacts", contactlist);
        return modelAndView;
    }
	@GetMapping("/viewContact")
    public ModelAndView viewContact() {
		List<Contact> contactlist = crepo.findAll();
		ModelAndView modelAndView=new ModelAndView("ContactList");
        modelAndView.addObject("contacts", contactlist);
        return modelAndView;
    }
	@GetMapping("/apiviewContacts")
    public List<Contact> viewContactApi() {
		List<Contact> contactlist = crepo.findAll();
//		ModelAndView modelAndView=new ModelAndView("ContactList");
//        modelAndView.addObject("contacts", contactlist);
        return contactlist;
    }
}
//Matricular number:1491966
//Name:Pinky Jitendra Tinani
//Created on:27.10.2023 