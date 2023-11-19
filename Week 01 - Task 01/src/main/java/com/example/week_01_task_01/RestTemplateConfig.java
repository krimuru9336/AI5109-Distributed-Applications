/**
 * @author Lucas Immanuel Nickel
 * @matriculation 1318441
 * @date 2023-11-04
 */

package com.example.week_01_task_01;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * This configuration class provides a bean definition for a RestTemplate.
 */
@Configuration
public class RestTemplateConfig {
    /**
     * Creates and configures a RestTemplate bean.
     *
     * @return RestTemplate An instance of RestTemplate for making HTTP requests.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
