package com.example.Spring_Week1;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://springbootfrontend.z8.web.core.windows.net/") // Add your frontend domain here
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Add allowed methods
                .allowedHeaders("*"); // Add allowed headers
    }
}
