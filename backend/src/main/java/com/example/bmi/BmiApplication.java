package com.example.bmi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
/**
 * Author: Thomas Niestroj
 * Created: 07.11.2023
 * */
@SpringBootApplication
@ComponentScan({"DogApi"})
@ComponentScan({"com.example.bmi"})
public class BmiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BmiApplication.class, args);
	}
}
