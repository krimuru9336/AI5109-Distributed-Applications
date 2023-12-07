package com.example.bmi;

/*
 * Author: Christian Jumtow
 * Created: 03.11.2023
 * MNr.: 1166358
 */

import org.springframework.data.jpa.repository.JpaRepository;

public interface BmiEntityRepository extends JpaRepository<BmiEntity, Long> {
}