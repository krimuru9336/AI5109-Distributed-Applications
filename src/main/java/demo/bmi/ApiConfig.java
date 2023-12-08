package demo.bmi;

/*
 * Author: Mohammed Amine Malloul
 * Created 05/11/2023
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApiConfig {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
