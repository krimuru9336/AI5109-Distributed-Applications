from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from database import get_db
from router import auth, user, websocket ,chats  
import os
from fastapi.staticfiles import StaticFiles

MEDIA_DIR = "media_files"
if not os.path.exists(MEDIA_DIR):
    os.makedirs(MEDIA_DIR)
app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"], 
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)
app.mount("/media_files", StaticFiles(directory="media_files"), name="media_files")

app.include_router(auth.router, prefix="/auth", tags=["auth"])
app.include_router(user.router, prefix="/users", tags=["users"])
app.include_router(chats.router, prefix="/chats", tags=["chats"])  
app.include_router(websocket.router, prefix="/websocket", tags=["websocket"])