// Sahan Wijesinghe - 05.12.2023 - Mtr Nr 1453575
package com.bmi.backend;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BmiRepository extends JpaRepository<BmiBean, Integer> {

}