package com.da.chitchat.database.user;

import android.provider.BaseColumns;

public class UserContract {
    private UserContract() {}

    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "user_entry";
        public static final String COLUMN_NAME_ID = "uuid";
        public static final String COLUMN_NAME_USERNAME = "username";
    }
}
