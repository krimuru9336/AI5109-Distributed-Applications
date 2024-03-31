# WhatsDown
A real-time chat application facilitating instant communication and seamless conversation between users.

## Table of Contents
- [WhatsDown](#whatsdown)
- [Table of Contents](#table-of-contents)
- [Features](#features)
- [Setup](#setup)

## Features

- **Real-Time Messaging:** Experience seamless and instantaneous communication with our real-time messaging feature, allowing users to exchange messages in the blink of an eye.

- **Edit and Delete Messages:** Modify or remove messages as needed, ensuring accurate and relevant communication.

- **Multimedia Sharing:** Share images, GIFs, or videos within the chat to enhance communication with visual elements.

- **Media Interaction:** Interact with media content by zooming in/out or rotating images and GIFs for better viewing experiences.

- **Group Chat:** Create or join group conversations to connect with multiple users simultaneously.

- **Google Sign-In:** Sign in to the application effortlessly using Google authentication for a secure and convenient login process.

## Setup
Step-by-step instructions on how to use this app.

### Firebase Authentication Configuration:
1. Go to the Firebase Console at [Firebase Console](https://console.firebase.google.com/).
2. Navigate to the Authentication section and select the 'Sign-in method' tab.
3. Enable Google as a sign-in provider.
4. In the Google sign-in provider settings, under Web SDK configuration, copy the Web Client ID.
5. Paste the Web Client ID into the `strings.xml` file in your Android project.

### Firebase Firestore Indexes:
1. In the Firebase Console, go to the Firestore section.
2. Under the Indexes tab, create two indexes:
   - **Messages :**
     - Fields: `groupName` Ascending, `receiver` Ascending, `sender` Ascending, `timestamp` Ascending, `__name__` Ascending.
   - **Messages :**
     - Fields: `groupName` Ascending, `timestamp` Ascending, `__name__` Ascending.

### Firebase Storage Bucket Creation:
1. In the Firebase Console, go to the Storage section.
2. Create a new storage bucket to store media files used within the chat application.

### Obtain SHA-1 & SHA-256 Keys:
1. In Android Studio, open the terminal window.
2. Run `./gradlew signingReport` command.
3. Note down the SHA-1 & SHA-256 keys generated in the output.
4. Go to the Firebase Console, navigate to your project settings.
5. Add these keys in the appropriate fields under the 'Your apps' section.

### Android Studio Setup:
1. Download and install Android Studio.
2. Clone or download the WhatsDown project from the repository.
3. Open the project in Android Studio.
4. Ensure that all necessary dependencies are resolved.
5. Run the app in Android Studio.

By following these steps, you'll set up the necessary configurations for Firebase Authentication, Firestore indexes, and Storage, allowing the WhatsDown chat application to function properly on Android devices.
