package com.example.bmi;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
/**
 * Author: Thomas Niestroj
 * Created: 07.11.2023
 * */
@Entity
@Table(name = "BMIResults")
public class BmiBean {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	double weight;
	double height;
	double bmi;

	public double getBmi() {
		return bmi;
	}

	public void setBmi(double bmi) {
		this.bmi = bmi;
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
}
