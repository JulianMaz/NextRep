package com.example.nextrep.viewmodels


import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel

data class WorkoutSetState(
    val index: Int,             // 1, 2, 3…
    val weightKg: String = "",
    val reps: String = "",
    val done: Boolean = false
)

data class WorkoutExerciseState(
    val id: Int,
    val name: String,
    val sets: SnapshotStateList<WorkoutSetState>
)

class WorkoutViewModel : ViewModel() {

    private val _exercises = mutableStateListOf<WorkoutExerciseState>()
    val exercises: List<WorkoutExerciseState> get() = _exercises

}