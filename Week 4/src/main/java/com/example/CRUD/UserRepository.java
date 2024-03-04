/* class purpose:
 * This is an interface that extends CrudRepository, which provides basic CRUD operations for the User entity.
 */
package com.example.CRUD;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}

/*
 * Author: Louay Ben Hadj Said created: 06/11/2023
 */