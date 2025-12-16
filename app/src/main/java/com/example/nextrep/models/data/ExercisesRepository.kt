package com.example.nextrep.models.data

import com.example.nextrep.models.dao.ExerciseDao
import com.example.nextrep.models.entity.toEntity
import com.example.nextrep.models.entity.toModel

class ExercisesRepository(
    private val exerciseDao: ExerciseDao
) {

    // Récupération de tous les exos depuis Room
    suspend fun getAllExercises(): List<Exercise> {
        return exerciseDao.getAllExercises().map { it.toModel() }
    }

    // Ajout d’un exo : on retourne l’exo avec l’ID généré par Room
    suspend fun addExercise(exercise: Exercise): Exercise {
        val newId = exerciseDao.insertExercise(exercise.toEntity()).toInt()
        return exercise.copy(id = newId)
    }

}