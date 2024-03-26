from fastapi import APIRouter, Depends, HTTPException, Response, status
from database import chat_rooms, chat_room_members, messages, users, get_db
from sqlalchemy import select
import databases
import databases.core
from sqlalchemy.sql import func
import json


from pydantic import BaseModel

class MessageEdit(BaseModel):
    new_text: str

router = APIRouter()


    

@router.get("/get-all-chats/{user_id}")
async def get_all_chats(user_id: int, db: databases.Database = Depends(get_db)):
    try:
        # Use the provided SQL query directly
# Get all chat_room_id of user_id from chat_room_member.user
# select distinct chats where user_id<>




        query = f"""
select crm.chat_room_id, cr.name as chat_room_name, cr.type,
case when cr.type = 1 then u.username else '' end as username,
m.message_text, m.message_type ,m.message_id
from
(select chat_room_id from chat_room_members where user_id = {user_id}) crs 
join
(select chat_room_id, user_id from chat_room_members where user_id <> {user_id}) crm
on crs.chat_room_id = crm.chat_room_id
join
(select chat_room_id, name, type, last_message_id from chat_rooms) cr
on cr.chat_room_id = crm.chat_room_id
JOIN
(select message_id, message_text, message_type from messages) m
on m.message_id = cr.last_message_id
join
(select user_id, username from users) u
on u.user_id = crm.user_id;      
"""

        result = await db.fetch_all(query)
        return result
    except Exception as e:
        return Response(
            content=str({"message": "Error.", "error": str(e)}),
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR
        )
@router.get("/get-messages/{chat_room_id}")
async def get_messages(chat_room_id: int, db: databases.Database = Depends(get_db)):
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
                unique_messages[message_id] = {
                    '_id': message_id,
                    'text': message['message_text'],
                    'createdAt': message['created_at'],
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
        # Return a success message
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

        # Return a success message
        return {"status": "Message deleted successfully"}

    except Exception as e:
        # Handle exceptions, log the error, and return an error response
        return {"status": "Error", "error": str(e)}