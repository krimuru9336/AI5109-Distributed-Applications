package com.rahulpatil.bmi.controllers;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rahulpatil.bmi.models.CatFact;
import com.rahulpatil.bmi.services.CatService;

@RestController
@RequestMapping("/cat")
public class CatController {
    @Autowired
    CatService catService;

    @GetMapping
    public List<CatFact> getFacts() {
        String jsonResponse = this.catService.fetchFactsFromApi();
        System.out.println("JSON Response: " + jsonResponse);
        ObjectMapper objectMapper = new ObjectMapper();
        List<CatFact> catFactsList = new ArrayList<CatFact>();
        try {
            CatFact[] catFacts = objectMapper.readValue(jsonResponse, CatFact[].class);
            for (CatFact catFact : catFacts) {
                catFactsList.add(catFact);
            }
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return catFactsList;
    }
}
/*
 * Author: Rahul Patil
 * Matriculation Number: 1478745
 * Created: 09.11.2023
 */