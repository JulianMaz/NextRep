package com.example.nextrep.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import com.example.nextrep.models.ExerciseEntity

@Dao
interface ExerciseDao {

    @Query("SELECT * FROM exercises ORDER BY name")
    suspend fun getAllExercises(): List<ExerciseEntity>

    @Insert
    suspend fun insertExercise(exercise: ExerciseEntity): Long

    @Delete
    suspend fun deleteExercise(exercise: ExerciseEntity)
}