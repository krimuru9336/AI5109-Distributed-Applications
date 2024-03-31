# Whisprly
A cutting-edge chat application designed for instant messaging and fluid conversations between users.

## Contents

- [Whisprly](#Whisprly)
  - [Contents](#contents)
  - [Key Features](#key-features)
  - [Installation Guide](#installation-guide)

## Key Features

1. **Instantaneous Messaging:** Engage in conversations with immediate message delivery, making every exchange swift and efficient.

2. **Message Management:** Have the flexibility to edit or delete messages, ensuring your conversations remain precise and up-to-date.

3. **Share Multimedia:** Enhance discussions by sharing photos, GIFs, or videos directly in the chat.

4. **Interactive Media:** Easily interact with shared media through zooming and rotating functions, offering a richer viewing experience.

5. **Group Conversations:** Participate in or start group chats to communicate with several users at once.

6. **Easy Google Sign-In:** Utilize Google authentication for a quick and secure access to the application.

## Installation Guide

Follow these steps to get started with WhatsDown.

1. **Setting Up Firebase Authentication:**
    - Access the Firebase Console at [Firebase Console](https://console.firebase.google.com/).
    - Head over to the Authentication section and click on the 'Sign-in method' tab.
    - Activate Google as a sign-in method.
    - Copy the Web Client ID found under the Web SDK configuration in the Google sign-in settings.
    - Insert the Web Client ID into your Android project's `strings.xml` file.

2. **Configuring Firestore Indexes:**
    - Navigate to the Firestore area in the Firebase Console.
    - Select the Indexes tab and set up two indexes:
        - Index 1: 
            - Order: `groupName Ascending`, `receiver Ascending`, `sender Ascending`, `timestamp Ascending`, `__name__ Ascending`.
        - Index 2:
            - Order: `groupName Ascending`, `timestamp Ascending`, `__name__ Ascending`.

3. **Creating a Firebase Storage Bucket:**
    - Move to the Storage section in the Firebase Console.
    - Establish a new bucket for storing chat media files.

4. **Acquiring SHA-1 & SHA-256 Keys:**
    - Open the terminal in Android Studio.
    - Execute the command `./gradlew signingReport`.
    - Record the SHA-1 & SHA-256 keys from the output.
    - In the Firebase Console, locate your project settings.
    - Enter these keys in the designated fields within the 'Your apps' area.

5. **Android Studio Preparation:**
    - Install [Android Studio](https://developer.android.com/studio) if you haven't already.
    - Download or clone the WhatsDown project from its repository.
    - Open the project in Android Studio.
    - Confirm that all dependencies are properly integrated.
    - Deploy the app via Android Studio.

These instructions will guide you through the necessary setup for Firebase Authentication, Firestore indexing, and Storage configuration, enabling WhatsDown to operate seamlessly on Android platforms.