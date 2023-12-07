/*
 * Author: Christian Jumtow
 * Created: 09.11.2023
 * MNr.: 1166358
 */

package com.example.bmi;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    private final ApiService apiService;

    public ApiController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping("/api-call")
    public String performApiCall() {
        ResponseEntity<String> apiResponse = apiService.makeApiCall();
        String responseString = "Statuscode: " + apiResponse.getStatusCode() + "<br><br>Header: " + apiResponse.getHeaders() + "<br><br>Body: " + apiResponse.getBody();

        return responseString;
    }
}