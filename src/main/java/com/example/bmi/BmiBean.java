package com.example.bmi;
//POJO / Bean (Container for data from input)

import jakarta.persistence.*;

/* Author: Felix Stumpf
* Created: 03.11.2023 / Distributed Applications
* Matriculation-ID: 1165939
*/

@Entity
@Table(name="bmi")
public class BmiBean {

    @Id
    private String name;


    private double weight;


    private double height;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {this.height = height;}

    public double getBmi(){return this.weight/(this.height*this.height);}

}
