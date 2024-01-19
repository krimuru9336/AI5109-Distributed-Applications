# Firebase Chat Application for Android

This Android chat application utilizes Firebase Realtime Database for real-time messaging. The frontend is implemented using Java for the logic and XML for the user interface.

## Features

- Real-time messaging
- User authentication with Firebase Authentication
- Simple and intuitive user interface
- Firebase Realtime Database for data storage

## Prerequisites

Before running the application, make sure you have the following:

- Android Studio installed
- Firebase project set up with Realtime Database and Authentication enabled

## Setup

1. Clone the repository:

   ```bash
   git clone https://github.com/your-username/firebase-chat-app.git
   ```

2. Open the project in Android Studio.

3. Connect the project to your Firebase project by adding the `google-services.json` file provided by Firebase. Follow the instructions [here](https://firebase.google.com/docs/android/setup) for more details.

4. Run the application on an Android emulator or a physical device.

## Configuration

Make sure to configure your Firebase project properly:

1. Enable Firebase Authentication and set up the authentication method (e.g., Email/Password).

2. Set up Firebase Realtime Database and adjust the security rules as needed.

## Screenshots

![1st mobile](screenshots/mobile1.jpg =x720)

![2nd mobile](screenshots/mobile2.jpg =x720)

## Project Structure

- `app/src/main/java/com/example/firebasechatapp`: Java code
- `app/src/main/res`: XML layout files

## Dependencies

- Firebase Realtime Database: `com.google.firebase:firebase-database`
- Firebase Authentication: `com.google.firebase:firebase-auth`
- Firebase UI: `com.firebaseui:firebase-ui-auth`

Make sure to check the `build.gradle` file for the latest versions.

