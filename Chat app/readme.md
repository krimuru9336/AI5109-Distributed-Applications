# Documentation report of Chat App project

## Documentation for ordinary user

This is a free and open source chatting app for real time communication.
Send a message to a specific group and everyone else in that group will see it instantly!

### Requirements:

- Internet connection
- Android version 14 or higher

### How to use

- Install the program.
- In the top bar menu, choose your username.
- Choose the group you wanna send a message to.
- type and send your message.

Your messages are displayed in Purple and messages from other participants are displayed in light pink.

> You may edit or delete your messages, but you can't to the same for other people's messages.!

**How to edit?**

- To edit a message, click on the edit button below your message.
- Type the new message you want to replace the older message.
- Press the edit button again.

**How to delete?**

- Simply click on the delete button. it deletes instantly.

## Technical documentation for developers.

### Description

This chatting app uses `Kotlin` and `Jetpack Compose` library.  
It benefits `firebase real time database` to handle the messaging functionality.  
Requires _android version 14_ or higher for testing/running the app on simulators/real devices.

### Project structure

- `MainActivity.kt` : This is the main activity file that serves as the entry point for the Chat App. It renders all the components in the screen.
- `/components` : Contains the UI components of the project. each file in this directory is a separate component. they might be imported and used directly in `MainActivity` or in other UI components.
- `/data` : This directory contains the static definitions of the project. the file `types.kt` in this directory stores the commonly used
  type definitions, constants and methods.
- `/db` : This directory contains the logic for connection to `firebase real time database`. the file `firebase.db.kt` stores the required methods
  for handling different queries to `fire base real time database`, such as sending a new message or deleting and editing an existing one.

### What is Jetpack Compose?

Jetpack Compose is a modern UI toolkit for building native Android apps. It is a declarative way of building user interfaces, where you describe what your UI should look like and Compose takes care of updating the UI automatically when the state changes.

### Why should we use Jetpack Compose?

There are several benefits of using Jetpack Compose:

1. **Declarative UI**: With Compose, you can define your UI using a simple and intuitive declarative syntax, making it easier to understand and maintain your code.

2. **Efficient UI updates**: Compose automatically updates only the parts of the UI that have changed, resulting in better performance and smoother animations.

3. **Faster development**: Compose simplifies the UI development process by providing a set of reusable components and powerful tools, allowing you to build UIs faster and with less code.

4. **Interactive and dynamic UI**: Compose makes it easy to create interactive and dynamic UIs by using state and animations, enabling you to build engaging user experiences.

5. **Compatibility and future-proofing**: Jetpack Compose is designed to work alongside existing Android views and is fully compatible with the existing Android ecosystem. It is also actively developed and supported by Google, ensuring its future relevance and updates.

### How is chatting functionality implemented in this project?

- UI Components render text input, drop down menus for group and user selection and the submit button.
- User sends a message, it is instantly stored in `firebase real time database`.
- `firebase real time db`'s `ref` is given a callback to listen for the event of `onDataChange`, which invokes whenever a change has occurred in the database. this callback updates the messages state in the Chat component with the latest state of messages in the firebase database and causes a re-render.
- As a result of re-rendering, messages are updated on screen instantly.

### State management

To ensure global accessibility to the common state of different components, they are stored in `MainActivity` and piped down to the children through parameters of the Components. as a result, when a component updates the state, it's instantly updated for all and therefor, re-render occurs real-time.
