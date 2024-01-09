# WhatsDown App Documentation

## Overview

The WhatsDown app is designed to facilitate seamless communication between users. It provides a chat interface allowing users to exchange messages in real-time.

## Getting Started

To get started with the WhatsDown app, follow these steps:

1. **Installation:**
    - Clone the repository: `git clone https://github.com/whatsdown/whatsdown-app.git`
    - Navigate to the project directory.

2. **Prerequisites:**
    - Android Studio installed.
    - Android SDK installed.
    - Basic understanding of Android app development.

3. **Setup:**
    - Open the project in Android Studio.
    - Start the Backend and ensure the correct URL is set.
    - Build and run the app on an Android device or emulator.

## Features

### Chat Interface

The main functionality of the app is the chat interface:

- Users can exchange text messages.
- Messages are displayed in a chat-style interface.
- Real-time message updates through periodic API calls.

### User Login

- User login is simulated meaning everyone can choose each user.
- Selection of users available from the dropdown menu.
- Logging in navigates the user to the chats screen showing all available users besides the logged in user.

### Chats List

- All users are listed and can be choosen to chat with them.
- The logged in user is not shown.

## Development Notes

### Backend Integration

- Utilizes Retrofit for handling API calls.
- Retrofit converters are set up for JSON parsing.

### Message Handling

- Uses Handler for periodic message updates.
- Messages are displayed in a custom LinearLayout.

### Error Handling

- Toast messages for error alerts.
- Stack trace output for debugging and error tracking.

## Further Development

- Implementing features for deleting and editing messages
- Support videos, images, gifs and group chats 
