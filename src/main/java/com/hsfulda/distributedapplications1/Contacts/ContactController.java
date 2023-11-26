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
        List<Contact> allContacts = contactRepository.findAll();
        System.out.println("GET All Contacts: " + printContacts(allContacts));
        return allContacts;
    }

    private String printContacts(List<Contact> allContacts) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Contact contact : allContacts) {
            stringBuilder.append("Name: ").append(contact.getName()).append(", ");
            stringBuilder.append("Phone: ").append(contact.getPhoneNumber()).append("\n");
        }
        return stringBuilder.toString();
    }

    @PostMapping
    public Contact addContact(@RequestBody Contact contact) {
        System.out.println("Add Contact to DB: Name: " + contact.getName() + ", Phone: " + contact.getPhoneNumber());
        return contactRepository.save(contact);
    }

    /*
    Jonas Wagner - 1315578 - 24.11.2023
     */
}
