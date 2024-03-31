# RippleChat Application Readme

## Project Overview

Ripplechat is a feature-rich chat application designed for seamless user registration, login, and real-time text-based communication. The project follows standard Android application development practices and incorporates Firebase for backend services.

## Features

### User Registration

New users can easily register by providing essential details such as username, email, and password. The information is securely stored in Firebase for future logins.

### User Login

Users can log in using their registered credentials. Firebase authentication is utilized to verify the entered information, granting access to the chat interface upon successful login.

### Chat Messaging

Once logged in, users can engage in real-time text-based conversations. Firebase Realtime Database is employed to store and retrieve chat messages. Note that this version currently supports text messages only.

### Editing and Deleting Messages

Users have the ability to edit their previously sent messages within a certain time frame to correct mistakes or update information. Additionally, users can delete messages they have sent to remove them from the conversation thread.

### Multimedia Support

Enriching the messaging experience, Ripplechat supports multimedia elements such as videos and GIFs in messages, allowing users to share various media types.

### Group Chat

Extended the application to support group chat functionality, enabling multiple users to participate in a single conversation thread. The group chat feature also includes multimedia support, allowing users to share various media types within group conversations. Moreover, the application facilitates capturing pictures from the camera directly within the chat interface for enhanced multimedia sharing capabilities.

## App Look

![Screenshot 1](https://drive.google.com/file/d/1Cxq7GCif303gupB2TTI_66WA-nq8IllH/view?usp=sharing)

![Screenshot 2](https://drive.google.com/file/d/1sQEpnLsgCUsJVFjbZYiImPpMXM7pSLBC/view?usp=sharing)

![Screenshot 3](https://drive.google.com/file/d/1azDldKo5lPI4Qtv0BG3e-V0Ybwp4_dEs/view?usp=sharing)

![Screenshot 4](https://drive.google.com/file/d/1nF9rt2AYxdZkZ7r7jihPRU21j5vTgrRi/view?usp=sharing)

![Screenshot 5](https://drive.google.com/file/d/1OJufamP-gc4BjmpVAZ9qsykXIL_UXvz6/view?usp=sharing)

![Screenshot 6](https://drive.google.com/file/d/1DSDk-bj1sMd0CUiKu0XC1z690kswbUMO/view?usp=sharing)

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

## Notes

- **Firebase Dependencies:**

  - The project relies on Firebase dependencies. Ensure that you have the required dependencies in the `build.gradle` files.

- **Custom Package Name:**
  - Update the package name (`com.example.yourpackage`) in the file paths based on your specific package structure.

Happy Chatting! 🚀
