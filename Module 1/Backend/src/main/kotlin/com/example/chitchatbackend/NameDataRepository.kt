package com.example.chitchatbackend

import org.springframework.data.repository.CrudRepository


interface NameDataRepository : CrudRepository<NameData?, Long?> {
    fun findTopByOrderByIdDesc(): NameData?
}

