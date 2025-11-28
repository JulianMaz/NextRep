package com.example.nextrep.data.session

import androidx.room.*
import com.example.nextrep.data.models.Session
import com.example.nextrep.data.models.SessionExerciseCrossRef
import com.example.nextrep.data.models.SessionWithExercises
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: Session): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRef(crossRef: SessionExerciseCrossRef)

    @Transaction
    @Query("SELECT * FROM sessions WHERE id = :sessionId")
    fun getSessionWithExercises(sessionId: Int): Flow<SessionWithExercises>

    @Query("SELECT * FROM sessions ORDER BY id ASC")
    fun getSessions(): Flow<List<Session>>

    @Transaction
    @Query("SELECT * FROM sessions ORDER BY id ASC")
    fun getAllSessionsWithExercises(): Flow<List<SessionWithExercises>>

    @Delete
    suspend fun deleteSession(session: Session)
}
