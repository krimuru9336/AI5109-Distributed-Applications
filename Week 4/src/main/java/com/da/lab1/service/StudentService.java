package com.da.lab1.service;

import java.util.List;

import com.da.lab1.model.StudentModel;

public interface StudentService {
	
	List<StudentModel> getStudents();
	void addStudent(StudentModel student);

}
