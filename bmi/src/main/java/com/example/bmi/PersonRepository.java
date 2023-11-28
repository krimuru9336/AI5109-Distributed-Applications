package com.example.bmi;

import org.springframework.data.jpa.repository.JpaRepository;

/*
 * Author: Adrianus Jonathan Engelbracht
 * Created: 06.07.2023
 * Matrikelnummer: 1151826
 */


public interface PersonRepository extends JpaRepository<Person, Long> {

	// define custom query methods
}
