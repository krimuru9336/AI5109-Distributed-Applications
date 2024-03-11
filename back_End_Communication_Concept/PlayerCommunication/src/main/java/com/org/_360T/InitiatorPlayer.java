package com.org._360T;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

/**
 * The {@code InitiatorPlayer} class acts as a client that initiates communication with a server.
 * It connects to a server on a specified port and sends messages received from the standard input.
 * It also receives and logs messages echoed back from the server.
 * The communication continues until 10 messages have been sent.
 */
public class InitiatorPlayer {

    /**
     * Logger for the {@code InitiatorPlayer} class, used to log informational and error messages.
     */
    private static final Logger LOGGER = Logger.getLogger(InitiatorPlayer.class.getName());

    /**
     * The entry point of the {@code InitiatorPlayer} application.
     * It establishes a connection to the server, sends user-input messages, and processes server responses.
     * The application exits upon sending 10 messages or if an error occurs during communication.
     *
     * @param args Command-line arguments, not used in this application.
     */
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5100)) {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            String userInput;
            int messageCount = 0;
            LOGGER.info("Connected to server. \n Initiator Player: Type your messages: ");

            while ((userInput = stdIn.readLine()) != null && messageCount < 10) {
                out.println(userInput + "" );
                LOGGER.info("Initiator Player - Response: " + in.readLine() + "\n");
                messageCount++;

                if (messageCount >= 10) {
                    LOGGER.info("Sent 10 messages, stopping.");
                    break;
                }
            }
        } catch (UnknownHostException e) {
            LOGGER.severe("Don't know about host: " + e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            LOGGER.severe("Couldn't get I/O for the connection: " + e.getMessage());
            System.exit(1);
        }
    }
}
