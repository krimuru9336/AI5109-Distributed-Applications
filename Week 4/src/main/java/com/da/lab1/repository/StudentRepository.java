package com.da.lab1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.da.lab1.model.StudentModel;

@Repository
public interface StudentRepository extends JpaRepository<StudentModel, Long>{

}
