package com.felixstumpf.distributedapp

import DatabaseHelper
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase

class NamesDataSource(context: Context) {

    private val databaseHelper = DatabaseHelper(context)
    private lateinit var database: SQLiteDatabase

    fun open() {
        database = databaseHelper.writableDatabase
    }

    fun close() {
        databaseHelper.close()
    }

    fun insertName(name: String): Long {
        val values = ContentValues()
        values.put(DatabaseHelper.COLUMN_NAME, name)
        return database.insert(DatabaseHelper.TABLE_NAME, null, values)
    }



    fun getAllNames(): List<NameData> {
        val names = mutableListOf<NameData>()
        val cursor: Cursor = database.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, null)

        cursor.use {
            val idColumnIndex = it.getColumnIndex(DatabaseHelper.COLUMN_ID)
            val nameColumnIndex = it.getColumnIndex(DatabaseHelper.COLUMN_NAME)

            while (it.moveToNext()) {
                if (idColumnIndex != -1 && nameColumnIndex != -1) {
                    val id = it.getLong(idColumnIndex)
                    val name = it.getString(nameColumnIndex)
                    names.add(NameData(id, name))
                } else {
                    // Handle the case where the column index is -1 (column not found)
                    // Log an error or perform appropriate error handling
                }
            }
        }

        return names
    }

    fun deleteName(id: Long): Int {
        return database.delete(
            DatabaseHelper.TABLE_NAME,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }

    fun deleteAllNames(): Int {
        return database.delete(DatabaseHelper.TABLE_NAME, null, null)
    }

}
