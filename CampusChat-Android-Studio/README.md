# Distributed Applications - Final Project: "CampusChat: Android Chat Application" By Mohammed Amine Malloul

## Overview 
CampusChat is an Android-based chat application designed for easy and efficient communication within campus communities. Utilizing Firebase for database management and user authentication, the application supports text and multimedia messaging in a user-friendly environment.

### Module 4: Editing and Deleting Message Video: [Download .zip file](Module_4_Video-Mohammed_Amine_Malloul.zip)

## Features
- User Authentication: Utilizes Firebase Authentication for secure user sign-up and login.
- Real-time Messaging: Powered by Firebase Realtime Database, facilitating dynamic text conversations.
- Editing and Deleting Messages: Users can edit or delete their messages after sending, allowing for corrections or removing sensitive information.

## Development Details
### Environment
- IDE: Android Studio
- Languages: Java, XML 
- Database: Firebase Realtime Database 
- Authentication: Firebase Authentication

### Project Structure
- BaseActivity: Manages the main layout of the application and includes a bottom navigation bar for navigating between different fragments.
- Fragments Handled:
  - HomeFragment: Displays chat summaries and manages chat sessions.
  - UserListFragment: Shows a list of users for starting new conversations.
  - AboutFragment: Provides information about the app and its developer.
- ConversationActivity: Facilitates sending, editing, and receiving messages in real-time.
- LoginActivity and SignUpActivity: Handle user authentication.

### UI Components
- Bottom Navigation Bar: For navigating between different sections of the app. 
- RecyclerView: Used for displaying lists of messages and chat summaries.
- Custom Adapters: MessageAdapter for messages, ChatSummaryAdapter for chat summaries and UserAdapter for users..

### Custom Layouts
- Message Items: Distinct layouts for sent and received messages, including functionality for editing and deleting.
- Chat Summary: Displays the most recent message along with the sender's information, updated in real-time for edits and deletions.

### Key Functionalities
- Loading and Sending Messages: Messages are loaded in real-time, and users can send text messages.
- Editing Messages: Provides an interface for users to edit their sent messages. Edited messages are updated in real-time.
- Deleting Messages: Allows users to delete their messages. Deleted messages are removed from the chat and replaced with a placeholder text indicating a message was deleted.
- Chat Summaries: The home screen displays a summary of all chats, dynamically updated to reflect the most recent messages, including edits and deletions.
- Message Alignment: Sent messages are right-aligned, while received messages are left-aligned, with distinct bubble styles for easy differentiation.

### Challenges and Solutions
- Message Alignment: Addressed issues with message item alignment in the RecyclerView.
- Dynamic Message Loading: Ensured messages between specific users are loaded correctly in conversations, including handling edits and deletions seamlessly.
- Chat Summary Updates: Implemented logic to update chat summaries in real-time when messages are edited or deleted, maintaining an accurate and up-to-date conversation history.

This README provides an overview of the CampusChat application's functionalities, development environment, and key features. It serves as an intermediate documentation to assist in understanding the application's structure and core components. Further details can be added as the development progresses and new features are implemented.
