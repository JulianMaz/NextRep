package com.example.nextrep.models.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nextrep.models.entity.WorkoutSetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSetDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(sets: List<WorkoutSetEntity>)

    @Query("""
        SELECT * FROM workout_sets
        WHERE exerciseId = :exerciseId
        ORDER BY timestamp DESC, sessionDate DESC, setIndex ASC
    """)
    fun getSetsForExercise(exerciseId: Int): Flow<List<WorkoutSetEntity>>
}