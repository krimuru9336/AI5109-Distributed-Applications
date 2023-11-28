package com.example.bmi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BmiService {
    /*
       Author: Azamat Afzalov
       Matriculation number: 1492864
       Date: 05.11.2023
    */
    private final BmiRepository bmiRepository;
    @Autowired
    public BmiService(BmiRepository bmiRepository) {
        this.bmiRepository = bmiRepository;
    }
    public Bmi create(Bmi body) {
        return this.bmiRepository.save(body);
    }
    public List<Bmi> getAll() {
        return this.bmiRepository.findAll();
    }

    public Double calculateBmi(Double height, Double weight) {
        return (weight) / Math.pow(height / 100, 2);
    }

    public Optional<Bmi> getById(Long id) {
        return this.bmiRepository.findById(id);
    }
}
