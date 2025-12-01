package com.example.nextrep.viewmodels


import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel

// 🔹 1 set d’un exercice dans le workout live
data class WorkoutSetState(
    val index: Int,             // 1, 2, 3…
    val weightKg: String = "",
    val reps: String = "",
    val done: Boolean = false
)

// 🔹 1 exercice dans le workout live
data class WorkoutExerciseState(
    val id: Int,
    val name: String,
    val sets: SnapshotStateList<WorkoutSetState>
)

class WorkoutViewModel : ViewModel() {

    // 🔹 liste observable d’exercices dans la séance en cours
    private val _exercises = mutableStateListOf<WorkoutExerciseState>()
    val exercises: List<WorkoutExerciseState> get() = _exercises

    // Appelée quand tu démarres une séance : tu transformes ta Session en état editable
    fun initFromSession(
        sessionId: Int,
        exerciseNames: List<String>      // pour l’instant, juste les noms
    ) {
        if (_exercises.isNotEmpty()) return  // éviter de réinitialiser si déjà remplie

        exerciseNames.forEachIndexed { idx, name ->
            val sets = mutableStateListOf<WorkoutSetState>()
            // Exemple: 3 sets par exercice au départ
            repeat(3) { setIndex ->
                sets.add(
                    WorkoutSetState(
                        index = setIndex + 1
                    )
                )
            }
            _exercises.add(
                WorkoutExerciseState(
                    id = idx,           // plus tard tu pourras mettre l’ID réel de l’exo
                    name = name,
                    sets = sets
                )
            )
        }
    }

    // 🔹 Mises à jour : ce sont ces fonctions que les TextField / Checkbox vont appeler

    fun updateWeight(exerciseIndex: Int, setIndex: Int, value: String) {
        val ex = _exercises[exerciseIndex]
        val oldSet = ex.sets[setIndex]
        ex.sets[setIndex] = oldSet.copy(weightKg = value)
    }

    fun updateReps(exerciseIndex: Int, setIndex: Int, value: String) {
        val ex = _exercises[exerciseIndex]
        val oldSet = ex.sets[setIndex]
        ex.sets[setIndex] = oldSet.copy(reps = value)
    }

    fun updateDone(exerciseIndex: Int, setIndex: Int, value: Boolean) {
        val ex = _exercises[exerciseIndex]
        val oldSet = ex.sets[setIndex]
        ex.sets[setIndex] = oldSet.copy(done = value)
    }
}