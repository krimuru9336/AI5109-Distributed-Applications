from pydantic import BaseModel
from fastapi import FastAPI, HTTPException, WebSocket, WebSocketDisconnect, Query
from fastapi.middleware.cors import CORSMiddleware
from typing import Optional, List, Dict
import json
from databases import Database

database = Database(
    'postgresql://default:9waurDfTAJc2@ep-plain-wildflower-76899077-pooler.us-east-1.postgres.vercel-storage.com:5432/verceldb', statement_cache_size=0
)

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.on_event("startup")
async def startup_db_client():
    await database.connect()


@app.on_event("shutdown")
async def shutdown_db_client():
    await database.disconnect()


class User(BaseModel):
    id: Optional[int] = None
    name: str
    email: str


@app.post("/user/", response_model=User)
async def create_item(user: User):
    try:
        query = "INSERT INTO users (name, email) VALUES (:name, :email) RETURNING id, name, email"
        result = await database.fetch_one(query, values={"name": user.name, "email": user.email})
        return result

    except Exception as e:
        raise HTTPException(
            status_code=500, detail=f"Database error: {e}"
        )


@app.get("/user/", response_model=List[User])
async def read_user(user_id: Optional[int] = None):
    try:
        if user_id is not None:
            query = "SELECT * FROM users WHERE id=:id"
            result = await database.fetch_one(query, values={"id": user_id})
            if result is None:
                raise HTTPException(status_code=404, detail="User not found")
            return [result]
        else:
            query_all = "SELECT * FROM users"
            try:
                results = await database.fetch_all(query_all)
            except Exception as e:
                print(e)
            if not results:
                raise HTTPException(status_code=404, detail="No users found")
            return results
    except Exception as e:
        raise HTTPException(
            status_code=500, detail=f"Database error: {e}"
        )


# Websockets

clients: Dict[int, WebSocket] = {}


@app.get("/past_messages/", response_model=List[Dict])
async def get_past_messages(sender_id: int = Query(...), reciever_id: int = Query(...)):
    try:
        query = "SELECT message as text, receiver_id as recieverId, sender_id as senderId, timestamp  FROM messages WHERE (receiver_id = :reciever_id AND sender_id = :sender_id) OR (receiver_id = :sender_id AND sender_id = :reciever_id) ORDER BY timestamp"
        past_messages = await database.fetch_all(query, values={"reciever_id": reciever_id, "sender_id": sender_id})
        return past_messages
    except Exception as e:
        raise HTTPException(
            status_code=500, detail=f"Database error: {e}")


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
                            'error': f"Error: Client {target_client_id} not found"
                        }))
                else:
                    await websocket.send_text(json.dumps({
                        'error': f"Error: Client {target_client_id} not found"
                    }))

                if client_id:
                    client_websocket = clients.get(client_id)
                    if client_websocket:
                        await client_websocket.send_text(json.dumps(data))
                    else:
                        await websocket.send_text(json.dumps({
                            'error': f"Error: Client {client_id} not found"
                        }))
                else:
                    await websocket.send_text(json.dumps({
                        'error': f"Error: Client {client_id} not found"
                    }))

                try:
                    query = "INSERT INTO messages (message, sender_id, receiver_id, timestamp) VALUES (:text, :sender_id, :reciever_id, :timestamp)"
                    values = {
                        "text": data.get('text'),
                        "sender_id": data.get('senderId'),
                        "reciever_id": data.get('recieverId'),
                        "timestamp": data.get('timestamp')
                    }
                    await database.execute(query, values)
                except Exception as e:
                    raise HTTPException(
                        status_code=500, detail=f"Database error: {e}")

            else:
                print("Error with the data", data)

    except WebSocketDisconnect:
        clients.pop(client_id, None)

if __name__ == "__main__":
    import uvicorn

    uvicorn.run("main:app", host="::", port=8000, reload=True)
