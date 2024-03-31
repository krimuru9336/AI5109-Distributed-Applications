# Real-Time Chat App

This is a real-time chat application developed for Android using Java, XML, and Firebase.
Users can sign up, log in, view other users, and start conversations with them. 
Firebase Cloud Messaging (FCM) is used for real-time messaging, and Cloud Firestore is used for data storage.

## Features

- **User Authentication:**
  - Users can sign up with their email and password.
  - Users can log in to their accounts.

- **User Interface:**
  - The app provides a user-friendly interface for seamless navigation.
  - Users can view a list of other registered users.
  - Users can start conversations with other users.

- **Real-Time Messaging:**
  - Firebase Cloud Messaging is used for real-time communication.
  - Users receive instant notifications for new messages.

- **Edit Messages:**
  - Users have the ability to edit their sent messages.
  - To edit a message, users can tap and hold on the message they want to modify, and an option to edit will be presented.
  - Edited messages are updated in real-time for all users in the conversation.

- **Delete Messages:**
  - Users can delete their own messages.
  - To delete a message, users can tap and hold on the message, and a delete option will be displayed.
  - Deleted messages are removed from the conversation in real-time for all participants.

- **Sending and Viewing Media:**
  - Users can send and receive images, videos, and GIFs within conversations.
  - Media files can be viewed or played directly within the chat interface.

- **Group Chats:**
  - Users can create and participate in group chats.
  - Group chat functionality includes editing and deleting messages.


- **Cloud Firestore Database:**
  - User data, chat messages, and other relevant information are stored in Cloud Firestore.

## Getting Started

1. **Clone the Repository:**

2. **Firebase Setup:**
- Create a new project on the [Firebase Console](https://console.firebase.google.com/).
- Add an Android app to your project and follow the setup instructions.
- Download the `google-services.json` file and place it in the `app` directory.

3. **Build and Run:**
- Open the project in Android Studio.
- Build and run the app on an emulator or a physical device.

4. **Sign Up and Log In:**
- Sign up with a new account or log in with an existing account.
- Explore the app and start conversations with other users.

## Dependencies

- Firebase Authentication
- Firebase Cloud Messaging
- Cloud Firestore

## Project Structure

- `app/src/main/java`: Java source code.
- `app/src/main/res`: XML layout files and resources.

## Contributing

If you'd like to contribute to the project, please follow these steps:

1. Fork the repository.
2. Create a new branch for your feature or bug fix.
3. Make your changes and submit a pull request.