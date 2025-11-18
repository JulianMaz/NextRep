package com.example.nextrep.viewmodels


import androidx.lifecycle.ViewModel
import com.example.nextrep.models.Exercise
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Represents the UI state for the ExercisesListPage screen.
 * It holds the list of exercises and other UI-related properties.
 */
data class ExercisesUiState(
    val exercises: List<Exercise> = emptyList(),
    val isLoading: Boolean = false

    // You could add other properties here like error messages
)

/**
 * ViewModel for the ExercisesListPage.
 * Its responsibility is to fetch and manage the data for the exercise list.
 */
class ExercisesViewModel : ViewModel() {
    // Private mutable state that can only be modified within this ViewModel
    private val _uiState = MutableStateFlow(ExercisesUiState())
    // Public, read-only state that the UI can observe
    val uiState: StateFlow<ExercisesUiState> = _uiState.asStateFlow()

    // The init block is called when the ViewModel is first created.
    init {
        loadExercises()
    }

    private var nextId = 1
    fun addExercise(exercise: Exercise) {
        val newExercise = exercise.copy(id = nextId++)
        val updatedList = _uiState.value.exercises + newExercise

        _uiState.value = _uiState.value.copy(
            exercises = updatedList
        )
    }

    fun getExerciseById(id: Int): Exercise? {
        return _uiState.value.exercises.find { it.id == id }
    }
    /**
     * Loads the list of exercises.
     * In a real application, this would fetch data from a repository
     * (which would get it from a database or a network source).
     */
    private fun loadExercises() {
        // You could set a loading state here if the data fetch was asynchronous
        // _uiState.value = ExercisesUiState(isLoading = true)

        // --- Example Data for Demonstration ---
        // Replace this with your actual data fetching logic.
        val exampleExercises = listOf(
            Exercise(id = 1, name = "Bench Press", description = "Lay on a bench, lower the bar to your chest, and press it back up.", series = 4, repetitions = 8),
            Exercise(id = 2, name = "Barbell Squat", description = "Stand with a barbell on your shoulders, squat down until your thighs are parallel to the floor.", series = 3, repetitions = 10),
            Exercise(id = 3, name = "Deadlift", description = "Lift a loaded barbell off the floor until you are standing upright.", series = 3, repetitions = 5),
            Exercise(id = 4, name = "Bicep Curl", description = "Curl dumbbells up towards your shoulders, squeezing your biceps.", series = 3, repetitions = 12)
        )

        // Update the UI state with the loaded data
        _uiState.value = ExercisesUiState(exercises = exampleExercises)
    }

    // Future functions to handle user actions can be added here
    fun addExercise(name: String, description: String) {
        // 1. Logic to save the new exercise to your repository/database.
        // 2. Call loadExercises() to refresh the list with the new item.
    }

    fun deleteExercise(exerciseId: Int) {
        // 1. Logic to delete the exercise from your repository/database.
        // 2. Call loadExercises() to refresh the list.
    }
}
