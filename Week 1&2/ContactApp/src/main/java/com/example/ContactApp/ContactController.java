package com.example.ContactApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


/* Author: Kimiya Fazlali - 1440906
 *  Created: 5 Nov 20223 */


@Controller
public class ContactController {
    @Autowired
    private ContactRepository contactRepository;

    @GetMapping("/")
    public String index(Model model) {
        List<Contact> contacts = contactRepository.findAll();
        model.addAttribute("contacts", contacts);
        return "index";
    }

    @PostMapping("/addContact")
    public String addContact(@RequestParam String name, @RequestParam String phoneNumber) {
        Contact contact = new Contact();
        contact.setName(name);
        contact.setPhoneNumber(phoneNumber);
        contactRepository.save(contact);
        return "redirect:/";
    }
}
