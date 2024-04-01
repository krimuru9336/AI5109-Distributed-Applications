## Intermediate Documentation-

### Chat Application named Quick Talk:

The application serves as a communication medium for two users via chatting. It is built using java in Android Studio.

#### Pre-requisites:

-   Android Studio IDE

-   Firebase for realtime database

#### Features:

-   **Secure sign-up and login:** Users can create account with email, password and login securely.

-   **Real-time messaging:** Users can communicate with other users on the application instantly.

-   **Cloud storage:** Users list, chats are securely stored on Firebase cloud storage to ensure faster, reliable and secure access.

-   **Search User:** Users can search for a user to chat with using the search user feature. It shows users a list of possible users based on user-entered characters.

-   **Chatting:** Users can chat with other users. Text messages are displayed along with timestamp. Received and sent messages are differently color-coded for easier readability.

-   **Edit/Delete texts:** Users can choose to edit or delete their already sent messages.

-   Send images, GIFs, Videos

-   Groupchats: Create groups, Add members intuitively with check boxes and name group

-   Recent chats view: Homepage shows the contacts user has interacted with and also groups the current user is part of.

#### Screenshots

##### Start \| Login \| Signup

![SignUp screen: Creates a user and stores in firebase database.](images/WhatsApp%20Image%202024-04-01%20at%203.37.25%20AM%20(1).jpeg){width="140"}

![Login screen: Using firebase authentication mechanism for login.](images/WhatsApp%20Image%202024-04-01%20at%203.37.25%20AM.jpeg){width="135" height="271"}

##### New Chat \| Search User \| Start Chat

**Home Screen:**

![](images/Home%20screen.jpeg){width="171"}

Shows users, groups current user is part of. Click + icon to start new chat

**Search Users:**

-   Enter Username in searchbar. It fetches probable users from database, matching with user entered characters.

-   Choosing a user, redirects to chatscreen.

**Create Group:**

-   Choose members\|Group Name\|click create

-   Choose group members: Use search bar to search for users. Choose with checkboxes.

-   Cancel anytime or go to previous steps using buttons on left-top.

-   Click Next after confirming group members.

-   Number of selected members visible.

-   Give groupname and click on create.

**Edit, delete texts:**

**Media Sharing:**

-   Send images

-   Send videos

-   Send GIFs

#### Getting Started

1.  Create a Firebase project by following Option1 in below documentation <https://firebase.google.com/docs/android/setup>
2.  Firebase Dependencies-

-   **Firebase Authentication**: Service to enable sign-up, login with email and password. Also helps in detecting current signed-in user.

-   **Cloud Firestore**: Flexible, Scalable NoSQL Cloud Database.

3.  Logics to handle new account creation, login existing user, search user to start chatting, chatting , editing and deleting sent messages.
4.  Connect with emulator to run the application.