package com.hsfulda.distributedsystems.misc;

import jakarta.persistence.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BmiRepository extends JpaRepository<BmiDBEntity, Integer> {

}
