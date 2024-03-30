# Readme Module 03

- https://elearning.hs-fulda.de/ai/mod/assign/view.php?id=72047
- Name: Adrianus Jonathan Engelbracht
- Matriculation number: 1151826
- Date: 21.01.2024
- Teacher: Kritika Murugan
- Module: Distributed Applications (WiSe2023/2024)



## Description

"SheetChatDa" is an application developed in Android Studio Hedgehog (2023.1.1 Patch 1). The used programming languages are Java for the logic of the application and XML for the design of the screens. 

Furthermore Kotlin DSL (build.gradle.kts) is used as the Build configuration language.

The authentication and storing of real time data is accomplished by connecting Firebase to the Android Studio application. The modules "Authentication"  enables Email/Password authentication and registration in the app, while the module "Realtime Database" enables the saving and retrieving of user credentials and the chat history.



## Manual

#### <u>**Signup**</u>

The signup  screen shows a logo and the  3 fields (username, email, password) required to sign up for an account.  

<img src="pictures/2024-01-21 10_33_47-.png" alt="Signup Screen" style="zoom:50%;" />

After entering the fields and clicking in sign up, the Chat Screen will be shown to the user. 

### **<u>Sign-in</u>** 

When entering a correct email and a password  and after clicking on sign up, the credentials are validates with the stored credentials in the firebase database and the user will be upon successful login taken to the chat overview screen. 

<img src="pictures/2024-01-21 10_43_00-.png" alt="Sign-in Screen" style="zoom:50%;" />

### <u>**Logout**</u>

When closing and reopening the app a user will be retaken to the chat overview screen, if he has logged in. 

To change the account for example, there is a menu, which has a menu entry to log the user out of the chat app. The user will then be taken to the Sign-in Screen again. 

<img src="pictures/2024-01-21 10_45_22-.png" alt="Logout Screen" style="zoom:50%;" />

### <u>Chat overview Screen</u>

The different accounts, who have registered trough the sign up process are shown in a list to the user. (with a shared generic profile picture and their username)

By clicking on a user, the chat between the signed in person and selected user will be opened. 

<img src="pictures/2024-01-21 10_51_39-.png" alt="Chat Overview Screen" style="zoom:50%;" />

### <u>**Chat Detail Screen**</u>

The chat detail screen shows a back button, a generic profile picture and the username at the top. 

At the bottom there is a field, where a message can be entered with a button who sends the message to the other user. 

In between the top and the bot is place for the the chat. Sent chat messages are on the right and received messages are on the left. 

Every chat message has a timestamp under the message in the format HH:MM. 

<img src="pictures/2024-01-21 10_58_44-.png" alt="Chat Detail Screen 1" style="zoom:50%;" />

<img src="pictures/2024-01-21 10_59_57-.png" alt="Chat Detail Screen 2" style="zoom:50%;" />

<img src="pictures/2024-01-21 11_01_28-.png" alt="Chat Detail Screen 3" style="zoom:50%;" />