package com.example.nextrep.data.session

import androidx.room.*
import com.example.nextrep.data.models.Session
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: Session)

    @Delete
    suspend fun deleteSession(session: Session)

    @Query("SELECT * FROM sessions ORDER BY date DESC")
    fun getSessions(): Flow<List<Session>>
}
