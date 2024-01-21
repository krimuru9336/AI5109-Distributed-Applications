# Android Chat Application

## Table of Content

	- [Introduction](#introduction)
	- [Requirements](#equirements)
	- [Features](#features)
	- [Technologies Used](#technologies-used)
	- [Installation](#installation)
	- [Usage](#usage)
	- [Project Structure](#project-structure)
	- [Further Development](#further-development)


## Introduction

This Android app allows users to chat in real-time using Google Firebase Authentication and Realtime Database. In this phase it supports only text messages and timestamps.


## Requirements

- Android Studio
- Android SDK 24 or higher
- Android emulator with Android 7.0+
- Java 8 or higher
- Google Firebase account
- Knowledge of Kotlin and Android development

	
## Features

1. Real-time message communication
2. User authentication with Google Firebase (Login with Google account or login with email address)
3. Secure data storage with Google Firebase Realtime Database
4. Simple and intuitive user interface
4. Logout from account and login again
	
## Technologies Used

- Android Studio: The IDE which is used to develop this Android app.
- Kotlin: The primary programming language for Android app development.
- XML:  Used for designing user interfaces in Android.
- Google Firebase (Authentication, Realtime Database)
	- Authentication: Enables user authentication with secure login methods.
	- Realtime Database: A NoSQL cloud database to store and sync app data in real-time.

## Installation

1. Downloading the project:
	- Open a terminal window and navigate to the directory where you want to clone the repository.
	- Clone the CoolChat2 repository to your local machine.
2. Importing the Project: 
	- Open Android Studio and select **File** > **New** > **Project from Version Control**.
	- Choose **Git** and enter the URL of the repository you cloned in step 2.
	- Click **Clone**.
3. Configuring Firebase:
	- In the Firebase console, create a new project or use an existing one.
    - Download the **google-services.json** file from the Firebase console and place it in the `app/` folder of the project.
3. Running the App:
	- Connect an Android device or emulator to your computer.
	- In Android Studio, click the **Run** button.
	- The app will be installed on the device or emulator and launched.


## Usage

To use the Real-Time Chat App, follow these steps:

1. **Launch the App:**
    - Connect an Android device or emulator to your computer.
    - In Android Studio, click the **Run** button.
    - The app will be installed on the device or emulator and launched.
    
2. **Sign In:**
    - Upon launching the app, you will be prompted to sign in using your Google account.
    - Tap the **Sign in with Google** button and follow the on-screen instructions to sign in.
    - You can also sign in with your email address and give your name and password.

3. **Send Messages:**
    - Once signed in, you can start chatting with other users.
    - Enter your message in the text box at the bottom of the screen.
    - Tap the **Send** button to send the message.

4. **Receive Messages:**
    - New messages from other users will appear in real-time in the chat window.
    - Tap a message to view the sender, timestamp, and content.


## Project Structure

The project is organized into the following folders:

- `app`: Contains the main Android application code.
- `build`: Contains generated build files.
- `gradle`: Contains Gradle project files that define the project's dependencies, build settings, and tasks. 
- `google-services.json`: File containing Firebase configuration information.  It is downloaded from the Firebase console during the project setup process.
- `README.md`: This document.

## Further Development

As the project evolves, consider implementing the following features for enhanced functionality:

- **Edit and Delete Messages**

Allow users to edit and delete their sent messages. This can be achieved by integrating additional options within the message interface.

- **Multimedia Messaging**

Extend the messaging capabilities to include multimedia content such as GIFs, images, and videos. You may need to integrate additional libraries or services for handling multimedia content.

- **Group Chat**

Extend the app to support group chat functionality, allowing users to create and participate in group conversations.


