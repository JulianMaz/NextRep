package com.example.nextrep.viewmodels

import androidx.lifecycle.ViewModel
import com.example.nextrep.models.Exercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ExercisesUiState(
    val exercises: List<Exercise> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ExercisesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ExercisesUiState())
    val uiState: StateFlow<ExercisesUiState> = _uiState.asStateFlow()

    private var nextId = 1   // 🔹 ID auto-incrémenté pour les exercices créés

    fun addExercise(exercise: Exercise) {
        val newExercise = exercise.copy(id = nextId++)   // 🔹 Ici on assigne un ID unique
        val updated = _uiState.value.exercises + newExercise

        _uiState.value = _uiState.value.copy(
            exercises = updated,                         // 🔹 Mise à jour de la liste
            errorMessage = null
        )
    }

    fun deleteExercise(exerciseId: Int) {
        val updated = _uiState.value.exercises.filterNot { it.id == exerciseId }
        _uiState.value = _uiState.value.copy(exercises = updated)
    }

    fun getExerciseById(id: Int): Exercise? {
        return _uiState.value.exercises.firstOrNull { it.id == id }
    }
}