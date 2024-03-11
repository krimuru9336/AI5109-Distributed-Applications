package com.org._360T.service;

import com.org._360T.pojo.Players;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * The {@code PlayerCommunicationService} class handles the communication process between two player entities.
 * It uses the {@code Players} class to create two separate players and manages the exchange of messages between them.
 * The communication process is conducted through the console, where messages are entered by the user.
 *
 * <p>The service logs the messages sent and received and terminates once a specified limit of exchanged messages is reached.</p>
 */
public class PlayerCommunicationService {

    /**
     * Logger for this service class. It's configured to use the {@code PlayerCommunicationService} class name.
     */
    private static final Logger LOGGER = Logger.getLogger(PlayerCommunicationService.class.getName());

    /**
     * Facilitates the sending and receiving of messages between two players.
     * It prompts the user to enter messages into the console, which are then passed between the two players.
     * The communication continues until the pre-defined message limit is reached for both sending and receiving.
     */
    public void playerCommunication() {
        Scanner scanner = new Scanner(System.in);

        // Create two players
        Players player1 = new Players("Initiator");
        Players player2 = new Players("Player2");

        int messageLimit = 10;
        int messageSentCount = 0;
        int messageReceivedCount = 0;

        while (messageSentCount <= messageLimit) {
            LOGGER.info("Enter a message to send from Initiator to Player2:");
            String message = scanner.nextLine();    //waits for the user to input a live
            player2.receiveMessage(message); // Player2 receives message
            messageSentCount++;

            if (messageReceivedCount <= messageLimit) {
                LOGGER.info("Enter a message to send from Player2 to Initiator:");
                message = scanner.nextLine();
                player1.receiveMessage(message); // Initiator receives message
                messageReceivedCount++;
            }
        }

        LOGGER.info("Message limit reached. Ending communication.");
        scanner.close();
    }
}
