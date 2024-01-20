from typing import List, Optional, Dict
import uvicorn
from fastapi import  FastAPI, Depends, HTTPException, status, WebSocket, WebSocketDisconnect
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
import mysql.connector
from mysql.connector import Error 
import json
from datetime import datetime

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
        password='Hauva@123',
        database='chatapp'
    )

class User(BaseModel):
    id: Optional[int] = None
    username:str
    password:str

class Message(BaseModel):
    user_id: int 
    receiver_id: int
    message: str
    timestamp: datetime


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
            sender_id = int(user_id)
            receiver_id = data.get('receiver_id')
            timestamp = datetime.utcnow()
            
            # Save the message to the database
            cursor = connection.cursor()
            query = """
            INSERT INTO messages (text,receiver_id,user_id,timestamp)
            VALUES (%s, %s, %s, %s)
            """
            values = (json.dumps(data), receiver_id, sender_id, timestamp)
            cursor.execute(query, values)
            connection.commit()
            cursor.close()

            # Broadcast the message to the reciever
            if receiver_id:
                receiver_socket = connections.get(receiver_id)
                if receiver_socket:
                    print("SENDING")
                    await receiver_socket.send_json(data)
            # if sender_id:
            #     sender_socket = connections.get(sender_id)
            #     if sender_socket:
            #         await sender_socket.send_json(data)
            
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
        query ="SELECT * from messages where (user_id = %s and receiver_id = %s) or (user_id= %s and receiver_id = %s) order by timestamp desc"
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

if __name__ == "__main__":
    uvicorn.run('main:app',host='0.0.0.0', port=8001, reload=True)

