package com.example.bmi.service;

import com.example.bmi.model.BmiBean;

public interface BmiService {
    public void saveUser(BmiBean bmiBean, double bmi);
    public double calculateBmi(BmiBean bmiBean);

    public BmiBean getUser(long id);
}
