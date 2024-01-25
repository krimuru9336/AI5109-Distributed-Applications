from fastapi import APIRouter, Depends, HTTPException, Query, status
from fastapi.responses import JSONResponse
from fastapi.encoders import jsonable_encoder

from database import users, get_db
import databases

router = APIRouter()

@router.get("/login")
async def login(username: str = Query(...), password: str = Query(...), db: databases.Database = Depends(get_db)):
    print(username)
    user = await db.fetch_one(users.select().where(users.c.username == username))
    if user and password == user['password']:
        return JSONResponse(content={"message": "Login successful","user":jsonable_encoder(user)})
    else:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid credentials")
