package com.example.bmi.model;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Oshadhi Samarasinghe
 * @date 2023-11-04
 */
// each inner class corresponds to a specific nested object within the JSON Weather response
@Getter
@Setter
public class WeatherResponse {

    public WeatherResponse() {
    }

    private Coord coord;
    private List<Weather> weather;
    private String base;
    private Main main;
    private int visibility;
    private Wind wind;
    private Rain rain;
    private Clouds clouds;
    private long dt;
    private Sys sys;
    private int timezone;
    private int id;
    private String name;
    private int cod;

    // Getters and setters
    @Getter
    @Setter
    static class Coord {
        private double lon;
        private double lat;

    }

    @Getter
    @Setter
    static class Weather {
        private int id;
        private String main;
        private String description;
        private String icon;

    }

    @Getter
    @Setter
    static class Main {
        private double temp;
        private double feels_like;
        private double temp_min;
        private double temp_max;
        private int pressure;
        private int humidity;
        private double sea_level;
        private double grnd_level;

    }

    @Getter
    @Setter
    static class Wind {
        private double speed;
        private int deg;
        private double gust;

        // Getters and setters
    }

    @Getter
    @Setter
    static class Rain {
        private double rain1h;

    }
    @Getter
    @Setter
    static class Clouds {
        private int all;

    }

    @Getter
    @Setter
    static class Sys {
        private int type;
        private int id;
        private String country;
        private long sunrise;
        private long sunset;

    }
}
