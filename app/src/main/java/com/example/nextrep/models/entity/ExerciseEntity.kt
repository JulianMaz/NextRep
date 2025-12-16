package com.example.nextrep.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.nextrep.models.data.Exercise

// 🔹 Entity Room pour la table des exercices
@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val series: Int,
    val repetitions: Int,
    val photoUri: String? // Add this field
)

// ---------- Mappers vers ton modèle UI existant ----------

// 🔹 Entity -> modèle de l’app
fun ExerciseEntity.toModel(): Exercise =
    Exercise(
        id = id,
        name = name,
        description = description,
        series = series,
        repetitions = repetitions,
        photoUri = photoUri
    )

// 🔹 Modèle de l’app -> Entity
fun Exercise.toEntity(): ExerciseEntity =
    ExerciseEntity(
        id = if (id == 0) 0 else id,    // 0 → laisse Room générer, sinon on garde
        name = name,
        description = description,
        series = series,
        repetitions = repetitions,
        photoUri = photoUri
    )