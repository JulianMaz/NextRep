package com.example.nextrep.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextrep.data.exercise.ExerciseDao
import com.example.nextrep.data.models.Exercise
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ExercisesUiState(
    val exercises: List<Exercise> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ExercisesViewModel(private val exerciseDao: ExerciseDao) : ViewModel() {

    val uiState: StateFlow<ExercisesUiState> =
        exerciseDao.getAllExercises().map { exercises ->
            ExercisesUiState(exercises = exercises, isLoading = false)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ExercisesUiState(isLoading = true)
        )

    fun addExercise(exercise: Exercise) {
        viewModelScope.launch {
            exerciseDao.insertExercise(exercise)
        }
    }

    fun deleteExercise(id: Int) {
        viewModelScope.launch {
            exerciseDao.deleteExercise(id)
        }
    }
}
