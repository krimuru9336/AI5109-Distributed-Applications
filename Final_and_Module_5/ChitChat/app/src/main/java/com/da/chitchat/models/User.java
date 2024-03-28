// Sven Schickentanz - fdai7287
package com.da.chitchat.models;

/**
 * Represents a user.
 */
public class User {
    private final String id;
    private final String username;

    /**
     * Constructs a new User object with the specified id and username.
     *
     * @param id       the unique identifier of the user
     * @param username the username of the user
     */
    public User(String id, String username) {
        this.id = id;
        this.username = username;
    }

    /**
     * Returns the id of the user.
     *
     * @return the id of the user
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the username of the user.
     *
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }
}