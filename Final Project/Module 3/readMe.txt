Module 3: Intermediate DOcumentation

Realtime chat application

Softwares used:

IDE: Android Studio
Programming Language: Java

1. Create new Empty project in Android Studio - choose java programming language.

2. Setup a firebase database connection to enable realtime communication.

3. Open Firebase and login with google account. Create a new project in it and give any name.
Once created - enter Android Studio Project name (eg. com.example.myapplication)

This gives a google-services.json file. Download it to local machine.
Move this file to project app-level.

4. In build.gradle of app-level, add google services to plugin
plugins {
    id("com.google.gms.google-services")
}

and import firebase dependency - 
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))

    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-database")

In build.gradle of project level - add the following to plugins
plugins {

    id("com.google.gms.google-services") version "4.4.0" apply false
}

Firebase is now set and can be used by importing it.

5. For chat application - 3 classes -
MessageModel - has parameters of a message.
Used "message" and "timestamp" in this case

MainActivity - Executed first. Has logic implementing database connectivity, message display on UI.


6. XML files -
These are designed on UI by drage and drop to define chat area, send button.

ChatAdapter - bindings with UI elements
