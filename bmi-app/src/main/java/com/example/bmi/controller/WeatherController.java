package com.example.bmi.controller;

import com.example.bmi.model.WeatherResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author Oshadhi Samarasinghe
 * @date 2023-11-04
 */

@Controller
public class WeatherController {
    @GetMapping("/weather")
    @ResponseBody
    public WeatherResponse getWeatherData(@RequestParam("lat") String lat,
                                          @RequestParam("lon") String lon)
    {
        RestTemplate restTemplate = new RestTemplate();
        String baseUri = "https://api.openweathermap.org/data/2.5/weather";
        String apiKey = "1ac4af2416275b8eaaba06cd1ec8d436";
        String apiUrl = UriComponentsBuilder
                .fromUriString(baseUri)
                .queryParam("lat", lat)
                .queryParam("lon", lon)
                .queryParam("appid", apiKey)
                .build()
                .toUriString();

       // Make a GET request and retrieve the response
        return restTemplate.getForObject(apiUrl, WeatherResponse.class);

    }
}
