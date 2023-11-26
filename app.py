"""
Author: Usama Sajjad
Matriculation Number: 1409048
Created Date: 05/11/2023

This Flask application serves as a simple web interface to collect user data (name and phone number), store it in an SQLite database,
and display the data on another page. It includes routing for the home page, data submission, and data display.
"""
import response
# Import necessary modules
from flask import Flask, request, render_template, flash, redirect, url_for, Response
import sqlite3
import threading

app = Flask(__name__)
app.secret_key = 'your_secret_key'  # Replace with a secret key of your choice

# Use thread-local storage for the database connection
db = threading.local()

def get_db():
    if not hasattr(db, 'connection'):
        db.connection = sqlite3.connect('mydb.db')
    return db.connection

def create_table():
    """
    Create the 'user_data' table in the database if it doesn't already exist.
    The table has columns for 'id', 'name', and 'phone_number'.
    """
    conn = get_db()
    cursor = conn.cursor()
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS user_data (
            id INTEGER PRIMARY KEY,
            name TEXT,
            phone_number TEXT
        )
    ''')
    conn.commit()

# Create the table when the application starts
create_table()

@app.route('/')
def home():
    """
    Renders the home page, which includes a form to submit user data.
    """
    return render_template('index.html')

@app.route('/submit', methods=['POST'])
def submit():
    """
    Handles the data submission form. Inserts user data into the 'user_data' table and redirects to the display page.
    """
    name = request.form['name']
    phone_number = request.form['phone_number']
    cursor = get_db().cursor()
    cursor.execute("INSERT INTO user_data (name, phone_number) VALUES (?, ?)", (name, phone_number))
    get_db().commit()
    cursor.close()

    flash("Data submitted successfully!", 'success')  # Flash success message
    return redirect(url_for('display'))  # Redirect to the display page

@app.route('/display')
def display():
    """
    Displays user data from the 'user_data' table in a table format.
    """
    cursor = get_db().cursor()
    cursor.execute("SELECT * FROM user_data")
    data = cursor.fetchall()
    cursor.close()
    return render_template('display.html', data=data)

# ... (integrating API)
"""
Author: Usama Sajjad
Matriculation Number: 1409048
Created Date: 09/11/2023
"""
import requests
from flask import jsonify, render_template
@app.route('/coinlayer_api', methods=['GET'])
def coinlayer_api():
    """
    Makes a call to the CoinLayer API and displays cryptocurrency exchange rates.
    """
    try:
        access_key = '69fb2d1101879af2d8ebdb9f6c01a5bf'  # Your CoinLayer API access key
        base_currency = 'USD'  # You can change the base currency if needed
        # Make a request to the CoinLayer API
        response = requests.get(f'http://api.coinlayer.com/live?access_key={access_key}&base={base_currency}')
        response.raise_for_status()
        data = response.json()
        # Extract exchange rates
        exchange_rates = data['rates']
        return render_template('coinlayer_api.html', exchange_rates=exchange_rates)
    except requests.exceptions.RequestException as e:
        return jsonify({'error': str(e)})
# ... (rest of the code)


if __name__ == '__main__':
    app.run()
