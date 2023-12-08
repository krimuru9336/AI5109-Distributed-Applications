package com.week5.backend;

import org.springframework.data.repository.CrudRepository;

public interface StudentRepository extends CrudRepository<Students, Long> {
}