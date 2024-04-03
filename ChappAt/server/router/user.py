from fastapi import APIRouter, Depends, HTTPException, status
from fastapi.encoders import jsonable_encoder
from fastapi.responses import JSONResponse

from database import users, get_db
from pydantic import BaseModel
import databases
import json
from sqlalchemy import select

router = APIRouter()

class UserRegistration(BaseModel):
    username: str
    password: str

@router.post("/register")
async def register(user_data: UserRegistration, db: databases.Database = Depends(get_db)):
    query = select(users).where(users.c.username == user_data.username)
    user = await db.fetch_one(query)
    if user:
        raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail="Username Already Exists")
    else:
        query = users.insert().values(username=user_data.username, password=user_data.password)
        last_record_id = await db.execute(query)
        print(last_record_id)
        if last_record_id:
            query = select(users).where(users.c.user_id == last_record_id)
            user = await db.fetch_one(query)
            print(user)
            return {"message": "User registered successfully","user":user}
        else:
            raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Registration Unsuccessful")
    



@router.get("/get-all/{user_id}")
async def get_all_users(user_id: int, db: databases.Database = Depends(get_db)):
    try:
        query = select(users.c.username,users.c.user_id).where(users.c.user_id != user_id)
        return await db.fetch_all(query)    
    except databases.DatabaseError as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error Getting Users: {str(e)}"
        )

@router.get("/get-user/{user_id}")
async def get_user_by_id(user_id: int, db: databases.Database = Depends(get_db)):
    try:
        query = select(users).where(users.c.user_id == user_id)
        user = await db.fetch_one(query)
        if user:
            return user
        else:
            raise HTTPException(
                status_code=status.HTTP_404_NOT_FOUND,
                detail="User not found"
            )
    except databases.DatabaseError as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error Getting User: {str(e)}"
        )
    

@router.get("/search-users")
async def search_users(query: str, db: databases.Database = Depends(get_db)):
    try:
        search_query = f"%{query}%"
        query = select(users).where(users.c.username.ilike(search_query))
        result = await db.fetch_all(query)
        return result
    except databases.DatabaseError as e:
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Error Searching Users: {str(e)}"
        )