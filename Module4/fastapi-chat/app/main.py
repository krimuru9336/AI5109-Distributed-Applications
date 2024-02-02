from fastapi import FastAPI, Depends, HTTPException
from fastapi.security import OAuth2AuthorizationCodeBearer
from firebase_admin import auth as firebase_auth
from firebase_admin import credentials
from firebase_admin import initialize_app, firestore

app = FastAPI()

# Firebase configuration
cred = credentials.Certificate("path/to/your/firebase/credentials.json")
firebase_app = initialize_app(cred)
firebase_database = firestore.client()

# OAuth2 Authorization Code Bearer for Firebase authentication
oauth2_scheme = OAuth2AuthorizationCodeBearer(tokenUrl="token")

# Function to get the current Firebase user based on the token
async def get_current_user(token: str = Depends(oauth2_scheme)):
    try:
        # Verify Firebase ID token
        decoded_token = firebase_auth.verify_id_token(token)
        uid = decoded_token.get("uid")
        
        # Retrieve user from Firestore based on UID
        user_ref = firebase_database.collection("users").document(uid)
        user_data = user_ref.get().to_dict()

        if user_data:
            return user_data
        else:
            raise HTTPException(status_code=401, detail="Invalid credentials")
    except firebase_auth.ExpiredIdTokenError:
        raise HTTPException(status_code=401, detail="Token has expired")
    except firebase_auth.InvalidIdTokenError:
        raise HTTPException(status_code=401, detail="Invalid token")

# Route for user authentication
@app.post("/login")
async def login(token: str = Depends(oauth2_scheme)):
    return {"token": token, "token_type": "bearer"}

# Route for creating a chat
@app.post("/chats/create")
async def create_chat(current_user: dict = Depends(get_current_user)):
    # Your logic to create a chat in the database
    # You can use current_user data for additional context
    return {"message": "Chat created successfully"}

# Route for editing a chat
@app.put("/chats/edit/{chat_id}")
async def edit_chat(chat_id: str, current_user: dict = Depends(get_current_user)):
    # Your logic to edit a chat in the database
    # You can use chat_id and current_user data for additional context
    return {"message": f"Chat {chat_id} edited successfully"}

# Route for deleting a chat
@app.delete("/chats/delete/{chat_id}")
async def delete_chat(chat_id: str, current_user: dict = Depends(get_current_user)):
    # Your logic to delete a chat in the database
    # You can use chat_id and current_user data for additional context
    return {"message": f"Chat {chat_id} deleted successfully"}
