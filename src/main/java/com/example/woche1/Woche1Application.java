package com.example.woche1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SpringBootApplication
public class Woche1Application {

  public static void main(String[] args) {
    SpringApplication.run(Woche1Application.class, args);
  }

}

@RestController
@CrossOrigin
class FormController {

  @Autowired
  private FormDataRepository formDataRepository;
  private final WeatherAPIService weatherAPIService;

  FormController(WeatherAPIService weatherAPIService) {
    this.weatherAPIService = weatherAPIService;
  }

  @PostMapping("/submit-form")
  public void receiveFormData(@RequestBody FormData formData) {
    System.out.println("Received Name: " + formData.getName());
    System.out.println("Received Phone: " + formData.getPhone());
    formDataRepository.save(formData);
  }

  @GetMapping("/get-form-data")
  public List<FormData> getDataFromDatabase() {
    List<FormData> formDataList = (List<FormData>) formDataRepository.findAll();
    return formDataList;
  }

  @GetMapping("/get-weather")
  public ResponseEntity<Object> getWeather() {
    Object responseData = weatherAPIService.callAPI();
    return ResponseEntity.ok(responseData);
  }


}