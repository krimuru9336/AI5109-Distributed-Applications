cred = credentials.Certificate('path/to/your/serviceAccountKey.json')  # Replace with actual path
firebase_app = initialize_app(firebase_config, credential=cred)
db = firestore.Client(firebase_app)
storage_bucket = storage.bucket()

def get_db():
    return db

def get_storage():
    return storage_bucket

app.dependency(get_db)
app.dependency(get_storage)
