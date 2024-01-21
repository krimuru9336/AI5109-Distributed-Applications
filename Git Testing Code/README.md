<<<<<<< HEAD
# Flask Web Application

## Description
-**Author: Usama Sajjad**
-**Matriculation Number: 1409048**
-**Created Date: 27/11/2023**
This Flask web application demonstrates a simple project that includes a form to input data (name and phone number), a backend that interacts with a SQLite database, and API calls to external services (Shibe API, CoinLayer API). The application also utilizes HTML templates to display data and images.

## Features
- **User Input Form:**
  - Accepts user input for name and phone number.
  - Submits data to the backend, which stores it in an SQLite database.

- **Display Data:**
  - Retrieves and displays data from the SQLite database on the frontend.

- **External API Integration:**
  - Fetches random dog images from the Shibe API.
  - Retrieves cryptocurrency exchange rates from the CoinLayer API.

## Project Structure
- **app.py:** Flask application file containing routes and backend logic.
- **templates/:** HTML templates for rendering pages.
  - **index.html:** Main page with navigation links.
  - **form.html:** Form for user input.
  - **display.html:** Displays data from the database.
  - **dog_api.html:** Displays random dog images from the Shibe API.
  - **coinlayer_api.html:** Displays cryptocurrency exchange rates from the CoinLayer API.
- **static/:** Folder for static files (e.g., CSS styles).

## Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/fdai7286/myDAApp.git
=======
# AI5109-Distributed-Applications

This README outlines the guidelines for working with this repository. It is essential to follow these guidelines to maintain a structured and collaborative development process.

## Basic Principles

1. **No Direct Work on Main Branch:**
   - No one should directly work on the `main` branch.

2. **Individual Project Branches:**
   - Each contributor manages their work on a branch named after their FD number (e.g., `fdai1234`).

3. **Prohibition of Editing Others' Branches:**
   - Editing or making changes directly to branches other than your own is strictly prohibited.

## Working on Your Own Branch

Follow these steps to create and work on your own branch:

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/krimuru9336/AI5109-Distributed-Applications.git
   cd AI5109-Distributed-Applications
   ```

2. **Create a New Branch Named After Your FD Number:**
   ```bash
   git checkout -b fdai1234
   ```
   - Replace `fdai1234` with your actual FD number.

3. **Make Changes:**
   - Implement your features or make necessary changes.

4. **Commit Changes:**
   ```bash
   git add .
   git commit -m "Your meaningful commit message here"
   ```

5. **Push Changes to Your Branch:**
   ```bash
   git push origin fdai1234
   ```
   - Replace `fdai1234` with your actual FD number.
>>>>>>> 6ef55c60fca023841729166453b9722a396e58a8
