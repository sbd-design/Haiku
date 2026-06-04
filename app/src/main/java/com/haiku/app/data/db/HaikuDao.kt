package com.haiku.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HaikuDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(haiku: HaikuEntity): Long

    @Query("SELECT * FROM haikus ORDER BY timestamp DESC")
    fun getAllHaikus(): Flow<List<HaikuEntity>>

    @Query("SELECT * FROM haikus ORDER BY timestamp DESC LIMIT 1")
    fun getLatestHaiku(): Flow<HaikuEntity?>

    @Query("DELETE FROM haikus WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM haikus")
    suspend fun deleteAll()
}
