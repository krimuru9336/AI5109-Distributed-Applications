package com.example.StudentManagement.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.StudentManagement.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>{

}
