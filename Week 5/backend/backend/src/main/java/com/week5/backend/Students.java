package com.week5.backend;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Students {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long serial_num;
  private String name;
  private String contact;

  public Long getId() {
    return serial_num;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }
}

