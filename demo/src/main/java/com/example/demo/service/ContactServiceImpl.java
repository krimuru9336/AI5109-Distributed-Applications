package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Contact;
import com.example.demo.repo.ContactRepo;

@Service
public class ContactServiceImpl implements ContactService{

	@Autowired
	private ContactRepo crepo;
	@Override
	public void insertContact(Contact contact) {
		// TODO Auto-generated method stub
		crepo.save(contact);
	}}
//Matricular number:1491966
//Name:Pinky Jitendra Tinani
//Created on:27.10.2023 