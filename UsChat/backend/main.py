from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from database import get_db
from router import auth, user, websocket ,chats  

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"], 
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(auth.router, prefix="/auth", tags=["auth"])
app.include_router(user.router, prefix="/users", tags=["users"])
app.include_router(chats.router, prefix="/chats", tags=["chats"])  
app.include_router(websocket.router, prefix="/websocket", tags=["websocket"])