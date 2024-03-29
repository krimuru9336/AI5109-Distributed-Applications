# ChitChat Application Client
You can find the client files in the `ChitChat` folder of the repository.
The client is written using Android Studio and Java. The client uses the [Socket.IO](https://socket.io/) library for websockets (socket.io-client:2.1.0) as well as [Glide](https://bumptech.github.io/glide/) for displaying images and gifs (com.github.bumptech.glide:glide:4.14.2) and [ExoPlayer](https://developer.android.com/media/media3/exoplayer) for showing videos (androidx.media3:media3-exoplayer:1.3.0). When opening the project in Android Studio, you may need to install the Android SDK. The tested Android version is 14 ("UpsideDownCake") and API Level 34.

The Backend is written in Java, while the Frontend is using XML.

## Building the Client
To build the client, you will need to install the Android SDK. You can do this by opening the project in Android Studio and following the prompts. You will also need to install the Android SDK Build Tools. You can do this by opening the SDK Manager in Android Studio and installing the SDK Build Tools for API Level 34.

Before building the client, you will need to change the IP address in `app/res/values/strings.xml` to the IP address of your server.

## Running the Client
To run the client, you will need to connect an Android device to your computer. You can do this with a USB cable or over WiFi. If you are using WiFi, you will need to enable USB debugging in the developer settings. You can then run the app by clicking the green play button in Android Studio. You may need to select your device from the list of devices.

You can also build an APK file to install on your device. You can do this by clicking Build > Build Bundle(s) / APK(s) > Build APK(s) in Android Studio. You can then install the APK file on your device. After the build is complete, you can find the APK file in `app/build/outputs/apk/debug/app-debug.apk`. You can install the APK file by opening it on your device.

If you only want to test the app on a virtual device, you can create a virtual device in Android Studio. You can then run the app by clicking the green play button in Android Studio. You may need to select your device from the list of devices.

## Entering a username
When you first open the app, you will be prompted to enter a username. This username will be used to identify you to other users. If the name is already taken, you will be prompted to enter a different name. After the first login, the app will remember your username and automatically log you in. If you want to change your username, you need to clear the app data. But beware: This will also delete all your chats and the username remains taken!

## Finding other users
When a user logs in, they will be added to the list of users. The view is automatically updated when a user logs in or out. You can click on a user to start a chat with them.

## Creating a group
You can create a group by clicking on the button next to "Groups". This will allow you to enter a group name. If the group name is taken, you have to choose a different one. After creating a group, you can add users to the group by clicking on the group and then clicking on the button on the top right. You can then select users from the list of users to add or remove them to the group.

## Sending messages
To send a message, click on a user in the list of users. You can then type a message and click the send button to send it. The message will be sent to the other user and will be displayed in the chat view. The chat view is automatically updated when a message is received.

## Sending media
To send media, click on the media button in the chat view. You can then select an image, gif, or video to send. The media will be sent to the other user and will be displayed in the chat view. The chat view is automatically updated when media is received. While the media is in transit, a placeholder text will be shown.

## Editing and deleting messages
You can edit or delete your own messages by long-pressing on the message. You can then select "Edit" or "Delete" from the menu that appears. If you edit a message, the other user will see that the message has been edited. If you delete a message, the other user will see that the message has been deleted.