package com.example.app;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Name of the user
    private String name;

    //Phone number of the user
    private String phone;

    //getter for Id
    public Long getId() {
        return id;
    }

    //getter for name
    public String getName() {
        return name;
    }

    //Getter for phoneNumber
    public String getPhone() {
        return phone;
    }

    //Setter for name
    public void setName(String name) {
        this.name = name;
    }

    //Setter for phoneNumber
    public void setPhone(String phone) {
        this.phone = phone;
    }

    //Setter for id
    public void setId(Long id) {
        this.id = id;
    }

}
/**
 * Week 1 - Basic Spring Boot Setup
 * Created By: Suhaila Kondappilly Aliyar
 * Created on: 3rd November 2023
 * Matriculation Number:1492822
 */
