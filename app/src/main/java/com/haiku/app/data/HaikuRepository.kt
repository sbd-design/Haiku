package com.haiku.app.data

import com.haiku.app.data.db.HaikuDao
import com.haiku.app.data.db.HaikuEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HaikuRepository @Inject constructor(
    private val dao: HaikuDao
) {
    val allHaikus: Flow<List<HaikuEntity>> = dao.getAllHaikus()
    val latestHaiku: Flow<HaikuEntity?> = dao.getLatestHaiku()

    suspend fun saveHaiku(haiku: HaikuEntity): Long = dao.insert(haiku)

    suspend fun deleteHaiku(id: Long) = dao.deleteById(id)

    suspend fun clearAll() = dao.deleteAll()
}
