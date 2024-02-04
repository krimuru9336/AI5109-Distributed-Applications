# Intermediate Chatapp Documentation

## Overview

Chatapp is an intuitive messaging platform designed to facilitate seamless connections between individuals. Offering functionalities such as instant messaging, media exchange, and group discussions, Chatapp encourages dynamic and interactive communication. Users have the ability to modify or remove their messages and share various types of multimedia content.

## Getting Started

To run Chatapp application on your machine, follow these steps:

1. **Installation:**
    - Navigate to the project directory (Chatapp).

2. **Prerequisites:**
    - Any IDE.
    - MySQL Installed

3. **Setup:**
    - Run the SQL queries below in MySQL:
    -CREATE DATABASE `chatapp`;

    -CREATE TABLE `users` ( `id` int NOT NULL AUTO_INCREMENT,`username` varchar(45) NOT NULL,`password` varchar(45) NOT NULL,PRIMARY KEY (`id`)) ;

    -CREATE TABLE `messages` (`id` int NOT NULL AUTO_INCREMENT,`text` varchar(255) NOT NULL,`receiver_id` int NOT NULL,`user_id` int NOT NULL, `timestamp` datetime DEFAULT NULL, PRIMARY KEY (`id`), UNIQUE KEY `id_UNIQUE` (`id`),KEY        `receiver_id` (`receiver_id`),KEY `user_id` (`user_id`),CONSTRAINT `receiver_id` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`),CONSTRAINT `user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`))


    - Open the project in Visual Studio or any other IDE.

    - Navigate to the server directory
    - Run the command "pip install -r requirements.txt"
    - After the packages are installed run the command "python main.py", to start the server

    - Navigate to the client directory
    - Run the command "npm i"
    - After the packages are installed run the command "npx expo start --tunnel", to start the client
    - Install Expo Go on your phone from App Store (https://apps.apple.com/us/app/expo-go/id982107779) / Play Store (https://play.google.com/store/apps/details?id=host.exp.exponent&hl=en&gl=US), and scan the QR code displayed on the terminal to open the application on your phone
    

## Features

### User Registration

- Users have to register for the first time with a unique name and the session is stored in the localstorage for future use.
- After Registration the user is navigated to the users screen where the list of all the registered users is displayed.
- The User can click and start the chat with any user present in the user list screen

### Real-Time Messaging

-Dynamic conversations are rendered using React Native components.
-WebSocket connections managed by FastAPI enable real-time updates.
-Messages are processed, stored, and retrieved from the SQL database.

### Chats History

- History of past messages is maintained using MySQL 


## Development Notes

### Backend Integration

- FastAPI is a modern, fast (high-performance), web framework for building APIs with Python.

### Message Handling

- WebSockets are used with FastAPI for real-time updates
- Messages are displayed in a custom Personalchat Component.
-Gifted Chat library is commonly used to support real-time updates, multimedia sharing, and consistent user experience.

### Error Handling

- Toast messages for error alerts.

## Further Development

- Planned features include support for editing and deleting messages, sharing videos, images, GIFs, and the introduction of group chats
