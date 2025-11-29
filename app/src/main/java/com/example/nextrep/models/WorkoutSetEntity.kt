package com.example.nextrep.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_sets")
data class WorkoutSetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,

    // 🔹 Référence de la session
    val sessionId: Int,
    val sessionName: String,
    val sessionDate: String,

    // 🔹 Référence de l’exercice
    val exerciseId: Int,
    val exerciseName: String,

    // 🔹 Données du set
    val setIndex: Int,         // 1, 2, 3, ...
    val weightKg: Float,
    val reps: Int,

    // 🔹 Quand est-ce que ce set a été fait (timestamp pour trier)
    val timestamp: Long
)