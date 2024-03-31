# Ripplechat Application Readme

## Project Overview

Ripplechat is a feature-rich chat application designed for seamless user registration, login, and real-time text-based communication. The project follows standard Android application development practices and incorporates Firebase for backend services.

## Features

### 1. User Registration

- **XML Layout File:** `res/layout/registeractivity.xml`
- **Java File:** `src/main/java/com/example/yourpackage/RegisterActivity.java`

  New users can easily register by providing essential details such as username, email, and password. The information is securely stored in Firebase for future logins.

### 2. User Login

- **XML Layout File:** `res/layout/loginactivity.xml`
- **Java File:** `src/main/java/com/example/yourpackage/LoginActivity.java`

  Users can log in using their registered credentials. Firebase authentication is utilized to verify the entered information, granting access to the chat interface upon successful login.

### 3. Chat Messaging

- **XML Layout File:** `res/layout/messageactivity.xml`
- **Java File:** `src/main/java/com/example/yourpackage/MessageActivity.java`

  Once logged in, users can engage in real-time text-based conversations. Firebase Realtime Database is employed to store and retrieve chat messages. Note that this version currently supports text messages only.

## Project Structure

- **Values Folder:** `res/values/`

  - Contains color (`res/values/colors.xml`) and string (`res/values/strings.xml`) resource files to centralize color schemes and string values, enhancing code maintainability.

- **Firebase Configuration:**
  - Firebase connection configuration files are located in the `app` module, including `google-services.json`. Ensure your Firebase project configuration aligns with these files.

## How to Use

1. **Clone the Repository:**

   - Clone this repository to your local machine using the following command:
     ```
     git clone <repository-url>
     ```

2. **Open Project in Android Studio:**

   - Open the project in Android Studio and ensure that you have the necessary dependencies installed.

3. **Firebase Setup:**

   - Set up a Firebase project (if not already done) and configure your application by replacing the `google-services.json` file with your project-specific configuration.

4. **Run the Application:**
   - Run the application on an emulator or physical Android device to test the registration, login, and messaging functionalities.

## Features added In Module 5

- **Multimedia Support:**

  - Enhanced the messaging functionality to support multimedia elements, including videos and GIFs.

- **Group Chat:**
  - Extended the application to support group chat functionality.
  - Added a multimedia support in the Group chat.
  - Added funtionlatiy to take pictures from camera.

## Notes

- **Firebase Dependencies:**

  - The project relies on Firebase dependencies. Ensure that you have the required dependencies in the `build.gradle` files.

- **Custom Package Name:**
  - Update the package name (`com.example.yourpackage`) in the file paths based on your specific package structure.

Happy Chatting! 🚀
