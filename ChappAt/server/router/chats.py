from fastapi import APIRouter, Depends, HTTPException, Response, status
from database import chat_rooms, chat_room_members, messages, users, get_db
from sqlalchemy import select
import databases
import databases.core
from sqlalchemy.sql import func
import json
router = APIRouter()

# ... (existing code)

# @router.get("/get-all-chats/{user_id}")
# async def get_all_chats(user_id: int, db: databases.Database = Depends(get_db)):
#     try:
#         # Join chat_room_members, chat_rooms, messages, and users tables to get all chats with user details
#         query = (
#             select(chat_room_members, chat_rooms, messages, users.c.username)
#             .join(chat_rooms, chat_room_members.c.chat_room_id == chat_rooms.c.chat_room_id)
#             .join(messages, chat_rooms.c.chat_room_id == messages.c.chat_room_id)
#             .join(users, messages.c.receiver_id == users.c.user_id)  
#             .where(chat_room_members.c.user_id == user_id)
#             .order_by(messages.c.created_at.desc())
#         )

#         result = await db.fetch_all(query)
#         return result
#     except databases.DatabaseError as e:
#         raise HTTPException(
#             status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
#             detail=f"Error Getting Chats: {str(e)}"
#         )
    

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
            .order_by(messages.c.created_at)
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
