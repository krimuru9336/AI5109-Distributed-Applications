package com.example.bmi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/* Author: Felix Stumpf
* Created: 03.11.2023 / Distributed Applications
* Matriculation-ID: 1165939
*/

@Controller
public class BmiController {
    @Autowired
    private BmiBeanRepository repository;

    @GetMapping({ "/","bmis"})
    public String getTable(Model model){

        List<BmiBean> bmiTable = repository.findAll();
        model.addAttribute("bmis", bmiTable);

        try {
            getRandomDuck(model);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "index";
    }

    @GetMapping("/bmi")
    public String bmiForm(Model model){
        model.addAttribute("bmi", new BmiBean());
        System.err.println(model.getAttribute("bmi"));
        return "bmi";
    }
    @PostMapping("/bmi")
    public String bmiSubmit(@ModelAttribute BmiBean bmi, Model model){
        model.addAttribute("bmi", bmi);
        System.err.println(model.getAttribute("bmi"));
        System.err.println(bmi.getName());
        repository.save(bmi);
        getTable(model);
        return "index";
    }


    @Value("${duck.api.url}")
    private String duckApiUrl;

    public void getRandomDuck(Model model) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<DuckApiResponse> apiResponse = restTemplate.getForEntity(duckApiUrl, DuckApiResponse.class);

        String duckImg = Objects.requireNonNull(apiResponse.getBody()).getUrl();

        int statusCode = apiResponse.getStatusCode().value();
        String headers = apiResponse.getHeaders().toString();

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(apiResponse.getBody());

        model.addAttribute("duckUrl", duckImg);
    }

}
