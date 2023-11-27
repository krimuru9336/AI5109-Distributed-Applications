package bmidemo.demo;

import java.util.ArrayList;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

// Name : Shyam Joshi
// Date : 7/11/2023
// Matriculation number 1482098

@Entity
public class countryData {
    @Getter @Setter public Name name;
    @Getter @Setter public boolean independent;
    @Getter @Setter public String status;
    @Getter @Setter public boolean unMember;
    @Getter @Setter public ArrayList<String> capital;
    @Getter @Setter public String region;
    @Getter @Setter public String subregion;
    @Getter @Setter public boolean landlocked;
    @Getter @Setter public double area;
    @Getter @Setter public int population;
}

class details{
    
}

// import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
// import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root[] root = om.readValue(myJsonString, Root[].class); */


class Name{
    @Getter @Setter public String common;
    @Getter @Setter public String official;
}



