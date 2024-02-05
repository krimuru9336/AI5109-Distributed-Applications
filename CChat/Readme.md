# CChat - Android Chat Application

CChat is a messaging application developed for Android, leveraging Firebase services such as Firestore for real-time database, Firebase Authentication for user authentication, and Firebase Cloud Messaging for push notifications.

## Features

- **Real-Time Messaging**: Enjoy seamless real-time messaging with other users.
- **User Authentication**: Securely register and log in using Firebase Authentication.
- **Cloud Storage**: Store and retrieve messages using Firebase Firestore, ensuring data consistency and reliability.
- **Push Notifications**: Stay informed with Firebase Cloud Messaging, receiving instant notifications for new messages.

## Getting Started

Follow these steps to set up and run the CChat application:

1. **Set Up Firebase Project**:
    - Create a new project on the [Firebase Console](https://console.firebase.google.com/).
    - Add your Android app to the project and follow the setup instructions to download the `google-services.json` file.
    - Enable Firebase Authentication, Firestore, and Cloud Messaging services.

2. **Update Configuration**:
    - Replace the `google-services.json` file in the `app` directory with your downloaded file.
    - Update the `google-services.json` with your Firebase project configurations.

3. **Build and Run**:
    - Open the project in Android Studio.
    - Build and run the application on an Android emulator or a physical device.

## Dependencies

CChat uses the following key dependencies:

- [Firebase Authentication](https://firebase.google.com/docs/auth) for user authentication.
- [Firebase Firestore](https://firebase.google.com/docs/firestore) for real-time database.
- [Firebase Cloud Messaging](https://firebase.google.com/docs/cloud-messaging) for push notifications.

For a complete list of dependencies, refer to the `build.gradle` files in the project.


Happy Chatting! 🚀
