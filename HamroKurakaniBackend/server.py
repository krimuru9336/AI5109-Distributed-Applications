import datetime
from flask import Flask, request,jsonify
from flask_socketio import SocketIO,emit,send, join_room
from flask_cors import CORS
from dotenv import load_dotenv
import mysql.connector
from mysql.connector import errorcode
from werkzeug.security import generate_password_hash, check_password_hash
from flask_jwt_extended import create_access_token, get_jwt_identity, jwt_required, JWTManager
import os
import uuid
from flask_cors import CORS

load_dotenv()

dbHost = os.getenv("DB_HOST")
dbUser = os.getenv("DB_USER")
dbPassword = os.getenv("DB_PASSWORD")
dbName = os.getenv("DB_NAME")

appSecret = os.getenv("APP_SECRET")

app = Flask(__name__)
CORS(app)

# Setup the Flask-JWT-Extended extension
app.config["JWT_SECRET_KEY"] = os.getenv("JWT_SECRET_KEY")
jwt = JWTManager(app)

app.config['SECRET_KEY'] = os.getenv("SOCKET_SECRET_KEY")
CORS(app,resources={r"/*":{"origins":"*"}})
socketio = SocketIO(app,cors_allowed_origins="*")

from flask_jwt_extended import create_access_token, get_jwt_identity, jwt_required

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
"INSERT INTO users (id, username, password)"
"VALUES (%s, %s, %s)")

select_user = "SELECT * from users WHERE username=%s"

select_every_users_expect_own = "SELECT id, username FROM users WHERE username!=%s"

userIdToSid = {}

@socketio.on("connect")
def connected():
    userId = request.args.get("uid")
    print("client has connected with socket id:", request.sid)
    userIdToSid[userId] = request.sid

@socketio.on('data')
def handle_message(data):
    userSid = request.sid
    recipientUid = data.get('recipientUid')
    msg = data.get("message")
    print(recipientUid)
    timestamp = datetime.now().strftime('%Y-%m-%dT%H:%M:%SZ')
    if recipientUid:
        recipientSid = userIdToSid[recipientUid]
        print(":::: ", recipientUid)
        roomName = recipientSid
        join_room(roomName)
        print(f"Sending message: '{msg}' to room: '{roomName}'")
        emit("data", {'message': msg, 'sender': userSid, 'timestamp': timestamp}, room=recipientSid)
    else:
        print(f"Broadcasting message: {msg}")
        # If recipientSid is not provided, broadcast the message to all connected clients
        emit("data", {'message': msg, 'sender': userSid,  'id': userSid, 'timestamp': timestamp}, broadcast=True)


@socketio.on("disconnect")
def disconnected():
    """event listener when client disconnects to the server"""
    print("user disconnected")
    emit("disconnect",f"user {request.sid} disconnected",broadcast=True)

@app.route("/", methods=["GET"])
def getServerStatus():
    return jsonify(userIdToSid), 201

@app.route("/login", methods=['POST'])
def login():
    data = request.json
    username = data.get("username")
    password = data.get("password")
    if(username == None):
        return "Username is required!", 400
    if(password == None):
        return "Password is required!", 400
    try:
        dbCur.execute(select_user, (username,))
        users = dbCur.fetchall()
        user = users[0] if len(users) > 0 else None
        if user and check_password_hash(user[2], password):
            access_token = create_access_token(identity=username, expires_delta=datetime.timedelta(days=30))
            return jsonify({"message": "Login successfully", "accessToken": access_token}), 201
        else:
            return jsonify({"message": "Incorrect username or password"}), 400
    except Exception as ex:
        return f"Unexpected {ex=}, {type(ex)=}", 400
            
@app.route("/register", methods=['POST'])
def register():
    data = request.json
    username = data.get("username")
    password = data.get("password")
    
    if(username == None):
        return "Username is required!", 400
    if(password == None):
        return "Password is required!", 400
    
    userUuid = uuid.uuid4()
    userId = str(userUuid)
    hashedPassword = generate_password_hash(password=password)

    try:
        dbCur.execute(insert_new_user, (userId, username, hashedPassword))
        dbCnx.commit()
        return jsonify({"message": "User registered successfully"}), 201
    except Exception as ex:
        if ex.errno == 1062:
            return "Username already taken.", 400
        else:
            return f"Unexpected {ex=}, {type(ex)=}", 400
        
@app.route("/chats", methods=['GET'])
@jwt_required()
def chats():
    current_username = get_jwt_identity()
    try:
        dbCur.execute(select_every_users_expect_own, (current_username, ))
        # getting columns name
        columns = [column[0] for column in dbCur.description]
        rows = dbCur.fetchall()
        # converting users as list of list to list of key value pairs 
        users = [dict(zip(columns, row)) for row in rows]
        return jsonify({"chats": users}), 201
    except Exception as ex:
        if ex.errno == 1062:
            return "Username already taken.", 400
        else:
            return f"Unexpected {ex=}, {type(ex)=}", 400

if __name__ == "__main__":
    socketio.run(app, debug=False)
