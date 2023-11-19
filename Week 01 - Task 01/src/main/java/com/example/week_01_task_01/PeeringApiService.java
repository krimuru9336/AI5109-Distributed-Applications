/**
 * @author Lucas Immanuel Nickel
 * @matriculation 1318441
 * @date 2023-11-04
 */

package com.example.week_01_task_01;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * This service class is responsible for interacting with an external API for peering data retrieval.
 */
@Service
public class PeeringApiService {
    private final RestTemplate restTemplate;

    /**
     * Constructor for the PeeringApiService class.
     *
     * @param restTemplate The RestTemplate instance to be injected for making HTTP requests.
     */
    public PeeringApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetches data from the external API.
     *
     * @return ResponseEntity A response entity containing the fetched data.
     */
    public ResponseEntity<String> fetchDataFromApi() {
        String apiUrl = "https://www.peeringdb.com/api/ix/31";
        return restTemplate.getForEntity(apiUrl, String.class);
    }
}
