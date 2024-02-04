package com.rahulpatil.bmi.models;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;

public class BmiMapper implements RowMapper<BmiModel> {

    @Override
    @Nullable
    public BmiModel mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new BmiModel(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getFloat("weight"),
                rs.getFloat("height"),
                rs.getFloat("bmi"));
    }

}

/*
 * Author: Rahul Patil
 * Matriculation Number: 1478745
 * Created: 05.11.2023
 */