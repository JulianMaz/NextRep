package com.example.nextrep.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_sets")
data class WorkoutSetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,

    val sessionId: Int,
    val sessionName: String,
    val sessionDate: String,

    val exerciseId: Int,
    val exerciseName: String,

    val setIndex: Int,
    val weightKg: Float,
    val reps: Int,

    // quand est-ce que ce set a été fait (pour trier par ordre)
    val timestamp: Long
)