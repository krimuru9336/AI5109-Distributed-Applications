package com.example.bmi;

import java.text.DecimalFormat;
/**
 * Author: Thomas Niestroj
 * Created: 07.11.2023
 * */
public class BMICalculator {

	public static double calculate(double heightInCm, double weightInKg) {
		
		// Create a DecimalFormat object with the desired format
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        // Use the format method to round the number to two decimal places
        String formattedNumber = decimalFormat.format(weightInKg / ((heightInCm / 100) *  (heightInCm / 100)));

        // Convert the formatted string back to a double if needed
        double roundedNumber = Double.parseDouble(formattedNumber.replace(",", "."));
		
        return roundedNumber; 
	}
}
