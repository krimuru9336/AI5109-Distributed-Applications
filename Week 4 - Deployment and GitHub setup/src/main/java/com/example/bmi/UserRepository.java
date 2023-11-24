package com.example.bmi;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<BmiBean, Long> {

}