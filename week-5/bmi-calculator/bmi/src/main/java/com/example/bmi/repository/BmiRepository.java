package com.example.bmi.repository;

import com.example.bmi.BmiBean;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BmiRepository extends JpaRepository <BmiBean, Long>{
}
