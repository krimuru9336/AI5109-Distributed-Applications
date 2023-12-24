package com.example.damodule

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $TABLE_NAME (ID INTEGER PRIMARY KEY AUTOINCREMENT, TEXT_VALUE TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertData(textValue: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_2, textValue)
        db.insert(TABLE_NAME, null, contentValues)
    }

    fun getData(): String {
        val db = this.readableDatabase
        val res = db.rawQuery("select * from $TABLE_NAME", null)
        if (res.moveToLast()) {
            return res.getString(1)
        }
        return ""
    }

    companion object {
        const val DATABASE_NAME = "Data.db"
        const val TABLE_NAME = "data_table"
        const val COL_1 = "ID"
        const val COL_2 = "TEXT_VALUE"
        const val DATABASE_VERSION = 1
    }
}
