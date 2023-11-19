/**
 * @author Lucas Immanuel Nickel
 * @matriculation 1318441
 * @date 2023-11-04
 */

package com.example.week_01_task_01;

import org.springframework.http.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class represents a RESTful API controller for peering data retrieval.
 * It handles HTTP requests related to fetching data from the API.
 */
@RestController
@RequestMapping("/api/peering")
@CrossOrigin(origins = {"http://azure.stinktopf.de", "https://azure.stinktopf.de"})
public class PeeringApiController {

    // ANSI Escape Sequences for Colors
    String red = "\u001B[31m";    // Red
    String resetColor = "\u001B[0m";

    private final PeeringApiService apiService;

    /**
     * Constructor for the PeeringApiController class.
     *
     * @param apiService The PeeringApiService instance to be injected for data retrieval.
     */
    public PeeringApiController(PeeringApiService apiService) {
        this.apiService = apiService;
    }

    /**
     * Handles HTTP GET requests to fetch data from the API.
     *
     * @return ResponseEntity A response entity containing the fetched data or an error message.
     */
    @GetMapping("/fetchData")
    public ResponseEntity<String> fetchDataFromApi() {
        // Use the service to retrieve data from the API, including the response object
        ResponseEntity<String> response = apiService.fetchDataFromApi();

        // Print the status code
        HttpStatusCode statusCode = response.getStatusCode();
        System.out.println("\n" + red + "Status Code: " + resetColor + statusCode);

        // Print response headers
        HttpHeaders responseHeaders = response.getHeaders();
        System.out.println("\n" + red + "Response Headers:" + resetColor);
        responseHeaders.forEach((key, value) -> {
            System.out.println(key + ": " + value);
        });

        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        } else {
            return ResponseEntity.status(response.getStatusCode()).body("Error in the API request");
        }
    }
}