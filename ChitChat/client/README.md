# ChitChat Documentation

## Overview

ChitChat is a user-friendly chat application that allows individuals to connect seamlessly. With features like real-time messaging, media sharing, and group chats, ChitChat fosters dynamic communication. Users can edit and delete messages, share multimedia content.

## Getting Started

To run ChitChat application on your machine, follow these steps:

1. **Installation:**
    - Navigate to the project directory (ChitChat).

2. **Prerequisites:**
    - Any IDE.
    - MySQL Installed

3. **Setup:**
    - Run the SQL queries below in MySQL:
    - CREATE DATABASE `chitchat`;
    - CREATE TABLE `messages` ( `id` int NOT NULL AUTO_INCREMENT, `message` varchar(255) NOT NULL, `sender_id` int NOT NULL, `receiver_id` int NOT NULL, `timestamp` varchar(255) DEFAULT NULL, PRIMARY KEY (`id`));
    - CREATE TABLE `users` (`id` int NOT NULL AUTO_INCREMENT, `email` varchar(45) NOT NULL, `name` varchar(45) NOT NULL,PRIMARY KEY (`id`), UNIQUE KEY `email_UNIQUE` (`email`));

    - Open the project in Visual Studio or any other IDE.

    - Navigate to the server directory
    - Run the command "pip install -r requirements.txt"
    - After the packages are installed run the command "python3 main.py", to start the server

    - Navigate to the client directory
    - Run the command "npm i"
    - Afer the packages are installed run the command "npx expo start --tunnel", to start the client
    - Install Expo Go on your phone from App Store/Play Store, and scan the QR code displayed on the terminal to open the application on your phone
    

## Features

### User Register

- Users have to register for the first time with a unique mail and the session is stored in the localstorage for future user.
- After Registration the user is navigated to the users screen where the a list of all the registered users is displayed.
- The User can click and start the chat with any user

### Real-Time Messaging

 - React Native components render dynamic conversations. 
 - FastAPI manages WebSocket connections for real-time updates. 
 - Messages are processed, stored, and retrieved from the SQL database.

### Chats History

- History of past messages is maintained using MySQL


## Development Notes

### Backend Integration

- FastAPI is a modern, fast (high-performance), web framework for building APIs with Python.

### Message Handling

- WebSockets are used with FastAPI for real-time updates
- Messages are displayed in a custom Chat Component.

### Error Handling

- Toast messages for error alerts.

## Further Development

- Support edit message, delete message, videos, images, gifs and group chats 