package com.example.bmi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BmiCalculatorApplication {

	public static void main(String[] args) {
		System.err.println("Test Main");
		SpringApplication.run(BmiCalculatorApplication.class, args);
	}

}
