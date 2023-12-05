// Sahan Wijesinghe - 05.12.2023 - Mtr Nr 1453575
package com.bmi.backend;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@Table(name = "cat_bmi")
public class BmiBean {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;	
	private String name;
	private String gender;
	private double ribCage;
	private double legLength;
	private double bmi;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public double getRibCage() {
		return ribCage;
	}
	public void setRibCage(double ribCage) {
		this.ribCage = ribCage;
	}
	public double getLegLength() {
		return legLength;
	}
	public void setLegLength(double legLength) {
		this.legLength = legLength;
	}
	public double getBmi() {
		return bmi;
	}
	public void setBmi(double bmi) {
		this.bmi = bmi;
	}
}