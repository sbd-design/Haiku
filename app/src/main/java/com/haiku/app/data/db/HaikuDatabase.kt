package com.haiku.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [HaikuEntity::class], version = 1, exportSchema = false)
abstract class HaikuDatabase : RoomDatabase() {
    abstract fun haikuDao(): HaikuDao
}
