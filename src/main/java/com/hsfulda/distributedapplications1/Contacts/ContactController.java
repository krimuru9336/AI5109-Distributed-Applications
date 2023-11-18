package com.hsfulda.distributedapplications1.Contacts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ContactController {

    @Autowired
    private ContactRepository contactRepository;

    @GetMapping("/")
    public String index(Model model) {
        List<Contact> contacts = contactRepository.findAll();
        model.addAttribute("contacts", contacts);
        model.addAttribute("newContact", new Contact());
        return "index";
    }

    @PostMapping("/contacts")
    public String addContact(@ModelAttribute Contact contact) {
        contactRepository.save(contact);
        return "redirect:/";
    }

    /*
    Jonas Wagner - 1315578 - 28.10.2023
     */
}
