package com.example.chitchatbackend

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@CrossOrigin
internal class NameController {
    @Autowired
    private val nameDataRepository: NameDataRepository? = null

    @PostMapping("/name")
        fun submitName(@RequestBody formData: NameData) {
        System.out.println("Received Name: " + formData.name)
        nameDataRepository?.save(formData)
    }

    @GetMapping("/name")
    fun getName(): Any? {
        val name = nameDataRepository?.findTopByOrderByIdDesc() ?: return ResponseEntity.status(500).body("No name in database")
        return name
    }
}