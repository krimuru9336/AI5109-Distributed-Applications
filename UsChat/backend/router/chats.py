from fastapi import APIRouter, Depends, Form, HTTPException, Response, status, UploadFile, File
from database import chat_rooms, chat_room_members, messages, users, get_db,MessageType
from sqlalchemy import select
import databases
import databases.core
from sqlalchemy.sql import func
import uuid
from typing import List
from datetime import datetime
from typing_extensions import Annotated
from sqlalchemy.sql import update
from typing import Optional
from .websocket import send_update_to_websocket

baseUrl = "https://18cb-2405-201-1004-1aeb-9869-b419-899e-4b66.ngrok-free.app"

from pydantic import BaseModel

class MessageEdit(BaseModel):
    new_text: str

class MessageWithMedia(BaseModel):
    chat_room_id: str
    sender_id: int
    filename: str
    message_type:int

class StartChat(BaseModel):
    name : Optional[str] = None
    is_group: bool = False
    user_ids: List[int]
    

router = APIRouter()


@router.get("/get-all-chats/{user_id}")
async def get_all_chats(user_id: int, db: databases.Database = Depends(get_db)):
    try:
        # Use the provided SQL query directly
# Get all chat_room_id of user_id from chat_room_member.user
# select distinct chats where user_id<>




        query = f"""
select crm.chat_room_id, cr.name as chat_room_name, cr.type,
case when cr.type = 1 then u.username else '' end as username,u.user_id,
m.message_text, m.message_type ,m.message_id
from
(select chat_room_id from chat_room_members where user_id = {user_id}) crs 
join
(select chat_room_id, user_id from chat_room_members where user_id <> {user_id}) crm
on crs.chat_room_id = crm.chat_room_id
join
(select chat_room_id, name, type, last_message_id from chat_rooms) cr
on cr.chat_room_id = crm.chat_room_id
LEFT JOIN
(select message_id, message_text, message_type from messages) m
on m.message_id = cr.last_message_id
join
(select user_id, username from users) u
on u.user_id = crm.user_id GROUP BY crm.chat_room_id;      
"""

        result = await db.fetch_all(query)
        return result
    except Exception as e:
        return Response(
            content=str({"message": "Error.", "error": str(e)}),
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR
        )
@router.get("/get-messages/{chat_room_id}")
async def get_messages(chat_room_id: str, db: databases.Database = Depends(get_db)):
    try:
        # Query to fetch messages for the given chat_room_id
        query = (
            select(messages, chat_room_members,users)
            .join(chat_room_members, messages.c.chat_room_id == chat_room_members.c.chat_room_id)
            .join(users, messages.c.sender_id == users.c.user_id)
            .where(chat_room_members.c.chat_room_id == chat_room_id)
            .order_by(messages.c.created_at.desc())
        )

        result = await db.fetch_all(query)
        unique_messages = {}
        for message in result:
            message_id = str(message['message_id'])
            if message_id not in unique_messages:
                text =  message['message_text'] if message['message_type'] == MessageType.text else  ''
                image = f"{baseUrl}/media_files/{message['message_text']}" if message["message_type"] != MessageType.text else ''
                
                unique_messages[message_id] = {
                    '_id': message_id,
                    'text': text,
                    'createdAt': message['created_at'],
                    'image': image ,
                    'user': {
                        '_id': message['sender_id'],
                        'name': message['username'],
                        # Add more user details as needed
                    },
                }

        # Convert the dictionary values to a list
        gifted_chat_messages = list(unique_messages.values())

        return gifted_chat_messages


    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error Getting Messages: {str(e)}"
        )


# Edit message
@router.put("/edit-message/{message_id}")
async def edit_message(message_id: str, message_edit: MessageEdit, db: databases.Database = Depends(get_db)):
    try:
        # Perform the update query using db.execute
        query = messages.update().where(messages.c.message_id == message_id).values(message_text=message_edit.new_text)
        await db.execute(query)
        query = select(messages).where(messages.c.message_id == message_id)
        response = await db.fetch_one(query)
        # Return a success message
        chat_room_id = response.chat_room_id
        print(chat_room_id);
        await send_update_to_websocket(chat_room_id)
        return {"status": "Message edited successfully"}
    except Exception as e:
        # Handle exceptions, log the error, and return an error response
        return {"status": "Error", "error": str(e)}

# Delete message
@router.delete("/delete-message/{message_id}")
async def delete_message(message_id: str, db: databases.Database = Depends(get_db)):
    try:
        # Get the chat_room_id associated with the current message_id
        chat_room_id_query = select([messages.c.chat_room_id]).where(messages.c.message_id == message_id)
        chat_room_id = await db.fetch_val(chat_room_id_query)

        # Get the last_message_id of the chat_room (excluding the current message_id)
        last_message_query = (
            select([messages.c.message_id])
            .where(messages.c.chat_room_id == chat_room_id)
            .where(messages.c.message_id != message_id)
            .order_by(messages.c.created_at.desc())
            .limit(1)
        )
        last_message_id = await db.fetch_val(last_message_query)

        # Update the corresponding chat_rooms row with the new last_message_id
        update_query = chat_rooms.update().values(last_message_id=last_message_id).where(chat_rooms.c.chat_room_id == chat_room_id)
        await db.execute(update_query)

        # Now, delete the message
        delete_query = messages.delete().where(messages.c.message_id == message_id)
        await db.execute(delete_query)
        await send_update_to_websocket(chat_room_id)

        # Return a success message
        return {"status": "Message deleted successfully"}

    except Exception as e:
        # Handle exceptions, log the error, and return an error response
        return {"status": "Error", "error": str(e)}
    

@router.post("/upload_media")
async def upload_media(
    file: bytes = File(...), 
    name: str = Form()
    ):
    file_type = name.split('.')[-1]
    image_ext = ['jpg','png','jpeg','heic']
    gif_ext = ['gif','webp']
    video_ext = ['mp4']
    message_type = None

    if(file_type in image_ext):
        message_type = MessageType.image
    elif (file_type in gif_ext):
        message_type = MessageType.gif
    elif (file_type in video_ext):
        message_type = MessageType.video

    if(message_type is not None):
        uniqueFileName = f"{uuid.uuid4()}.{file_type}"

        with open(f"media_files/{uniqueFileName}", "wb") as f:
            f.write(file)
            f.close()

        return {"filename": uniqueFileName, "message_type" : message_type}
    else:
        return {"status": "Error", "error": "Invalid File Type"}


@router.post("/insert-media-message")
async def insert_media_message(
    data : MessageWithMedia,
    db: databases.Database = Depends(get_db)
):

    try:
        message_id = str(uuid.uuid4())
        query = messages.insert().values(
            message_id=message_id,
            sender_id=data.sender_id,
            message_text=data.filename,  # Store the filename as the message text
            message_type=MessageType(data.message_type).name,
            created_at= datetime.utcnow(),
            edited_at=datetime.utcnow(),
            chat_room_id=data.chat_room_id
        )
        await db.execute(query)
        query = update(chat_rooms).where(
            (chat_rooms.c.chat_room_id == data.chat_room_id)
        ).values(last_message_id=message_id)
        await db.execute(query)
        await send_update_to_websocket(data.chat_room_id)
        # Return a success response
        return {"status": "Message sent with image successfully" , "message_id":message_id}
    except Exception as e:
        print(f"Exception Here, ${e}")
        # Handle exceptions, log the error, and return an error response
        return {"status": "Error", "error": str(e)}


@router.post("/start-chat")
async def insert_media_message(
    data : StartChat,
    db: databases.Database = Depends(get_db)
):

    try:
        chat_type = 2 if data.is_group  else 1 
        group_name = None if not data.is_group else data.name
        chat_room_id = f"{uuid.uuid4()}";
        query = chat_rooms.insert().values(
            chat_room_id=chat_room_id,
            name = group_name,
            type = chat_type
        )
        response =await db.execute(query)
        print(response)
        if(chat_room_id):
            for user_id in data.user_ids:
                query = chat_room_members.insert().values(user_id=user_id, chat_room_id = chat_room_id)
                await db.execute(query)
            query = select(chat_rooms).where(chat_rooms.c.chat_room_id == chat_room_id)
            chat = await db.fetch_one(query)
            return {"status": "Chat room created" , "chat":chat}
    except Exception as e:
        print(f"Exception Here, ${e}")
        # Handle exceptions, log the error, and return an error response
        return {"status": "Error", "error": str(e)}