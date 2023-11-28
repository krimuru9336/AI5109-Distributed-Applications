package com.example.bmi;
import jakarta.persistence.*;

@Entity
@Table(name = "bmi")
public class Bmi {
    /*
       Author: Azamat Afzalov
       Matriculation number: 1492864
       Date: 05.11.2023
    */
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Double weight;
    private Double height;
    private String phone;
    private Double bmi;

    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Double getWeight() {
        return weight;
    }
    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getHeight() {
        return height;
    }
    public void setHeight(Double height) {
        this.height = height;
    }

    public String getPhone() {return phone;}
    public void setPhone(String phone) {this.phone = phone;}

    public Double getBmi() {return bmi;}
    public void setBmi(Double bmi) {this.bmi = bmi;}

    @Override
    public String toString() {
        return "Bmi{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", weight=" + weight +
                ", height=" + height +
                ", phone=" + phone +
                '}';
    }




}
