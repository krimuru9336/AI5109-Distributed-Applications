from fastapi import APIRouter, Depends, WebSocket, WebSocketDisconnect
from typing import List
from database import get_db,MessageType,messages,chat_rooms
import databases
import datetime
import uuid
import json
from sqlalchemy.sql import select, update

router = APIRouter()

# Dictionary to store lists of WebSocket instances per room_id
websocket_connections = {}

@router.websocket("/ws/{room_id}")
async def websocket_endpoint(websocket: WebSocket, room_id: str, db: databases.Database = Depends(get_db)):
    print(f"WebSocket connection accepted for room_id: {room_id}")
    await websocket.accept()

    try:
        # Check if room_id key exists in the dictionary
        if room_id not in websocket_connections:
            # If not, create a new list for this room_id
            websocket_connections[room_id] = []

        # Append the current WebSocket instance to the list
        websocket_connections[room_id].append(websocket)

        while True:
            data = await websocket.receive_text()
            print(f"Received message for room_id {room_id}: {data}")

            # Process the received message (example: insert into messages table)
            message_data = json.loads(data)
            await insert_message_into_db(db, room_id, message_data)

            # Broadcast the processed message to all WebSocket instances for this room_id
            for connection in websocket_connections.get(room_id, []):
                await connection.send_json(message_data)
    except WebSocketDisconnect:
        print(f"WebSocket connection closed for room_id: {room_id}")
    finally:
        # Remove the WebSocket instance from the list when the connection is closed
        websocket_connections.get(room_id, []).remove(websocket)

async def insert_message_into_db(db: databases.Database, room_id: str, message_data: dict):
    # Insert into messages table
    query = messages.insert().values(
        message_id=message_data["_id"],
        sender_id=message_data["user"]["_id"],
        message_text=message_data["text"],
        message_type=MessageType.text,
        created_at=datetime.datetime.utcnow(),
        edited_at=datetime.datetime.utcnow(),
        chat_room_id=room_id
    )
    message_id = await db.execute(query)

    # Update last_message_id in chat_room_members table
    query = update(chat_rooms).where(
        (chat_rooms.c.chat_room_id == room_id)
    ).values(last_message_id=message_data["_id"])
    await db.execute(query)
