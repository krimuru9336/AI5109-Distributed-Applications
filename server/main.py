from typing import List, Optional, Dict
import uvicorn
from fastapi import  FastAPI, Depends, HTTPException, status, WebSocket, WebSocketDisconnect
from fastapi.middleware.cors import CORSMiddleware
from fastapi import FastAPI, File, UploadFile, Form
from pydantic import BaseModel
import mysql.connector
from mysql.connector import Error 
import json
import os
from datetime import datetime
from typing import Any
from fastapi.staticfiles import StaticFiles

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"]
)

connection = mysql.connector.connect(
        host='localhost',
        user='root',
        password='hath2153',
        database='chatapp'
    )

class User(BaseModel):
    id: Optional[int] = None
    username:str
    password:str
class Group(BaseModel):
    id: Optional[int] = None
    name:str
    user_ids: str


class UserID(BaseModel):
    id:int

class Message(BaseModel):
    _id: str 
    createdAt: str
    receiver_id: int
    user: UserID

class DeleteData(BaseModel):
    message_id:str

class EditMessage(BaseModel):
    message: Any


# Register a new user
@app.post("/register/")
async def register_user(user: User):
    cursor = connection.cursor()

    try:
        # Check if the username is already registered
        cursor.execute("SELECT * FROM users WHERE username = %s", (user.username,))
        existing_user = cursor.fetchone()
        if existing_user:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="username is already registered",
            )

        # Insert the new user
        cursor.execute("INSERT INTO users (username, password) VALUES (%s, %s)", (user.username, user.password))
        user.id = cursor.lastrowid
        connection.commit()

    except Error as e:
        print(f"Error: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Internal Server Error",
        )

    finally:
        cursor.close()

    return user



   
#Get_Users Endpoint
@app.get("/get-users/")
async def get_registered_users():
    cursor = connection.cursor(dictionary=True)

    try:
        # Fetch the list of registered users from the database
        cursor.execute("SELECT username,id FROM users")
        registered_users = cursor.fetchall()
        return registered_users

    except Error as e:
        print(f"Error: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Internal Server Error",
        )

    finally:
        cursor.close()

connections: Dict[int, WebSocket] = {}

@app.websocket("/ws/{user_id}")
async def websocket_endpoint(websocket: WebSocket, user_id: int):
    await websocket.accept()
    connections[user_id] = websocket

    try:
        while True:
            data = await websocket.receive_text()
            data = json.loads(data)
            receiver_id = data.get('receiver_id')
            group_id = data.get('group_id')
            sender_id = int(user_id)                
            if(data.get('type') == 'send'):
                timestamp = datetime.utcnow()

                # Save the message to the database
                cursor = connection.cursor()
                query = """
                INSERT INTO messages (text,receiver_id,user_id,timestamp,group_id)
                VALUES (%s, %s, %s, %s, %s)
                """
                values = (json.dumps(data), None if group_id else receiver_id, sender_id, timestamp,group_id)
                cursor.execute(query, values)
                connection.commit()
                cursor.close()

                # Broadcast the message to the reciever
                if group_id:
                    if len(receiver_id) != 0:
                        for id in receiver_id:
                            if int(id) != sender_id:
                                receiver_socket = connections.get(int(id))
                                if receiver_socket:
                                    await receiver_socket.send_json(data)
                else:
                    if receiver_id:
                        receiver_socket = connections.get(receiver_id)
                        if receiver_socket:
                            await receiver_socket.send_json(data)
            elif(data.get('type') == 'refresh'):
                print(group_id)
                if group_id:
                    if len(receiver_id) != 0:
                        for id in receiver_id:
                            if int(id) != sender_id:
                                receiver_socket = connections.get(int(id))
                                if receiver_socket:
                                    await receiver_socket.send_json({
                                            'type':'refresh'
                                        })
                else:
                    if receiver_id:
                        receiver_socket = connections.get(receiver_id)
                        if receiver_socket:
                            await receiver_socket.send_json({
                            'type':'refresh'
                        })
                

            
    except WebSocketDisconnect:
        # Remove disconnected WebSocket
        connections.remove(websocket)


@app.get("/get-user-id/{username}")
async def get_user_id(username: str):
    cursor = connection.cursor(dictionary=True)

    try:
        # Fetch the user ID based on the username
        cursor.execute("SELECT id FROM users WHERE username = %s", (username,))
        user = cursor.fetchone()
        if user:
            return user["id"]
        else:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail=f"User with username {username} not found",
            )

    except Error as e:
        print(f"Error: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Internal Server Error",
        )

    finally:
        cursor.close()


#get_chat_history Endpoint
@app.get("/get-chat-history/{sender_id}/{receiver_id}")
async def get_chat_history(sender_id ,receiver_id):
    cursor = connection.cursor(dictionary=True)

    try:
        # Fetch  old chats between two users
        cursor = connection.cursor(dictionary=True)
        query ="SELECT * from messages where (user_id = %s and receiver_id = %s) or (user_id= %s and receiver_id = %s) AND group_id = NULL order by timestamp desc"
        cursor.execute(query,[sender_id,receiver_id,receiver_id,sender_id])
        messages = cursor.fetchall()
        return messages

    except Error as e:
        print(f"Error: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Internal Server Error",
        )

    finally:
        cursor.close()

@app.get("/get-group-history/{group_id}")
async def get_group_history(group_id):
    cursor = connection.cursor(dictionary=True)

    try:
        # Fetch  old chats in a group
        cursor = connection.cursor(dictionary=True)
        query ="SELECT * from messages where group_id = %s order by timestamp desc"
        cursor.execute(query,[group_id])
        messages = cursor.fetchall()
        return messages

    except Error as e:
        print(f"Error: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Internal Server Error",
        )

    finally:
        cursor.close()



@app.post("/delete-message/")
async def delete_message(data:DeleteData):
    print(data.message_id)
    cursor= connection.cursor()
    try:
        cursor.execute("DELETE FROM messages WHERE text LIKE %s", ['%' + data.message_id + '%'])
        connection.commit()

    except Error as e:
        print(f"Error: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Deleting message Failed: Internal Server Error",
        )

    finally:
        cursor.close()


@app.put("/edit-message/")
async def edit_message(data:EditMessage):
    cursor= connection.cursor()
    message_id = data.message.get('_id', '')
    print(message_id)
    print(json.dumps(data.message))
    try:
        if(message_id):
            cursor.execute("update messages set text = %s WHERE text LIKE %s ", [json.dumps(data.message), '%'+ message_id + '%'])
            connection.commit()
        else:
            print('message_id missing')

    except Error as e:
        print(f"Error: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Deleting message Failed: Internal Server Error",
        )

    finally:
        cursor.close()

@app.post("/upload/")
def upload_image(file: bytes = File(...), name: str = Form(), type: str = Form()):
    app.mount("/media", StaticFiles(directory="media"), name="media")
    file_path = os.path.join('media', name)
   
    try:
        
        with open(file_path, "wb") as f:
            f.write(file)
        return {"filename": name}

    except Error as e:
        print(f"Error: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Image upload to server failed : Internal Server Error",
        )
#Create New Group
@app.post("/create-group/")
async def create_group(group: Group):
    print(group.name,group.user_ids)
    cursor = connection.cursor()
    try:
        # Insert the new group
        cursor.execute("INSERT INTO `groups` (name,user_ids) VALUES (%s, %s)", (group.name, group.user_ids))
        group.id = cursor.lastrowid
        connection.commit()

    except Error as e:
        print(f"Error: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Internal Server Error",
        )
    finally:
        cursor.close()
    return group

#Read goups

@app.get("/get-groups/")
async def get_groups():
    cursor = connection.cursor(dictionary=True)

    try:
        # Fetch the list of registered users from the database
        cursor.execute("SELECT id,name,user_ids FROM `groups`")
        groups = cursor.fetchall()
        return groups

    except Error as e:
        print(f"Error: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Internal Server Error",
        )

    finally:
        cursor.close()


if __name__ == "__main__":
    uvicorn.run('main:app',host='0.0.0.0', port=8001, reload=True)

