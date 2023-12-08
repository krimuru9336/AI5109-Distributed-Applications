package com.example.bmi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Oshadhi Samarasinghe
 * @date 2023-12-05 
 * @MatriculationNumber 1458992
 */

@JsonIgnoreProperties
public class BmiBeanDto {
    private long id;
    private double height;
    private double weight;
    private String name;

    private double bmi;

    public BmiBeanDto() {
    }

    public BmiBeanDto(double height, double weight, String name) {
        this.name = name;
        this.height = height;
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getBmi() {
        return bmi;
    }

    public void setBmi(double bmi) {
        this.bmi = bmi;
    }
}
