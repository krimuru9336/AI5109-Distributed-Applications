from pydantic import BaseModel
from fastapi import FastAPI, HTTPException, WebSocket, WebSocketDisconnect, Query
import mysql.connector
from fastapi.middleware.cors import CORSMiddleware
from typing import Optional, List, Dict
import json
from memory_profiler import profile

conn = mysql.connector.connect(
    host="localhost",
    user="root",
    password="6jubwe32",
    database="chitchat"
)

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


class User(BaseModel):
    id: Optional[int] = None
    name: str
    email: str


@app.post("/user/", response_model=User)
def create_item(user: User):
    cursor = conn.cursor()
    try:
        query = "INSERT INTO users (name, email) VALUES (%s, %s)"
        cursor.execute(query, (user.name, user.email))
        conn.commit()
        user.id = cursor.lastrowid
        cursor.close()
        return user

    except mysql.connector.Error as err:
        if err.errno == 1062:  # MySQL error code for duplicate entry
            raise HTTPException(
                status_code=400, detail="Duplicate email, user already exists")
        else:
            # Handle other database errors
            raise HTTPException(
                status_code=500, detail=f"Database error: {err}")


@app.get("/user/", response_model=List[User])
def read_user(user_id: Optional[int] = None):
    cursor = conn.cursor()
    try:
        if user_id is not None:
            # If user_id is provided, fetch a specific user
            query = "SELECT * FROM users WHERE id=%s"
            cursor.execute(query, (user_id,))
            user = cursor.fetchone()
            cursor.close()
            if user is None:
                raise HTTPException(status_code=404, detail="User not found")
            print(user[0])
            return [{"id": user[0], "email": user[1], "name": user[2]}]
        else:
            # If no user_id is provided, fetch all users
            query_all = "SELECT * FROM users"
            cursor.execute(query_all)
            users = cursor.fetchall()
            cursor.close()
            if not users:
                raise HTTPException(status_code=404, detail="No users found")
            # Convert the result to a list of dictionaries
            return [{"id": user[0], "email": user[1], "name": user[2]} for user in users]
    except mysql.connector.Error as err:
        raise HTTPException(
            status_code=500, detail=f"Database error: {err}")


# Websockets

clients: Dict[int, WebSocket] = {}


@app.get("/past_messages/", response_model=List[Dict])
async def get_past_messages(sender_id: int = Query(...), reciever_id: int = Query(...)):
    try:
        cursor = conn.cursor(dictionary=True)
        query = "SELECT message as text, receiver_id as recieverId, sender_id as senderId, timestamp  FROM messages WHERE (receiver_id = %s AND sender_id = %s) OR (receiver_id = %s AND sender_id = %s) ORDER BY timestamp"
        cursor.execute(query, (sender_id, reciever_id, reciever_id, sender_id))
        past_messages = cursor.fetchall()
        cursor.close()
        return past_messages
    except mysql.connector.Error as err:
        raise HTTPException(
            status_code=500, detail=f"Database error: {err}")


@profile
@app.websocket("/ws/{client_id}")
async def websocket_endpoint(websocket: WebSocket, client_id: int):
    await websocket.accept()
    clients[client_id] = websocket
    try:
        while True:
            # Receive message from the client
            data = await websocket.receive_text()

            if (data):

                try:
                    data = json.loads(data)
                except json.JSONDecodeError as e:
                    print(f"Error decoding JSON: {e}")
                    continue

                target_client_id = data.get("recieverId")
                print(target_client_id)

                # Send the received message to the specific client
                if target_client_id:
                    target_websocket = clients.get(target_client_id)
                    if target_websocket:
                        await target_websocket.send_text(json.dumps(data))
                    else:
                        await websocket.send_text(json.dumps({
                            'error': "Error: Client {target_client_id} not found"
                        }))
                else:
                    await websocket.send_text(json.dumps({
                        'error': "Error: Client {target_client_id} not found"
                    }))

                if client_id:
                    client_websocket = clients.get(client_id)
                    if client_websocket:
                        await client_websocket.send_text(json.dumps(data))
                    else:
                        await websocket.send_text(json.dumps({
                            'error': "Error: Client {client_id} not found"
                        }))
                else:
                    await websocket.send_text(json.dumps({
                        'error': "Error: Client {client_id} not found"
                    }))

                try:
                    cursor = conn.cursor()
                    query = "INSERT INTO messages (message, sender_id, receiver_id, timestamp) VALUES (%s, %s, %s, %s)"
                    cursor.execute(query, (data.get('text'), data.get(
                        'senderId'), data.get('recieverId'), data.get('timestamp')))
                    conn.commit()
                    cursor.close()
                except mysql.connector.Error as err:
                    raise HTTPException(
                        status_code=500, detail=f"Database error: {err}")

            else:
                print("Error with the data", data)

    except WebSocketDisconnect:
        clients.pop(client_id, None)


if __name__ == "__main__":
    import uvicorn

    if (conn):
        uvicorn.run("main:app", host="::", port=8000, reload=True)
    else:
        print("MYSQL CONNECTION ERROR")
