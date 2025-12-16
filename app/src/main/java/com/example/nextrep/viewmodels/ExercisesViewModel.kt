package com.example.nextrep.viewmodels

import androidx.lifecycle.ViewModel
import com.example.nextrep.models.data.Exercise
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

    private var nextId = 1

    fun addExercise(exercise: Exercise) {
        val newExercise = exercise.copy(id = nextId++)
        val updated = _uiState.value.exercises + newExercise

        _uiState.value = _uiState.value.copy(
            exercises = updated,
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

    fun setExercises(exercises: List<Exercise>) {
        _uiState.value = _uiState.value.copy(
            exercises = exercises
        )
    }

    fun addExerciseLocal(exercise: Exercise) {
        _uiState.value = _uiState.value.copy(
            exercises = _uiState.value.exercises + exercise
        )
    }
}