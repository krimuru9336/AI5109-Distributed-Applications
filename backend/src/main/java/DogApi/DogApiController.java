package DogApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Author: Thomas Niestroj (MatrikelNr: 142396)
 * Created: 08.11.2023
 * */
@RestController
@RequestMapping("/dogs")
public class DogApiController {

    private final String apiUrl = "https://dog.ceo/api/breeds/image/random";
    private final RestTemplate restTemplate;

    @Autowired
    public DogApiController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/random")
    public ResponseEntity<DogApiResponse> getRandomDogImage() {
        // Make a GET request to the Dog API
        DogApiResponse response = restTemplate.getForObject(apiUrl, DogApiResponse.class);
        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
