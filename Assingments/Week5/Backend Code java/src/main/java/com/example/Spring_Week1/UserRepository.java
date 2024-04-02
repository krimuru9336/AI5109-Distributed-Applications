package com.example.Spring_Week1;

import org.springframework.data.repository.CrudRepository;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface UserRepository extends CrudRepository<User, Integer> {
}


/*
Author : Sheikh Zubeena Shireen
ScreenCaptured Date : 7/11/2023
Matriculation Number : 1492765
 */


