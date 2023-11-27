// Sahan Wijesinghe - 09.11.2023 - 1453575

package com.example.bmi200;

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
public class MeowFactsConsumer {

	private final String apiUrl = "https://meowfacts.herokuapp.com/";
	private final RestTemplate restTemplate;

	@Autowired
	public MeowFactsConsumer(RestTemplate restTemplate) {
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
