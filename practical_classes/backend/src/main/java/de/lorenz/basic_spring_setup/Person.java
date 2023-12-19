package de.lorenz.basic_spring_setup;

/**
 * @Author: Lorenz Hohmann (ID: 1259904)
 * @Date: 01.11.2023
 */
public class Person {

	private String name;
	private String phone;

	Person() {
	}

	public Person(String name, String phone) {
		this.name = name;
		this.phone = phone;
	}

	public String getName() {
		return this.name;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

}