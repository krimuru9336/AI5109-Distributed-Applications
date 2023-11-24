package com.hsfulda.distributedapplications1.Contacts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@CrossOrigin(origins = "http://20.102.110.117")
public class ContactController {

    @Autowired
    private ContactRepository contactRepository;

    @GetMapping
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    @PostMapping
    public Contact addContact(@RequestBody Contact contact) {
        return contactRepository.save(contact);
    }

    /*
    Jonas Wagner - 1315578 - 24.11.2023
     */
}
