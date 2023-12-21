package demo.bmi;

/*
 * Author: Mohammed Amine Malloul
 * Created 05/11/2023
 */
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiService {


    @Value("${api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public ApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String fetchDataFromApi() {

            // Make the request
            return restTemplate.getForObject(apiUrl, String.class);
            
    }
}