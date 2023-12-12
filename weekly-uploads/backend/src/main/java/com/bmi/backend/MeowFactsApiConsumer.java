// Sahan Wijesinghe - 05.12.2023 - Mtr Nr 1453575

package com.bmi.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/meow-facts")
public class MeowFactsApiConsumer {

	private final String apiUrl = "https://meowfacts.herokuapp.com/";
	private final RestTemplate restTemplate;

	@Autowired
	public MeowFactsApiConsumer(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@GetMapping("/meow")
	public ResponseEntity<MeowFactsApiResponse> getMeowFacts() {
		ResponseEntity<MeowFactsApiResponse> entity = restTemplate.getForEntity(apiUrl, MeowFactsApiResponse.class);
		HttpStatusCode statusCode = entity.getStatusCode();
		HttpHeaders headers = entity.getHeaders();

		System.out.println("Status Code: " + statusCode);
		System.out.println("Headers: " + headers);

		if (entity.hasBody()) {
			return ResponseEntity.status(statusCode).headers(headers).body(entity.getBody());
		} else {
			return ResponseEntity.status(statusCode).headers(headers).build();
		}
	}
}
