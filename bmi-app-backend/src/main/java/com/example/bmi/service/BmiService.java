package com.example.bmi.service;

import com.example.bmi.dto.BmiBeanDto;
import com.example.bmi.model.BmiBean;

import java.util.List;

public interface BmiService {
    public BmiBean saveUser(BmiBean bmiBean, double bmi);

    public double calculateBmi(BmiBean bmiBean);

    public BmiBean getUser(long id);

    public List<BmiBean> getUsers();

}
