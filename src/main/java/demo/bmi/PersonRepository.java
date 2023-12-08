package demo.bmi;

import org.springframework.data.jpa.repository.JpaRepository;
/**
 * Author: Mohammed Amine Malloul
 * Created 05/11/2023
 */

public interface PersonRepository extends JpaRepository<Person, Integer> {
}