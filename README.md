# AI5109-Distributed-Applications

# RahilChatApplication - Firebase Chat Application

## Overview

This Android application is a real-time chat app developed using Android Studio and Firebase Realtime Database. Firebase provides a cloud-based database that allows seamless integration for building real-time applications.

## Features

- **User Authentication:**
  - Users can sign up, log in, and log out securely using Firebase Authentication.
  
- **Realtime Chat Features:**
  - Users can send and receive messages in real-time.
  - Users can edit their message and resend it.
  - Users can delete the message they have sent.
  - Messages are stored in Firebase Realtime Database.
  - User can create a Group
  - User can add himself/herself in the created group
  - All users can see all list of groups
  - User can message a person from the group

- **User List:**
  - Display a list of users.

## Technologies Used

- **Android Studio:**
  - The primary IDE for Android app development.

- **Firebase:**
  - Firebase Authentication: User authentication.
  - Firebase Realtime Database: Store and sync data in real-time.

- **Kotlin:**
  - Programming language used for Android app development.

## Project Structure

- **`app/` folder:**
  - Contains the main source code for the Android application.

- **`app/src/main/res/` folder:**
  - Resources, including layouts, strings, and drawables.

- **`app/build.gradle` file:**
  - Configuration file for app-specific build settings.

- **`google-services.json` file:**
  - Configuration file for Firebase services.

## How to Run the App

1. **Clone the Repository:**
2. **Open in Android Studio:**
- Open Android Studio and select "Open an existing Android Studio project."
- Choose the cloned project directory.

3. **Connect to Firebase:**
- Create a Firebase project on the [Firebase Console](https://console.firebase.google.com/).
- Add an Android app to your Firebase project and download the `google-services.json` file.
- Place the `google-services.json` file in the `app/` directory of your Android project.

4. **Run the App:**
- Connect an Android device or use an emulator.
- Click the "Run" button in Android Studio to install and launch the app on your device.
