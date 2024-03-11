package com.org._360T.pojo;

import java.util.logging.Logger;

/**
 * The {@code Players} class represents a player in a messaging game.
 * It keeps track of the number of messages received and logs each received message.
 * Each player has a unique name and a counter for the messages they receive.
 */
public class Players {

    /**
     * Logger for the {@code Players} class, used to log informational messages.
     */
    private static final Logger LOGGER = Logger.getLogger(Players.class.getName());

    /**
     * The name of the player.
     */
    private final String name;

    /**
     * The count of messages received by this player.
     */
    private int messageCount = 0;

    /**
     * Constructs a new player with the specified name.
     *
     * @param name The name of the player.
     */
    public Players(String name) {
        this.name = name;
    }

    /**
     * Receives a message and increments the player's message count.
     * The reception of the message is logged along with the player's name.
     *
     * @param message The message received by the player.
     */
    public void receiveMessage(String message) {
        LOGGER.info(this.name + " received: " + message);
        this.messageCount++;
    }

    /**
     * Retrieves the count of messages received by the player.
     *
     * @return The number of messages received.
     */
    public int getMessageCount() {
        return this.messageCount;
    }

    /**
     * Retrieves the name of the player.
     *
     * @return The name of the player.
     */
    public String getName() {
        return this.name;
    }
}
