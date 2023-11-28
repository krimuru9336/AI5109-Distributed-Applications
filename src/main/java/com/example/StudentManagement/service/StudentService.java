package com.example.StudentManagement.service;

import java.util.List;

import com.example.StudentManagement.model.Student;

public interface StudentService {
	
	List<Student> getAllStudents();
	void AddStudent(Student student);

}
