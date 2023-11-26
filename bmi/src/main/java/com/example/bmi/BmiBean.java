package com.example.bmi;
@Deprecated
public class BmiBean {
	private String name;
	private double height;
	private double weight;
	private double bmi;

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
	
	public double getBmi() {
		bmi = weight/(height*height/10000);
		return bmi;
	}
}
