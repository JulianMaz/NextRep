package com.example.nextrep.models

import com.example.nextrep.data.WorkoutSetDao
import kotlinx.coroutines.flow.Flow

class WorkoutHistoryRepository(
    private val workoutSetDao: WorkoutSetDao
) {

    suspend fun saveWorkoutSets(sets: List<WorkoutSetEntity>) {
        workoutSetDao.insertAll(sets)
    }

    fun getHistoryForExercise(exerciseId: Int): Flow<List<WorkoutSetEntity>> {
        return workoutSetDao.getSetsForExercise(exerciseId)
    }
}