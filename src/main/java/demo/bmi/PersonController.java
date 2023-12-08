package demo.bmi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Author: Mohammed Amine Malloul
 * Created 05/11/2023
 */


@RestController
@CrossOrigin(origins = "https://red-sky-08fa64a03.4.azurestaticapps.net")
public class PersonController {

    @Autowired
    private PersonRepository personRepository;

    @GetMapping("/")
    public ResponseEntity<List<Person>> index() {
        List<Person> persons = personRepository.findAll();
        return ResponseEntity.ok(persons);
    }

    @PostMapping("/add-person")
    public ResponseEntity<String> addPerson(@RequestBody Person newPerson) {
        personRepository.save(newPerson);
        return ResponseEntity.ok("Person added successfully");
    }
}



