package com.example.bmi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example")
public class BmiCalculatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(BmiCalculatorApplication.class, args);

	}

	//mysql workbench
	// mysql server
}
