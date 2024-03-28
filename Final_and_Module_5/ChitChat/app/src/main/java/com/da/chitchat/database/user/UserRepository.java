// Sven Schickentanz - fdai7287
package com.da.chitchat.database.user;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.da.chitchat.models.User;

import java.util.UUID;

/**
 * The UserRepository class is responsible for handling the database operations for the User table.
 * It provides methods to save and retrieve user data from the database.
 * It is used to save user data to identify the current user of the application.
 */
public class UserRepository {
    private final UserDbHelper dbHelper;

    /**
     * Constructor for the UserRepository class.
     * 
     * @param context The context of the application.
     */
    public UserRepository(Context context) {
        dbHelper = new UserDbHelper(context);
    }

    /**
     * Saves the user data to the database.
     * 
     * @param id The unique identifier of the user.
     * @param name The username of the user.
     */
    public void saveUser(UUID id, String name) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserContract.UserEntry.COLUMN_NAME_ID, id.toString());
        values.put(UserContract.UserEntry.COLUMN_NAME_USERNAME, name);
        db.insertWithOnConflict(UserContract.UserEntry.TABLE_NAME, null, values,
                SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    /**
     * Retrieves the user data from the database.
     * 
     * @return The user object containing the user data.
     */
    public User getUser() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                UserContract.UserEntry.TABLE_NAME,
                new String[]{UserContract.UserEntry.COLUMN_NAME_ID, UserContract.UserEntry.COLUMN_NAME_USERNAME},
                null,
                null,
                null,
                null,
                null
        );

        User user = null;

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_NAME_ID);
            int usernameIndex = cursor.getColumnIndex(UserContract.UserEntry.COLUMN_NAME_USERNAME);

            if (idIndex >= 0 && usernameIndex >= 0) {
                String id = cursor.getString(idIndex);
                String username = cursor.getString(usernameIndex);
                user = new User(id, username);
            }
        }

        cursor.close();
        db.close();

        return user;
    }
}
