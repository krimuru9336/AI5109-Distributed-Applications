package com.example.chitchat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

public class DbConnection extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "User";

    public static  final Map<String,String[]> DATABASE_TABLES = new HashMap<String,String[]>();
    private static final String TABLE_USERS = "user";
    public static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_ID = "id";
    public static final String COLUMN_PASSWORD = "password";


    private static final String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS +
            "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_EMAIL + " TEXT, " +
            COLUMN_PASSWORD + " TEXT)";

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_USERS_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public DbConnection(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        DATABASE_TABLES.put("user",new String[]{"id","email","password"});
        DATABASE_TABLES.put("user",new String[]{"id","email","password"});
    }
//
//    public Cursor getUserByEmail(String email) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        String[] projection = {COLUMN_ID, COLUMN_EMAIL, COLUMN_PASSWORD};
//        String selection = COLUMN_EMAIL + "=?";
//        String[] selectionArgs = {email};
//        return db.query(TABLE_USERS, projection, selection, selectionArgs, null, null, null);
//    }
//    public long insertUser(String email, String password) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_EMAIL, email);
//        values.put(COLUMN_PASSWORD, password);
//        long id = db.insert(TABLE_USERS, null, values);
//        db.close();
//        return id;
//    }
//
//
//    public Cursor getAllUsers() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        return db.query(TABLE_USERS, null, null, null, null, null, null);
//    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if exists and create a new one
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

}
