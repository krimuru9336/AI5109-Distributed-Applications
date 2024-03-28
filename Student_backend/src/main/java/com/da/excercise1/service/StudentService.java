package com.example.StudentManagement.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.StudentManagement.model.Student;
import com.example.StudentManagement.repository.StudentRepository;

@Service
public class StudentServiceImplement implements StudentService {
	
	@Autowired
	private StudentRepository stuRepo;
	

	@Override
	public List<Student> getAllStudents() {
		return stuRepo.findAll();
		
	}


	@Override
	public void addStudent(Student student) {
		this.stuRepo.save(student);
		
	}
	

}