# Android ChatApp 'WhatsDown' - Intermedia documentation report

This small report contains all technical information about the app.

## What this app can do

This app is a simple chat client for Android smartphones. You can chat with anyone using this app.
It provides an overview of the chat list where you can select the user you want to chat with. You
can open the chat, which shows all the latest messages. You can send messages to the person you are
chatting with and they will receive them in real time. Messages can also be edited and deleted at a
later date. Media such as pictures and videos can also be sent through the app. You can also choose
from a list of users and create group chats with an unlimited number of people.

Real-time means that the messages are received by your buddy within seconds (depending on your
Internet connection). This is a two-way connection.

## Initial development Setup

You can develop the application by using [Android studio](https://developer.android.com/studio).

```bash
git clone *REPO-URL*
```

Open the folder in Android Studio. Now open
the  [Google Firebase Console](https://firebase.google.com/) and create a new Android project.
Download the `google-services.json` and put it in the root directory of the app. This files creates
the connection from your app to your Firebase project. (**Don't push the file in your repository!**)

If everything is setup correctly, you can start the app by running in an emulator or a connected
Android Smartphone. Make sure to enable
the [Developer mode](https://developer.android.com/studio/debug/dev-options) on your Android device.

## Technical information

### Backend

This application uses [Google Firebase](https://firebase.google.com/) to enable real-time messaging.
It uses [Firestore](https://firebase.google.com/docs/firestore) to store the
data, [Firebase Authentication](https://firebase.google.com/docs/auth) to register the user
anonymously, and [Cloud Storage for Firebase](https://firebase.google.com/docs/storage) to store
media in the cloud.

The following list contains a short description of some of the most important components of the app.

- Activities:
    - **OverviewActivity**: Manages the overview of all chats and users.
    - **ChatRoomActivity**: Manages the chat room by displaying messages and allowing user
      interaction.
    - **LoginUserActivity**: Handles the user login by username.
- Adapters:
    - **ChatListRecyclerAdapter**: Handles the RecyclerView for the chat list.
    - **ChatRoomRecyclerAdapter**: Handles the RecyclerView for the chat room (messages).
    - **UserListRecyclerAdapter**: Handles the RecyclerView for the user list.
    - **ViewPageAdapter**: Handles the views of the overview activity.
- Fragments:
    - **ChatsFragment**: Fragment of the overview activity that handles the chat list.
    - **UsersFragment**: Fragment of the overview activity that handles the user list.
- Listeners:
    - chatroom:
        - **AddMediaButtonClickListener**: Handles the click on the add media button.
        - **ChatMessageLongClickListener**: Handles the long click on a message.
        - **EditMessageCancelListener**: Handles the cancel action of the edit message dialog.
        - **EditMessageUpdateListener**: Handles the update action of the edit message dialog.
        - **SendButtonClickListener**: Handles the click on the send button.
    - user_list:
        - **AddGroupButtonClickListener**: Handles the click on the add group button.
        - **ChatGroupTitleCancelListener**: Handles the cancel action of the chat group title
          dialog.
        - **ChatGroupTitleCreateListener**: Handles the create action of the chat group title
          dialog.
        - **CheckboxChangeListener**: Handles the change of the user checkbox.
    - **LoginButtonClickListener**: Handles the click on the login button to login a user into the
      app.
    - **NewChatClickListener**: Handles the click on a user in the user list to create a new chat.

### Frontend

The front end of the application is built using XML layouts.
To update the user's view in real time as messages are sent and received, the app
uses [RecyclerViews](https://developer.android.com/reference/androidx/recyclerview/widget/RecyclerView).
A RecyclerView listens to the Firestore and dynamically updates the view when data changes in the
cloud.

## Features

This list contains a list of all features (also planned features) of this app.

- [x] Anonymous login screen with firebase
- [x] Overview page of all chats
- [x] Chat view where you can see recent messages
- [x] Sending and Receiving messages (in real-time)
- [x] Editing messages
- [x] Deleting messages
- [x] Sending and receiving images
- [x] Sending and receiving GIFs
- [x] Sending and receiving videos
- [x] Deleting media (images, GIFs, videos)
- [x] User and chat list overview in separated views
- [x] Group chat functionality (including all previous functionalities)

## Credits

This app was created with the help
of [this](https://www.youtube.com/watch?v=jHH-ZreOs1k&ab_channel=EasyTuto) tutorial.
