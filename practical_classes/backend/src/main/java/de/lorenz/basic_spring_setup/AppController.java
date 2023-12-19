package de.lorenz.basic_spring_setup;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author: Lorenz Hohmann (ID: 1259904)
 * @Date: 01.11.2023
 */
@Controller
@RequestMapping("/")
@CrossOrigin(origins = "http://20.8.90.134/")
public class AppController {

  @GetMapping
  public String showIndex(Model model) {

    // setup database scheme
    this.setupDatabaseScheme();

    // get entries from database
    List<Person> persons = this.getEntriesFromDatabase();
    model.addAttribute("persons", persons);

    // add empty person to model
    model.addAttribute("person", new Person());

    return "index";
  }

  /**
   * @Author: Lorenz Hohmann (ID: 1259904)
   * @Date: 01.11.2023
   */
  @PostMapping
  public String postForm(@ModelAttribute Person person, Model model) {
    // connect form data with model
    model.addAttribute("person", person);

    // add entry to database
    this.addEntryToDatabase(person);

    // get entries from database
    List<Person> persons = this.getEntriesFromDatabase();
    model.addAttribute("persons", persons);

    return "index";
  }

  /**
   * This method is called to setup the database scheme if it does not exists.
   * 
   * @Author: Lorenz Hohmann (ID: 1259904)
   * @Date: 01.11.2023
   */
  private void setupDatabaseScheme() {
    try {
      Connection connection = DatabaseConnection.getConnection();
      Statement statement = connection.createStatement();

      // check if persons table exists
      String sql = "SHOW TABLES LIKE 'persons'";
      if (!statement.executeQuery(sql).next()) {
        // create table
        sql = "CREATE TABLE persons (id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(255), phone VARCHAR(255))";
        statement.executeUpdate(sql);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method is called to add an entry to the database.
   * 
   * @Author: Lorenz Hohmann (ID: 1259904)
   * @Date: 01.11.2023
   */
  private void addEntryToDatabase(Person person) {
    try {
      Connection connection = DatabaseConnection.getConnection();
      Statement statement = connection.createStatement();

      // insert entry
      String sql = "INSERT INTO persons (name, phone) VALUES ('" + person.getName() + "', '" + person.getPhone() + "')";
      statement.executeUpdate(sql);

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method is called to get all entries from the database.
   * 
   * @Author: Lorenz Hohmann (ID: 1259904)
   * @Date: 01.11.2023
   */
  private List<Person> getEntriesFromDatabase() {
    List<Person> persons = new ArrayList<Person>();
    try {
      Connection connection = DatabaseConnection.getConnection();
      Statement statement = connection.createStatement();

      // get entries
      String sql = "SELECT * FROM persons";
      ResultSet rs = statement.executeQuery(sql);

      while (rs.next()) {
        persons.add(new Person(rs.getString("name"), rs.getString("phone")));
      }

    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
    return persons;
  }
}
