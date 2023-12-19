package de.lorenz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import de.lorenz.models.Name;

@RestController
@RequestMapping("/api/name")
public class NameController {

    @Autowired
    private NameRepository nameRepository;

    @PostMapping
    public ResponseEntity<Object> saveName(@RequestBody Name newName) {
        if (newName.getName() == null || newName.getName().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Name cannot be empty");
        }

        // Delete existing names
        nameRepository.deleteAll();

        // Save the new name
        nameRepository.save(newName);

        // return name
        return ResponseEntity.status(HttpStatus.OK).body(newName);
    }

    @GetMapping
    public ResponseEntity<Object> getName() {
        // get all entries limit 1
        Iterable<Name> names = nameRepository.findAll();
        Name storedName = names.iterator().next();

        if (storedName == null || storedName.getName() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No name found");
        }

        // return name as JSON
        return ResponseEntity.status(HttpStatus.OK).body(storedName);
    }
}
