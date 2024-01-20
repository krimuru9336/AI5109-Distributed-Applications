# WhatsDown Chat App

Welcome to the WhatsDown repository, a real-time chatting app that allows users to connect and communicate seamlessly. The app is built using Android Studio with Kotlin for the mobile app and Firebase Cloud Firestore for real-time communication.

## Technology Used
- **Mobile App:** Android Studio with Kotlin.
- **Real-time Communication:** Firebase Cloud Firestore.

## Features

### User Authentication
WhatsDown incorporates a user authentication system that enables users to create accounts, log in securely, and authenticate their identity within the chat application.

### Real-time Message Communication
Users can send and receive text messages in real-time. Each message is accompanied by a timestamp, providing a dynamic and interactive chatting experience.

### TODO: Upcoming features: Media Content Support, Edit and Delete Messages, Group Chats, Notifications

## Logic

### User Authentication
- Firebase authentication API handles the registration process where users can register by providing their phone number.
- Upon registration, Firebase Authentication sends an SMS message containing a one-time code to the user's phone.
- Registered users can log in using their credentials, and Firebase authentication verifies the user's identity.

### Real-time Message Communication
- Users can create or join chat rooms, and chat messages are associated with specific rooms.
- The frontend sends new messages to Firebase Cloud Firestore.
- The backend listens for changes in the database and pushes updates to all connected clients.
- The frontend updates the UI in real-time when new messages are received.

### Message Structure
- Users can send text messages and emojis as a message. 
- Both the sender and receiver would see the message content along with the time the respective message got sent.
- In the UI, sender would see their messages on the right side and the other's messages on the left.

## Project Structure

### Activity Files
- **ChatActivity.java:** Main activity for individual chat conversations.
- **LoginOtpActivity.java:** Handles user authentication using OTP.
- **LoginPhoneNumberActivity.java:** Manages phone number-based user login.
- **LoginUsernameActivity.java:** Controls user login using a username.
- **MainActivity.java:** App's entry point and primary navigation hub.
- **SearchUserActivity.java:** Allows users to search for other users to initiate chats.
- **SplashActivity.java:** Displays a splash screen while the app initializes.

### Fragment Files
- **ChatFragment.java:** Manages chat UI and logic within the chat activity.
- **ProfileFragment.java:** Handles user profile display and editing.

## Getting Started

1. Clone or download the repository.
2. Set up your Firebase project and update the google-services.json file.
3. Build and run the app on your Android device or emulator.

Enjoy!
