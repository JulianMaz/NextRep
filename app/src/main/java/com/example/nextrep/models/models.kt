package com.example.nextrep.models

// ce fichier est responsable la data structure de l'app

data class Exercise(
    val name: String,
    val description: String,
    val series: Int,
    val repetitions: Int,
    val photoUri: String? = null
)

data class Session(
    val name: String,
    val exercises: List<Exercise>,
    val date: String
)