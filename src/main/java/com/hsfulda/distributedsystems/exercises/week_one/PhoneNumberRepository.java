package com.hsfulda.distributedsystems.exercises.week_one;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhoneNumberRepository extends JpaRepository<PhoneNumberDBEntity, Integer> {

}
