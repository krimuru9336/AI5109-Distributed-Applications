package com.rahulpatil.bmi.models;

public class BmiModel {
    int id;
    String name;
    float weight;
    float height;
    float bmi;

    public BmiModel() {
    }

    public BmiModel(int id, String name, float weight, float height, float bmi) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.height = height;
        this.bmi = bmi;
    }

    @Override
    public String toString() {
        return "BmiModel [id=" + id + ", name=" + name + ", weight=" + weight + ", height=" + height + ", bmi=" + bmi
                + "]";
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

    /*
     * Author: Rahul Patil
     * Matriculation Number: 1478745
     * Created: 05.11.2023
     */

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getBmi() {
        return bmi;
    }

    public void setBmi(float bmi) {
        this.bmi = bmi;
    }
}

/*
 * Author: Rahul Patil
 * Matriculation Number: 1478745
 * Created: 05.11.2023
 */