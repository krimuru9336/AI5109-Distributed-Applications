package com.example.Spring_Week1;
import jakarta.persistence.*;

/*
Author : Sheikh Zubeena Shireen
ScreenCaptured Date : 7/11/2023
Matriculation Number : 1492765
 */

@Entity // This tells Hibernate to make a table out of this class
@Table(name = "user") //database table name
public class User { //has three fields for ID, username and phone number
    @Id  //Primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "my_sequence")
    @SequenceGenerator(                     //Ensures sequence starts with initial value 1 and increments 1
            name = "my_sequence",
            sequenceName = "my_sequence",
            allocationSize = 1,
            initialValue = 1)
    private Integer id;
    private String name;
    private String number;

//getters and setters
    public Integer getId() {return id;}
    public void setId(Integer id) {
        this.id = id;
    }
    public String getname() {
        return name;
    }
    public void setname(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
}

/*
Author : Sheikh Zubeena Shireen
ScreenCaptured Date : 7/11/2023
Matriculation Number : 1492765
 */
