## Intro

This project consist of two separate applications

- **Frontend**
- **Backend**

Each separate project directory contains individual README file.

## Stack

### Frontend

The frontend application uses Angular 16. For end-to-end testing Cypress is used.

### Backend

Backend application is created with ExpressJS library of NodeJS.

### Database

MongoDB has been used in order to stor the data. The backend uses the library called **mongoose** in order to interact with MongoDB.

## Steps of the students-info submission

- First of all need to run both applications, `frontend` and `backend`
- Frontend application runs on http://localhost:4200/
- While redirecting to the above link, an angular project will load from the frontend/src/index.html path
- For frontend index.html is the main entry point which will load other components, in this case it loads app component
- User inputs informations in the fields and submit the form
- There is a service class which holds the methods using backend url to create, get and delete students info
- In the backend directory index.js is the main entry point which imports and defines routes
- Route file defines the path, methods(post, get, delete) and use the function from controller
- Controller has the functions which helps to create, get and delete students info
- Based on the user actions, data got created or loaded or deleted from the DB and changes got visible on the frontend

- **Frontend**: http://localhost:4200
- **Backend**: http://localhost:3000
