package com.rahulpatil.bmi.services;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.rahulpatil.bmi.models.BmiMapper;
import com.rahulpatil.bmi.models.BmiModel;

@Service
public class BmiService {

    @Autowired
    DataSource dataSource;

    @Autowired
    JdbcTemplate jdbcTemplate;

    public List<BmiModel> getBmiList() {
        return jdbcTemplate.query(
                "select * from bmi",
                new BmiMapper());
    }

    public long addOne(BmiModel bmiModel) {
        long result = jdbcTemplate.update(
                "insert into bmi(name,height,weight,bmi)values(?,?,?,?)",
                bmiModel.getName(),
                bmiModel.getWeight(),
                bmiModel.getHeight(),
                bmiModel.getBmi());
        return result;
    }
}

/*
 * Author: Rahul Patil
 * Matriculation Number: 1478745
 * Created: 05.11.2023
 */