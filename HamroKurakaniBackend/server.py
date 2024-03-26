from datetime import datetime, timedelta
from flask import Flask, request,jsonify, url_for
from flask_socketio import SocketIO,emit,send, join_room
from flask_cors import CORS
from dotenv import load_dotenv
import mysql.connector
from mysql.connector import errorcode
from werkzeug.security import generate_password_hash, check_password_hash
from flask_jwt_extended import create_access_token, get_jwt_identity, jwt_required, decode_token, JWTManager
import os
import uuid
from flask_cors import CORS
from werkzeug.utils import secure_filename
import traceback

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
CORS(app,resources={r"/*":{"origins":"decoded_token*"}})
socketio = SocketIO(app,cors_allowed_origins="*")

from flask_jwt_extended import create_access_token, get_jwt_identity, jwt_required

current_directory = os.path.dirname(os.path.abspath(__file__))
app.config['UPLOAD_FOLDER'] = os.path.join(current_directory, 'static')

# vaild file types
fileTypes = ["image", "video", "gif"]

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
insert_new_message = (
"INSERT INTO messages (id, sender_id, sender_username, receiver_user_id, receiver_group_id, content, content_type)"
"VALUES (%s, %s, %s, %s, %s, %s, %s)")

select_user_by_username = "SELECT * from users WHERE username=%s"
select_user_by_id = "SELECT * from users WHERE id=%s"
select_every_users_expect_own = "SELECT id, username FROM users WHERE id!=%s"
select_message_by_id = "SELECT * FROM messages WHERE id=%s"
select_messages_by_sender_receiver_id = "SELECT * FROM messages WHERE (sender_id = %s AND receiver_user_id = %s) OR (receiver_user_id = %s AND sender_id = %s)"

update_message_by_id = "UPDATE messages SET content=%s, is_edited=True WHERE id=%s"

delete_message_by_id = "DELETE FROM messages WHERE id=%s"

# these two are vise versa of each other
userIdToSid = {}
sidToUserId = {}

def getUniqueId():
    uuidObj = uuid.uuid4()
    return str(uuidObj)

@socketio.on("connect")
def connected():
    access_token = request.args.get("access_token")
    if not access_token:
        return False
    try:
        decoded_token = decode_token(access_token)
        userId = decoded_token["sub"] # this also performs token verification
        print("client has connected with socket id:", request.sid)
        userIdToSid[userId] = request.sid
        sidToUserId[request.sid] = userId
        return userId
    except:
        return False

@socketio.on('new_message')
def handle_new_message(data):
    userSid = request.sid
    recipient_id = data.get('recipient_id')
    message_type = data.get('content_type')
    message_content = data.get("content")

    # Handle different types of messages
    if message_type == 'text':
        content = message_content
    elif message_type in fileTypes:
        # Handle file uploads
        file = request.files['file']
        file_path = save_file(file)
        content = file_path

    recipientSid = userIdToSid[recipient_id]
    senderId = sidToUserId[userSid]

    if recipientSid and senderId:
        try:
            dbCur.execute(select_user_by_id, (senderId,))
            senders = dbCur.fetchall()
            sender = senders[0] if len(senders) > 0 else None

            if sender == None:
                return False
            
            senderUsername = sender[1]
            messageId = getUniqueId()

            # Storing the message
            dbCur.execute(insert_new_message, (messageId, senderId, senderUsername, recipient_id, None, content, message_type))
            dbCnx.commit()

            # retreiving a stored
            dbCur.execute(select_message_by_id, (messageId,))
            # messages = dbCur.fetchall()

            rows = dbCur.fetchall()
            columns = [column[0] for column in dbCur.description]
            messages = [dict(zip(columns, row)) for row in rows]
            newMessage = messages[0]

            roomName = recipientSid
            join_room(roomName)
            print(f"Sending message: '{messageId}' to room: '{roomName}'")

            emit("new_message", {"content":newMessage["content"], "content_type": newMessage["content_type"], "sender_username": newMessage['sender_username'], 'sent_at': newMessage['sent_at'].strftime("%a, %d %b %Y %H:%M:%S GMT")}, room=recipientSid)
        except Exception:
            traceback.print_exc()
            return False; 
    else:
        print(f"Broadcasting message: {messageId}")
        # # If recipientSid is not provided, broadcast the message to all connected clients
        # emit("data", {'message': msg, 'sender': userSid,  'id': userSid, 'timestamp': timestamp}, broadcast=True)

@socketio.on('edit')
def handle_edit_message(data):
    userSid = request.sid
    message_id = data.get('message_id')
    content = data.get("content")

    try:
        dbCur.execute(update_message_by_id, (content, message_id,))
        dbCnx.commit()

        dbCur.execute(select_message_by_id, (message_id,))

        rows = dbCur.fetchall()
        columns = [column[0] for column in dbCur.description]
        messages = [dict(zip(columns, row)) for row in rows]
        updated_message = messages[0]
                
        emit("edit", {"id": updated_message["id"], "content":updated_message["content"], "sender_username": updated_message['sender_username'], 'sent_at': updated_message['sent_at'].strftime("%a, %d %b %Y %H:%M:%S GMT")}, room=userSid)
    except:
        return False; 

@socketio.on('delete')
def handle_delete_message(data):
    userSid = request.sid
    message_id = data.get('message_id')
    try:
        dbCur.execute(delete_message_by_id, (message_id,))
        dbCnx.commit()                
        emit("delete", {"id": message_id}, room=userSid)
    except:
        return False; 

@socketio.on("disconnect")
def disconnected():
    """event listener when client disconnects to the server"""
    print("user disconnected")
    emit("disconnect",f"user {request.sid} disconnected",broadcast=True)

@app.route("/", methods=["GET"])
def getServerStatus():
    return jsonify({"userIdToSid": userIdToSid, "sidToUserId": sidToUserId }), 201

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
        dbCur.execute(select_user_by_username, (username,))
        users = dbCur.fetchall()
        user = users[0] if len(users) > 0 else None
        if user and check_password_hash(user[2], password):
            user_id = user[0]
            access_token = create_access_token(identity=user_id, expires_delta=timedelta(days=30))
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
    
    userId = getUniqueId()
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
    current_userid = get_jwt_identity()
    try:
        dbCur.execute(select_every_users_expect_own, (current_userid, ))
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

@app.route("/chat_history", methods=['GET'])
@jwt_required()
def chat_history():
    current_userid = get_jwt_identity()
    second_userid = request.args.get('user_id')
    try:
        dbCur.execute(select_messages_by_sender_receiver_id, (current_userid, second_userid, current_userid, second_userid))
        columns = [column[0] for column in dbCur.description]
        rows = dbCur.fetchall()
        chats = [dict(zip(columns, row)) for row in rows]
        return jsonify({"chats": chats}), 201
    except Exception as ex:
        if ex.errno == 1062:
            return "Username already taken.", 400
        else:
            return f"Unexpected {ex=}, {type(ex)=}", 400
        
@app.route("/upload_file", methods=['POST'])
# @jwt_required()
def upload_file():
    files = request.files
    file_type = request.form.get("file_type")
    if 'file' not in files:
        return jsonify({"error": "No files"}), 400
    if not file_type:
        return jsonify({"error": "No file type specified"}), 400
    if file_type not in fileTypes:
        return jsonify({"error": "Invalid file type"}), 400
    
    file = request.files['file']
    if file.filename == '':
        return jsonify({"error": "No selected file"}), 400
    
    if file:
        return save_file(file, file_type)

# It saves uploaded files into the server's filesystem
def save_file(file, type):
    orignal_filename = secure_filename(file.filename)
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    file_extension = os.path.splitext(orignal_filename)[1]
    unique_filename = f"{timestamp}_{uuid.uuid4().hex}{file_extension}" # constructing a unique file name
    file.save(os.path.join(f"{app.config['UPLOAD_FOLDER']}/{type}", unique_filename))
    return url_for('static', filename=f"{type}/{unique_filename}")

if __name__ == "__main__":
    socketio.run(app, debug=False)
