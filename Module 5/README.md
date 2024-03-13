# ChitChat Documentation

ChitChat is a chat app that is written in Kotlin, uses Android XML layout for the UI and Firebase as
backend.

## Features

- Anonymous login, no personal data required
- Chat overview
- User search to start a new chat
- Display of messages in a chat with real time updates
- Edit and delete messages
- Group chats
- Support of images, GIFs and videos

## Development

### Prerequisites

Firebase Authentication and Firestore Database must be available

### Installation

After cloning the repository, only the google-services.json needs to be added. This can be obtained
in the Firebase Console and should never be committed in the repository.

### Frontend

The app uses XML layouts. RecycleViews are used to display the data from Firebase in real time.

### Backend

The app uses firebase authentication to authenticate a user (anonymously). Firestore is used to
store the data.

### Activites

- MainActivity: The app's entry point
- LoginActivity: Handles anonymous login by entering a username
- ChatActivity: The activity for chat conversations
- SearchUserActivity: Search for other users to start a chat

## Credits

The code for the development of the app was partially derived
from [this](https://www.youtube.com/watch?v=jHH-ZreOs1k) tutorial.