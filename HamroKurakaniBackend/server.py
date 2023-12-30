from flask import Flask, request,jsonify
from flask_socketio import SocketIO,emit
from flask_cors import CORS
from dotenv import load_dotenv
import mysql.connector
from mysql.connector import errorcode
import os
import uuid

load_dotenv()

app = Flask(__name__)

app.config['SECRET_KEY'] = 'secret!'
CORS(app,resources={r"/*":{"origins":"*"}})
socketio = SocketIO(app,cors_allowed_origins="*")

dbHost = os.getenv("DB_HOST")
dbUser = os.getenv("DB_USER")
dbPassword = os.getenv("DB_PASSWORD")
dbName = os.getenv("DB_NAME")

dbConfig = {
  'user': dbUser,
  'password': dbPassword,
  'host': dbHost,
  'database': dbName,
  'raise_on_warnings': True
}

try:
    dbCnx = mysql.connector.connect(**dbConfig)
    print("Database connection successfull")
except mysql.connector.Error as err:
    if err.errno == errorcode.ER_ACCESS_DENIED_ERROR:
        print("Something is wrong with your user name or password")
    elif err.errno == errorcode.ER_BAD_DB_ERROR:
        print("Database does not exist")
    else:
        print(err)

dbCur = dbCnx.cursor()

# DB QUERIES
insert_new_user = (
"INSERT INTO users (id, username)"
"VALUES (%s, %s)")
select_user = "SELECT * from users WHERE id=%s"

# @app.route("/http-call")
# def http_call():
#     """return JSON with string data as the value"""
#     data = {'data':'This text was fetched using an HTTP call to server on render'}
#     return jsonify(data)

@socketio.on("connect")
def connected():
    """event listener when client connects to the server"""
    print(request.sid)
    print("client has connected")
    emit("connect",{"data":f"id: {request.sid} is connected"})

@socketio.on('data')
def handle_message(data):
    """event listener when client types a message"""
    print("data from the front end: ",str(data))
    emit("data",{'data':data, 'id':request.sid}, broadcast=True)

@socketio.on("disconnect")
def disconnected():
    """event listener when client disconnects to the server"""
    print("user disconnected")
    emit("disconnect",f"user {request.sid} disconnected",broadcast=True)

@app.route("/login", methods=['GET', 'POST'])
def login():
    if request.method == 'POST':
        data = request.json
        username = data.get("username")
        if(username == None):
            return "Username is required!", 400
        userUuid = uuid.uuid4()
        userId = str(userUuid)
        try:
            dbCur.execute(insert_new_user, (userId, username))
            dbCur.execute(select_user, (userId,))
            users = dbCur.fetchall()
            return users[0]
        except Exception as ex:
            if ex.errno == 1062:
                return "Username already taken.", 400
            else:
                return f"Unexpected {ex=}, {type(ex)=}", 400

if __name__ == "__main__":
    socketio.run(app, debug=True)
