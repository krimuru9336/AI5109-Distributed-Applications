package com.week5.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}


@RestController
@CrossOrigin
class FormController {

  @Autowired
  private StudentRepository studRepository;



  @PostMapping("/addStudent")
  public void receiveFormData(@RequestBody Students studentData) {
    System.out.println("Received Name: " + studentData.getName());
    System.out.println("Received Phone: " + studentData.getContact());
    studRepository.save(studentData);
  }

  @GetMapping("/getStudents")
  public List<Students> getDataFromDatabase() {
    List<Students> formDataList = (List<Students>) studRepository.findAll();
    return formDataList;
  }
}