from pydantic import BaseModel, EmailStr, Field, datetime

class User(BaseModel):
    email: EmailStr
    username: str = Field(..., min_length=3)

class Login(BaseModel):
    email: EmailStr
    password: str = Field(..., min_length=6)


class Message(BaseModel):
    sender: str
    content: str
    timestamp: datetime = datetime.utcnow()
    media_url: str | None = None
