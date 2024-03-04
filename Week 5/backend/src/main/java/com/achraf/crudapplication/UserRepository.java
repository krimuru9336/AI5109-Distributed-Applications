/* class purpose:
 * This is an interface that extends CrudRepository, which provides basic CRUD operations for the User entity.
 */
package com.achraf.crudapplication;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}

/*
 * Author: Achraf Boudabous created: 28/10/2023
 */