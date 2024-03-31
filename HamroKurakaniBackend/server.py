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

# loading environment variables from .env file
load_dotenv()

# settting up database variables
dbHost = os.getenv("DB_HOST")
dbUser = os.getenv("DB_USER")
dbPassword = os.getenv("DB_PASSWORD")
dbName = os.getenv("DB_NAME")

appSecret = os.getenv("APP_SECRET")

app = Flask(__name__)

# This will ensure "Cross Original Resource Sharing" errors do not occur
CORS(app)

# Seting up the Flask-JWT-Extended to handle authentication for this app
app.config["JWT_SECRET_KEY"] = os.getenv("JWT_SECRET_KEY")
jwt = JWTManager(app)

# Setting up Socket, which will handle the real time communication
app.config['SECRET_KEY'] = os.getenv("SOCKET_SECRET_KEY")
CORS(app,resources={r"/*":{"origins":"decoded_token*"}})
socketio = SocketIO(app,cors_allowed_origins="*")

from flask_jwt_extended import create_access_token, get_jwt_identity, jwt_required

current_directory = os.path.dirname(os.path.abspath(__file__))
app.config['UPLOAD_FOLDER'] = os.path.join(current_directory, 'static') # folder in with static files like images and video will be stored

# vaild file types
fileTypes = ["image", "video"]

# configuration for database
dbConfig = {
  'user': dbUser,
  'password': dbPassword,
  'host': dbHost,
  'database': dbName,
  'raise_on_warnings': True
}

# Connecting to the database and handeling any error that might occur
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

# USERS
insert_new_user = (
"INSERT INTO users (id, username, password)"
"VALUES (%s, %s, %s)")
select_user_by_username = "SELECT * from users WHERE username=%s"
select_user_by_usernames = "SELECT * from users WHERE username IN ({})"
select_user_by_id = "SELECT * from users WHERE id=%s"
select_every_users_expect_own = "SELECT id, username FROM users WHERE id!=%s"

# MESSAGES
insert_new_message = (
"INSERT INTO messages (id, sender_id, sender_username, receiver_user_id, receiver_group_id, content, content_type)"
"VALUES (%s, %s, %s, %s, %s, %s, %s)")
select_message_by_id = "SELECT * FROM messages WHERE id=%s"
select_messages_by_sender_receiver_user_id = "SELECT * FROM messages WHERE (sender_id = %s AND receiver_user_id = %s) OR (receiver_user_id = %s AND sender_id = %s) ORDER BY sent_at ASC"
select_messages_by_sender_receiver_group_id = "SELECT * FROM messages WHERE receiver_group_id = %s ORDER BY sent_at ASC"
update_message_by_id = "UPDATE messages SET content=%s, is_edited=True WHERE id=%s"
delete_message_by_id = "DELETE FROM messages WHERE id=%s"

# GROUPS
create_new_group = "INSERT INTO chatgroups (id, name) VALUES (%s, %s)"
select_chatgroups_by_ids = "SELECT * from chatgroups WHERE id IN ({})"

# USER GROUPS
add_group_members = (
"INSERT INTO usergroups (id, userid, chatgroupid)"
"VALUES (%s, %s, %s)") 
select_usergroups_by_userid = "SELECT * from usergroups WHERE userid=%s"

# these two are vise versa of each other
userIdToSid = {}
sidToUserId = {}

# Simple function that generates a random ID
def getUniqueId():
    uuidObj = uuid.uuid4()
    return str(uuidObj)

# This event establishes the connection between a user and our socket server
@socketio.on("connect")
def connected():
    access_token = request.args.get("access_token")
    if not access_token:
        return False
    try:
        decoded_token = decode_token(access_token) # validating 
        userId = decoded_token["sub"]
        print("client has connected with socket id:", request.sid)
        userIdToSid[userId] = request.sid
        sidToUserId[request.sid] = userId
        return userId
    except:
        return False

@socketio.on('new_message')
def handle_new_message(data):
    userSid = request.sid
    recipient_id_client = data.get('recipient_id')
    recipient_type = data.get('recipient_type')
    message_type = data.get('message_type')
    content = data.get("content")

    if not recipient_id_client:
        return "You need to send recipient id", 400

    recipient_id = None
    recipient_group_id = None
    
    if recipient_type == "user":
        recipient_id = recipient_id_client
    elif recipient_type == "group":
        recipient_group_id = recipient_id_client

    senderId = sidToUserId[userSid]

    if senderId:
        try:
            dbCur.execute(select_user_by_id, (senderId,))
            senders = dbCur.fetchall()
            sender = senders[0] if len(senders) > 0 else None

            if sender == None:
                return False
            
            senderUsername = sender[1]
            messageId = getUniqueId()

            # Storing the message
            dbCur.execute(insert_new_message, (messageId, senderId, senderUsername, recipient_id, recipient_group_id, content, message_type))
            dbCnx.commit()

            # retreiving a stored
            dbCur.execute(select_message_by_id, (messageId,))

            rows = dbCur.fetchall()
            columns = [column[0] for column in dbCur.description]
            messages = [dict(zip(columns, row)) for row in rows]
            newMessage = messages[0]

            roomName = recipient_id
            if recipient_type == "user" and recipient_id in userIdToSid:
                recipientSid = userIdToSid[recipient_id]
                roomName = recipientSid
                join_room(roomName)
            elif recipient_type == "group":
                roomName = recipient_group_id
                join_room(roomName)

            print(f"Sending message: '{messageId}' to room: '{roomName}'")
            emit("new_message", {"content":newMessage["content"], "content_type": newMessage["content_type"], "sender_username": newMessage['sender_username'], 'sent_at': newMessage['sent_at'].strftime("%a, %d %b %Y %H:%M:%S GMT")}, room=roomName)

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
        # fetching individual users that the current user can chat with
        dbCur.execute(select_every_users_expect_own, (current_userid, ))
        userCols = [column[0] for column in dbCur.description]
        userRows = dbCur.fetchall()
        users = [dict(zip(userCols, row)) for row in userRows]

        # fetching group ids of every group that the current user is a member of
        dbCur.execute(select_usergroups_by_userid, (current_userid, ))
        usergroupRows = dbCur.fetchall()
        chatgroupid = [row[2] for row in usergroupRows]

        groups = []
        if(len(chatgroupid) > 0):
            # finally fetching group data
            query = select_chatgroups_by_ids.format(','.join(['%s']*len(chatgroupid)))
            dbCur.execute(query, chatgroupid)
            groupCols = [column[0] for column in dbCur.description]
            chatGroups = dbCur.fetchall()
            groups = [dict(zip(groupCols, row)) for row in chatGroups]

        return jsonify({"chats": users, "groups": groups}), 201
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
    group_id = request.args.get('group_id')

    try:
        if second_userid is not None:
            # fetching user to user chat
            dbCur.execute(select_messages_by_sender_receiver_user_id, (current_userid, second_userid, current_userid, second_userid))
        elif group_id is not None:
            # fetching group chats
            dbCur.execute(select_messages_by_sender_receiver_group_id, (group_id, ))
        columns = [column[0] for column in dbCur.description]
        rows = dbCur.fetchall()
        chats = [dict(zip(columns, row)) for row in rows]
        return jsonify({"chats": chats}), 201
    except Exception as ex:
        if ex.errno == 1062:    
            return "Username already taken.", 400
        else:
            return f"Unexpected {ex=}, {type(ex)=}", 400
            
@app.route("/groups", methods=['POST'])
@jwt_required()
def create_group():
    current_userid = get_jwt_identity()

    data = request.json
    group_name = data.get("group_name")
    member_names = data.get("member_names")

    try:
        # fetching user ids based on their usernames
        query = select_user_by_usernames.format(','.join(['%s']*len(member_names)))
        dbCur.execute(query, member_names)
        rows = dbCur.fetchall()
        member_ids = [row[0] for row in rows]

        if current_userid not in member_ids:
            member_ids.append(current_userid)
        
        group_id = getUniqueId()
        # creating new group
        dbCur.execute(create_new_group, (group_id, group_name))
        dbCnx.commit()
        for member_id in member_ids:
            group_member_id = getUniqueId()
            dbCur.execute(add_group_members, (group_member_id, member_id, group_id))
            dbCnx.commit()

        return "done", 200
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
