package com.example.bmi.service;

import com.example.bmi.BmiBean;
import com.example.bmi.repository.BmiRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

//author - Seshenya Weerasinghe
//date - 07.12.2023-->
//matriculation number - 1454176
@Service
public class BmiService {
    BmiRepository bmiRepository;

    public BmiService(BmiRepository bmiRepository) {
        this.bmiRepository = bmiRepository;
    }

    public BmiBean saveUser(BmiBean bmiBean) {
        return bmiRepository.save(bmiBean);
    }

    public List<BmiBean> getAllBmiData() {
        return bmiRepository.findAll();
    }

}
