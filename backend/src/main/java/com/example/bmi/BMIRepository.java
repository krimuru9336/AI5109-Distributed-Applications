package com.example.bmi;

import org.springframework.data.repository.CrudRepository;
/**
 * Author: Thomas Niestroj
 * Created: 07.11.2023
 * */
public interface BMIRepository extends CrudRepository<BmiBean, Integer> {

}