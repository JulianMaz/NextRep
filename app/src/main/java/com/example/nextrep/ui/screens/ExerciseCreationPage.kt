package com.example.nextrep.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nextrep.data.models.Exercise
import com.example.nextrep.viewmodels.ExercisesViewModel

@Composable
fun ExerciseCreationPage(
    exercisesViewModel: ExercisesViewModel,   // 🔹 On reçoit le ViewModel partagé depuis NextRepApp
    onExerciseCreated: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var seriesText by remember { mutableStateOf("") }
    var repetitionsText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Exercise name") }
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.padding(top = 16.dp)
        )

        OutlinedTextField(
            value = seriesText,
            onValueChange = { seriesText = it },
            label = { Text("Number of series") },
            modifier = Modifier.padding(top = 16.dp)
        )

        OutlinedTextField(
            value = repetitionsText,
            onValueChange = { repetitionsText = it },
            label = { Text("Repetitions per series") },
            modifier = Modifier.padding(top = 16.dp)
        )

        Button(
            onClick = {
                val series = seriesText.toIntOrNull() ?: 0
                val reps = repetitionsText.toIntOrNull() ?: 0

                val newExercise = Exercise(
                    id = 0,
                    name = name,
                    description = description,
                    series = series,
                    repetitions = reps,
                    photoUri = null
                )

                exercisesViewModel.addExercise(newExercise) // 🔹 Ajout via le ViewModel partagé
                onExerciseCreated()                         // 🔹 Retour vers la liste
            },
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Save Exercise")
        }
    }
}