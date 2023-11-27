/*
 * Author: Sahan Wijesinghe (1453575) 
 * Created: 07.11.2023
 */
package com.example.bmi200;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BmiRepository extends JpaRepository<BmiBean, Integer> {

}