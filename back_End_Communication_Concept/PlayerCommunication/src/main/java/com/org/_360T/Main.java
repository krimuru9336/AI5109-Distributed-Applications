package com.org._360T;

import com.org._360T.service.PlayerCommunicationService;

/**
 * The {@code Main} class acts as the entry point for the application to start player communications.
 * It initializes the {@code PlayerCommunicationService} and triggers the method to begin the interactive
 * message exchange between two players.
 *
 * <p>This class is designed to be executed from the command line and does not accept any arguments.
 * When run, it starts a simple text-based communication session between the players.</p>
 */
public class Main {

    /**
     * The main method that initializes the communication service and starts the player communication process.
     *
     * @param args Command-line arguments, which are ignored in this application.
     */
    public static void main(String[] args) {
        // Create an instance of the communication service
        PlayerCommunicationService playerCommunicationService = new PlayerCommunicationService();

        // Start the player communication session
        playerCommunicationService.playerCommunication();
    }
}
