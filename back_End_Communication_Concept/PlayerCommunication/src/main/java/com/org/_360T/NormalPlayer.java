package com.org._360T;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * The {@code NormalPlayer} class represents a simple server application that listens for connections on a
 * specific port, accepts messages from a client, and echoes those messages back to the client.
 * This server counts the number of messages received and will stop after receiving 10 messages.
 *
 * <p> Usage:
 * <pre>
 *     java com.org._360T.NormalPlayer
 * </pre>
 * Once started, it will listen on port 5100 for client connections.
 * </p>
 *
 * @author Your Name
 * @version 1.0
 */
public class NormalPlayer {

    /**
     * Logger for this class. It's configured to use the {@code NormalPlayer} class name.
     */
    private static final Logger LOGGER = Logger.getLogger(NormalPlayer.class.getName());

    /**
     * Starts the server listening on port 5100. It accepts connections from clients, reads messages line by line,
     * and echoes each message back to the client with an appended message count. The server stops after receiving
     * 10 messages.
     *
     * @param args Command-line arguments, not used in this application.
     * @throws IOException if an I/O error occurs when opening the socket, waiting for a connection,
     *                     reading from the socket, or writing to the socket.
     */
    public static void main(String[] args) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(5100)) {
            LOGGER.info("Server is listening on port 5100.");
            try (Socket clientSocket = serverSocket.accept()) {
                LOGGER.info("Connection from " + clientSocket.getRemoteSocketAddress());

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                String inputLine;
                int messageCount = 0;

                while ((inputLine = in.readLine()) != null) {
                    LOGGER.info("Normal Player - Received : " + inputLine + "\n");
                    messageCount++;
                    // Echo the message back with the message count
                    out.println(inputLine + " " + messageCount );

                    if (messageCount >= 10) {
                        LOGGER.info("Received 10 messages, stopping.");
                        break;
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.severe("An exception occurred: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            LOGGER.info("ServerPlayer stopped.");
        }
    }
}
