# ChappAt - Chat Application

ChappAt is a cross-platform chat application developed using React Native and FastAPI. It utilizes Zustand for state management.

## Features

- Real-time chat functionality
- User authentication
- WebSocket integration for instant messaging
- Zustand for efficient state management

## Getting Started

To run the FastAPI server, use the following command:

```bash
python -m uvicorn main:app --reload
```

To run the React Native app, navigate to the `app` directory and use the commands below:

```bash
# For Android
npm run android

# For iOS
npm run ios
```

## Tech Stack

### Frontend

- React Native
- React Navigation
- Zustand for state management
- React Native Gifted Chat for chat UI

### Backend

- FastAPI
- SQLAlchemy for database interaction
- WebSocket for real-time communication

## Installation

```bash
# Install backend dependencies
pip install -r requirements.txt

# Install frontend dependencies
cd app
npm install
```

## Scripts

- `npm start`: Start the React Native development server
- `npm test`: Run Jest tests
- `npm run lint`: Run ESLint for code linting
- `python -m uvicorn main:app --reload`: Start the FastAPI server

## Dependencies

- `@fortawesome/fontawesome-svg-core`, `@fortawesome/free-solid-svg-icons`, `@fortawesome/react-native-fontawesome`: FontAwesome for icons
- `@react-native-community/viewpager`, `@react-navigation/bottom-tabs`, `@react-navigation/material-top-tabs`, `@react-navigation/native`, `@react-navigation/native-stack`, `@react-navigation/stack`: React Navigation for navigation
- `axios`: HTTP client for making requests
- `zustand`: State management library
- `react-native-gifted-chat`: UI component for chat interface
- Other dependencies for various functionalities

## Development Environment

- Node.js version: 18 or later
- Python version: 3.9 or later

## License

This project is licensed under the [MIT License](LICENSE).

Feel free to explore, contribute, and use ChappAt for your chat application needs!
