package com.example.nextrep.models.data

import com.example.nextrep.models.dao.WorkoutSetDao
import com.example.nextrep.models.entity.WorkoutSetEntity
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