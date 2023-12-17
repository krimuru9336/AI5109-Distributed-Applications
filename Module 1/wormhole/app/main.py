from fastapi import FastAPI
from sqlalchemy import create_engine, Column, String, Integer
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
from dotenv import load_dotenv
import os

load_dotenv()

DATABASE_USERNAME = os.getenv("DA_DATABASE_USERNAME")
DATABASE_PASSWORD = os.getenv("DA_DATABASE_PASSWORD")
DATABASE_HOST = os.getenv("DA_DATABASE_HOST")
DATABASE_NAME = os.getenv("DA_DATABASE_NAME")

DATABASE_URL = f"mysql+mysqlconnector://{DATABASE_USERNAME}:{DATABASE_PASSWORD}@{DATABASE_HOST}/{DATABASE_NAME}"
engine = create_engine(DATABASE_URL)
Base = declarative_base()

class TextStorage(Base):
    __tablename__ = "text_storage"
    id = Column(Integer, primary_key=True, index=True)
    text = Column(String(255), index=True)  # Specify the length for VARCHAR


Base.metadata.create_all(bind=engine)

app = FastAPI()

SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

@app.post("/save-text")
async def save_text(text: dict):
    received_text = text.get("text", "")
    
    db = SessionLocal()
    db_text = TextStorage(text=received_text)
    db.add(db_text)
    db.commit()
    db.refresh(db_text)
    db.close()
    
    return {"message": "Text saved successfully", "received_text": received_text}

@app.get("/retrieve-text")
async def retrieve_text():
    db = SessionLocal()
    db_text = db.query(TextStorage).order_by(TextStorage.id.desc()).first()
    db.close()
    
    if db_text:
        return {"text": db_text.text}
    else:
        return {"text": "No text found"}
