# WhatsDown
A real-time chat application facilitating instant communication and seamless conversation between users.

## Table of Contents

- [WhatsDown](#WhatsDown)
  - [Table of Contents](#table-of-contents)
  - [Features](#features)
  - [Setup](#setup)

## Features

1. **Real-Time Messaging:** Experience seamless and instantaneous communication with our real-time messaging feature, allowing users to exchange messages in the blink of an eye.

2. **Edit and Delete Messages:** Modify or remove messages as needed, ensuring accurate and relevant communication.

3. **Multimedia Sharing:** Share images, GIFs, or videos within the chat to enhance communication with visual elements.

4. **Media Interaction:** Interact with media content by zooming in/out or rotating images and GIFs for better viewing experiences.

5. **Group Chat:** Create or join group conversations to connect with multiple users simultaneously.

6. **Google Sign-In:** Sign in to the application effortlessly using Google authentication for a secure and convenient login process.

## Setup

step-by-step instructions on how to use this app.

1. **Firebase Authentication Configuration:**
    - Go to the Firebase Console at [Firebase Console](https://console.firebase.google.com/).
    - Navigate to the Authentication section and select the 'Sign-in method' tab.
    - Enable Google as a sign-in provider.
    - In the Google sign-in provider settings, under Web SDK configuration, copy the Web Client ID.
    - Paste the Web Client ID into the `strings.xml` file in your Android project.

2. **Firebase Firestore Indexes:**
    - In the Firebase Console, go to the Firestore section.
    - Under the Indexes tab, create two indexes:
        - Index 1: 
            - Fields: `groupName Ascending`, `receiver Ascending`, `sender Ascending`, `timestamp Ascending`, `__name__ Ascending`.
        - Index 2:
            - Fields: `groupName Ascending`, `timestamp Ascending`, `__name__ Ascending`.

3. **Firebase Storage Bucket Creation:**
    - In the Firebase Console, go to the Storage section.
    - Create a new storage bucket to store media files used within the chat application.

4. **Obtain SHA-1 & SHA-256 Keys:**
    - In Android Studio, open the terminal window.
    - Run `./gradlew signingReport` command.
    - Note down the SHA-1 & SHA-256 keys generated in the output.
    - Go to the Firebase Console, navigate to your project settings.
    - Add these keys in the appropriate fields under the 'Your apps' section.

5. **Android Studio Setup:**
    - Download and install [Android Studio](https://developer.android.com/studio).
    - Clone or download the WhatsDown project from the repository.
    - Open the project in Android Studio.
    - Ensure that all necessary dependencies are resolved.
    - Run the app in Android Studio.

By following these steps, you'll set up the necessary configurations for Firebase Authentication, Firestore indexes, and Storage, allowing the WhatsDown chat application to function properly on Android devices.