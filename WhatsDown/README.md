# WhatsDown
Project Distributed Application Hochschule Fulde Winter Terms 23/24

This app enables chat functionality over Google Firebase Services without the need for registration.
Users can create an account securely over the OTP-Firebase-Service.

## OTP (One Time Password)
WhatsDown implements One-Time Password (OTP) sign-in using Firebase Authentication. 
It simplifies the process of securely verifying users' phone numbers through OTP without the need to
create a standard account.

## Firebase storage
Firebase Storage allows to securely store and serve multimedia content such as images, videos, and audio files.
It also stores all users, chatrooms and messages. 

## Models
- `UserModel.java`: Represents a single user.
- `ChatMessageModel.java`: Represents a single text message with all its attributes (timestamp, senderID, actual message...)
- `ChatroomModel.java`: Represents a chatroom between two or more users.

## Activities

An activity represents a single, focused task with its own user interface. Manages UI and user interactions.
This application consists of the following activities:

- `SplashActivity.java`: Displays a loading screen while the app is initializing.
- `LoginOtpActivity.java`: Handles user authentication using OTP.
- `LoginPhoneNumberActivity.java`: Manages phone number-based user login over OTP.
- `MainActivity.java`: Main page of the app.
- `ChatActivity.java`: Activity for individual chat conversations between two people. (Group Chats not yet implemented)
- `SearchUserActivity.java`: Allows users to search for other users to initiate chats.

## Fragments

A fragment is a modular portion of an interface or behavior within an activity. 
It can be combined within activities for a flexible UI, having its own lifecycle.

- `ChatFragment.java`: Manages chat UI and logic within the chat activity.
- `ProfileFragment.java`: Handles user profile display and editing.
- `SearchUserFragment.java`: Displays user search results and options for starting a chat.

## Adapter 

- `ChatAdapter.java`:  Adapter that listens to a FirestoreArray and displays its data in real time, in this case for a specific chat.
- `RecentChatAdapter.java`:  Same as 'ChatAdapter.java', but for all chats a specific user has made, responsible for populating the main page with previews of the recent chats.
- `SearchUserAdapter.java`:  Handles everything regarding the search functionality. 



