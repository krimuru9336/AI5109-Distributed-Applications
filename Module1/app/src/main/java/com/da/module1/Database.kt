package com.da.module1

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Upsert

@Entity
data class Name(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)

@Dao
interface NameDao {
    @Upsert
    suspend fun storeName(name: Name)
}

@Database(entities = [Name::class], version = 1)
abstract class NameDatabase : RoomDatabase() {
    abstract val dao: NameDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: NameDatabase? = null

        fun getDatabase(context: Context): NameDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NameDatabase::class.java,
                    "names.db"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}