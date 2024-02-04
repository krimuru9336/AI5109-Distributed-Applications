package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getUsers() {
        Iterable<User> usersIterable = userRepository.findAll();
        List<User> usersList = new ArrayList<>();
        usersIterable.forEach(usersList::add); // Convert Iterable to List
        return usersList;
    }
    // Other business logic methods
}
