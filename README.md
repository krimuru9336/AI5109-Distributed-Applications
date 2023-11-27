# Flask Web Application

## Description
-Author: Usama Sajjad
-Matriculation Number: 1409048
-Created Date: 27/11/2023
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
