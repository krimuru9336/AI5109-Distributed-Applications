package com.example.demo.entity;

import javax.persistence.Entity;
import javax.persistence.*;

@Entity
public class Contact {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int id;
	public String name;
	public String phoneNumber;

	public Contact() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Contact( String name, String phoneNumber) {
		super();
		this.name = name;
		this.phoneNumber = phoneNumber;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
}	
//Matricular number:1491966
//Name:Pinky Jitendra Tinani
//Created on:27.10.2023 