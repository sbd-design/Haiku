package com.haiku.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "haikus")
data class HaikuEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val line1: String,
    val line2: String,
    val line3: String,
    val appName: String,
    val packageName: String,
    val originalText: String,
    val timestamp: Long = System.currentTimeMillis()
)
