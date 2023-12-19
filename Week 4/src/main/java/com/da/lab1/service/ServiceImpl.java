package com.da.lab1.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.da.lab1.model.StudentModel;
import com.da.lab1.repository.StudentRepository;

@Service
public class ServiceImpl implements StudentService {
	
	@Autowired
	private StudentRepository stuRepo;
	

	@Override
	public List<StudentModel> getStudents() {
		return stuRepo.findAll();
		
	}


	@Override
	public void addStudent(StudentModel student) {
		this.stuRepo.save(student);
	}
	

}
