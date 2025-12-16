package com.example.nextrep.models.data

// ce fichier est responsable la data structure de l'app

data class Exercise(
    val id: Int,
    val name: String,
    val description: String,
    val series: Int,
    val repetitions: Int,
    val photoUri: String? = null
)

data class Session(
    val id: Int,
    val name: String,
    val exercises: List<Exercise>,
    val date: String
)