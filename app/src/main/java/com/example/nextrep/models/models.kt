package com.example.nextrep.models

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
    val date: String,
    val exercises: List<String>
)