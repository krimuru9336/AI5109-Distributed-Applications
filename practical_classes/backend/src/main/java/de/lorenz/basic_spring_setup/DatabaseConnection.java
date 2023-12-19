package de.lorenz.basic_spring_setup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @Author: Lorenz Hohmann (ID: 1259904)
 * @Date: 01.11.2023
 */
public class DatabaseConnection {

  private static final String JDBC_URL = "jdbc:mysql://localhost:3306/master_da_ex1";
  private static final String USER = "username";
  private static final String PASSWORD = "secret";

  public static Connection getConnection() {
    try {
      return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    } catch (SQLException e) {
      e.printStackTrace();
      throw new RuntimeException("Failed to connect to the database.");
    }
  }

}
