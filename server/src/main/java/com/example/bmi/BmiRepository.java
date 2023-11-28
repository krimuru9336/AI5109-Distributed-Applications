package com.example.bmi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BmiRepository extends JpaRepository<Bmi, Long> {
     /*
       Author: Azamat Afzalov
       Matriculation number: 1492864
       Date: 05.11.2023
    */
}
