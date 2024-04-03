from fastapi import APIRouter, Depends, HTTPException, Query, status
from fastapi.responses import JSONResponse
from fastapi.encoders import jsonable_encoder
from pydantic import BaseModel

from database import users, get_db
import databases

router = APIRouter()


class AuthData(BaseModel):
    username: str
    password: str

@router.post("/login")
async def login(auth : AuthData , db: databases.Database = Depends(get_db)):
    print(auth.username)
    user = await db.fetch_one(users.select().where(users.c.username == auth.username))
    if user and auth.password == user['password']:
        return JSONResponse(content={"message": "Login successful","user":jsonable_encoder(user)})
    else:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid credentials")
