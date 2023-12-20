package com.example.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Contact;

@Repository
public interface ContactRepo extends JpaRepository<Contact,String>{
	
}
//Matricular number:1491966
//Name:Pinky Jitendra Tinani
//Created on:27.10.2023 