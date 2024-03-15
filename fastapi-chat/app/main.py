from fastapi import FastAPI, Depends, HTTPException
from fastapi.security import OAuth2AuthorizationCodeBearer
from firebase_admin import auth as firebase_auth
from firebase_admin import credentials
from firebase_admin import initialize_app, firestore
from multipart.operations import MultipartParser
from jose import jwt
from pydantic import ValidationError

app = FastAPI()

# Firebase configuration
cred = credentials.Certificate("path/to/serviceAccountKey.json")
firebase_app = initialize_app(cred)
firebase_database = firestore.client()

SECRET_KEY = "something_random_so_no_one_can_guess_this_secret"
ALGORITHM = "HS256"  

async def verify_jwt(token: str = Depends(fastapi.Query(..., description="JWT token"))):
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        return payload
    except (jwt.JWTError, ValidationError) as e:
        raise HTTPException(status_code=401, detail=f"Invalid or expired JWT: {e}")


oauth2_scheme = OAuth2AuthorizationCodeBearer(tokenUrl="token")


async def get_current_user(token: str = Depends(oauth2_scheme)):
    try:
        
        decoded_token = firebase_auth.verify_id_token(token)
        uid = decoded_token.get("uid")
        
        
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

async def create_user(user: User):
    try:
        user_record = await auth.create_user(
            email=user.email,
            password=user.password,
        )
        return {'message': 'User created successfully'}
    except firebase_auth.EmailExistsError:
        return {'error': 'Email already exists'}
    except Exception as e:
        return {'error': str(e)}  


@app.post("/login")
async def sign_in_user(login: Login):
    try:
        user = await auth.sign_in_with_email_and_password(login.email, login.password)
        
        return {'token': 'access_token'}  
    except firebase_auth.InvalidEmailError:
        return {'error': 'Invalid email format'}
    except firebase_auth.FirebaseAuthError as e:
        return {'error': str(e)}

@app.post("/logout")
async def sign_out_user(token: str = Depends(verify_jwt)):
  
    return {'message': 'Logged out successfully'}


@app.post('/chats/{recipient_email}/create-message')
async def create_message(
    recipient_email: str, message: Message, token: str = Depends(verify_jwt)
):
    
    sender_email = get_current_user_email(token)  
    if sender_email is None:
        return {'error': 'Unauthorized access'}

    chat_ref = chats_col.document(f'{sender_email}-{recipient_email}')
    chat_ref.set({'messages': []}, merge=True)  
    chat_ref.collection('messages').document().set(message.dict())
    return {'message': 'Message sent successfully'}

@app.post("/chats/create")
async def create_chat(current_user: dict = Depends(get_current_user)):
    
    return {"message": "Chat created successfully"}


@app.put("/chats/edit/{chat_id}")
async def edit_chat(chat_id: str, current_user: dict = Depends(get_current_user)):
    return {"message": f"Chat {chat_id} edited successfully"}


@app.delete("/chats/delete/{chat_id}")
async def delete_chat(chat_id: str, current_user: dict = Depends(get_current_user)):
    return {"message": f"Chat {chat_id} deleted successfully"}



group_chats_col = db.collection('group_chats')

@app.post('/group-chats/{group_id}/create-message')
async def create_group_message(
    group_id: str, message: Message, token: str = Depends(verify_jwt)
):
    # Validate access using the token (implementation not included)
    sender_email = get_current_user_email(token)  # Replace with token validation logic
    if sender_email is None:
        return {'error': 'Unauthorized access'}

    group_chat_ref = group_chats_col.document(group_id)
    group_chat_ref.set({'messages': []}, merge=True)  # Create group chat if it doesn't exist
    group_chat_ref.collection('messages').document().set(message.dict())
    return {'message': 'Message sent successfully'}


@app.post('/upload-media')
async def upload_media(file: UploadFile = File(verify_jwt)):
    filename = file.filename
    content_type = file.content_type
    blob = storage.Blob(name=filename, content_type=content_type, bucket=get_storage())
    await blob.upload(MultipartParser(file.file))
    media_url = f'https://firebasestorage.googleapis.com/v0/b/{get_storage().name}/{blob.name}'
