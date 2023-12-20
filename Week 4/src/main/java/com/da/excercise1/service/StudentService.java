package com.da.excercise1.service;

import java.util.List;

import com.da.excercise1.model.StudentModel;

public interface StudentService {
	
	List<StudentModel> getStudents();
	void addStudent(StudentModel student);

}
