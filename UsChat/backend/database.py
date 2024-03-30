import databases
import sqlalchemy
import enum
import datetime

DATABASE_URL = "mysql+mysqlconnector://root:@localhost:3306/uschatdb"
database = databases.Database(DATABASE_URL)
metadata = sqlalchemy.MetaData()

users = sqlalchemy.Table(
    "users",
    metadata,
    sqlalchemy.Column("user_id", sqlalchemy.Integer, primary_key=True, index=True),
    sqlalchemy.Column("username", sqlalchemy.String(255)),
    sqlalchemy.Column("password", sqlalchemy.String(255)),
)

class MessageType(enum.Enum):
    text = 1
    image = 2
    video = 3
    gif = 4

messages = sqlalchemy.Table(
    "messages",
    metadata,
    sqlalchemy.Column("message_id", sqlalchemy.String(255), primary_key=True, index=True),
    sqlalchemy.Column("sender_id", sqlalchemy.Integer, sqlalchemy.ForeignKey("users.user_id"), index=True),
    sqlalchemy.Column("message_text", sqlalchemy.Text),
    sqlalchemy.Column("message_type", sqlalchemy.Enum(MessageType)),
    sqlalchemy.Column("created_at", sqlalchemy.DateTime, default=datetime.datetime.utcnow),
    sqlalchemy.Column("edited_at", sqlalchemy.DateTime, default=datetime.datetime.utcnow, onupdate=datetime.datetime.utcnow),
    sqlalchemy.Column("attachment", sqlalchemy.Text),
    sqlalchemy.Column("chat_room_id", sqlalchemy.String(255), sqlalchemy.ForeignKey("chat_rooms.chat_room_id")),
)

chat_rooms = sqlalchemy.Table(
    "chat_rooms",
    metadata,
    sqlalchemy.Column("chat_room_id", sqlalchemy.String(255), primary_key=True, index=True),
    sqlalchemy.Column("name", sqlalchemy.String(255)),
    sqlalchemy.Column("description", sqlalchemy.Text),
    sqlalchemy.Column("type", sqlalchemy.String(50)),
    sqlalchemy.Column("last_message_id", sqlalchemy.String(255), sqlalchemy.ForeignKey("messages.message_id")),
)

chat_room_members = sqlalchemy.Table(
    "chat_room_members",
    metadata,
    sqlalchemy.Column("member_id", sqlalchemy.Integer, primary_key=True, index=True),
    sqlalchemy.Column("user_id", sqlalchemy.Integer, sqlalchemy.ForeignKey("users.user_id")),
    sqlalchemy.Column("chat_room_id", sqlalchemy.String(255), sqlalchemy.ForeignKey("chat_rooms.chat_room_id")),
)

engine = sqlalchemy.create_engine(DATABASE_URL)
metadata.create_all(engine)

async def get_db():
    db = databases.Database(DATABASE_URL)
    await db.connect()
    return db
