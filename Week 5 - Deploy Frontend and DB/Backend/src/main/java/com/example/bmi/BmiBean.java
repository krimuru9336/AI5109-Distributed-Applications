package com.example.bmi;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "bmi")
public class BmiBean {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private double height;
	private double weight;
	private double bmi;
	
	public BmiBean() {
		
	}

	public BmiBean(String name, double height, double weight) {
		this.name = name;
		this.height = height;
		this.weight = weight;
		if (height != 0) {
			this.bmi = weight / Math.pow(height / 100, 2);
		} else {
			this.bmi = 0;		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public double getBmi() {
		if (bmi == 0 && height != 0) return calcBmi();
		return bmi;
	}

	public void setBmi(double bmi) {
		this.bmi = bmi;
	}
	
	public double calcBmi() {
		if (bmi == 0 && height != 0) this.bmi = weight / Math.pow(height / 100, 2);
		return this.bmi;
	}
}
