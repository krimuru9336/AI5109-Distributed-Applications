package com.example.bmi.service;

import com.example.bmi.model.BmiBean;
import com.example.bmi.repository.BmiRepository;
import org.springframework.stereotype.Service;

/**
 * @author Oshadhi Samarasinghe
 * @date 2023-11-03
 */

@Service
public class BmiServiceImpl implements  BmiService{

    BmiRepository bmiRepository;

    public BmiServiceImpl(BmiRepository bmiRepository) {
        this.bmiRepository = bmiRepository;
    }

    @Override
    public void saveUser(BmiBean bmiBean, double bmi) {
        bmiBean.setBmi(bmi);
        bmiRepository.save(bmiBean);

    }

    @Override
    public double calculateBmi(BmiBean bmiBean) {
        if(bmiBean.getHeight() > 0.0  && bmiBean.getWeight() > 0.0){
            double height = bmiBean.getHeight()*0.01; // height in meters
            double weight = bmiBean.getWeight();
            return weight / (height*height);
        }else{
            return 0.0;
        }

    }


    @Override
    public BmiBean getUser(long id) {
        return bmiRepository.findById(id).orElse(new BmiBean());
    }
}
