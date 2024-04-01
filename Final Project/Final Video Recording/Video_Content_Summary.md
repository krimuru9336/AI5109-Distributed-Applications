## Overview:

The video explores working of chat application called QuickTalk. Tech stack and features are in Main Document (Module4).md in Final Project folder. Following document explains what is covered in the Final video.

## Final Video explanation:

1.  **Signup**: It shows how user can signup on the application using username, email id and a password. A toast is displayed to inform user about the status of signup whether it was successful or not. (Field Validation is also present.)

2.  **Login**:

    Then a user can login using email ID and password which they just set up while signup. A toast is present to display the status of login also. 

3.  **Home screen**: After logging in user, can see the home screen, which has a list of recent chats of current user And also the groups which the current user is a member of.

    Clicking on plus icon opens up the search window .

4.  **Search user:** Dynamic searching and filtering implemented to show the list of usernames, which the current user is searching.

    Upon choosing a user from this list, chat page is opened with the other username.

5.  **Chat activity:** Sent messages display timestamp .

    -   **Edit/Delete:** Long press on a particular message for edit and delete options.

    -   **GIFs:** Clicking On GIF option, user can type a query which triggers a **real time**-connection via **giphy API** and brings up 5 gifs matching the user query. Choose any gift to be sent .

    -   **Video Sharing:** Send a video by clicking on the attach icon .

    Video can be pressed upon to pause and play on choice .

    *(Play pause Video was missed in the main video, so have attached a separate video for this feature. )*

    **6.Group creation :**

    -   **Add members:** Select or unselect members to add in the group with a checkmark. Once selected, click on next ,

    -   **Group naming:** In this section, group name can be entered. It also shows a list of members count selected for the current group if desired user can navigate to the previous section also. 

    Clicking on create button, creates the group and a toast is shown for successful message taking user to home screen.

    **7. Image sharing and group chats:** Photo sharing and group chats feature is shown in the last part of the video.

    The group created had four members and we see how message sent by any member is transmitted to only the four members of the group.