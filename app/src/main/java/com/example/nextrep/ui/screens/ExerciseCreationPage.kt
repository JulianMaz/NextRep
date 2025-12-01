package com.example.nextrep.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextrep.models.Exercise
import com.example.nextrep.models.ExercisesRepository
import com.example.nextrep.viewmodels.ExercisesViewModel
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ExerciseCreationPage(
    exercisesViewModel: ExercisesViewModel = viewModel(),
    exercisesRepository: ExercisesRepository,
    onExerciseCreated: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var series by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

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
            modifier = Modifier.padding(top = 8.dp)
        )

        OutlinedTextField(
            value = series,
            onValueChange = { series = it },
            label = { Text("Series") },
            modifier = Modifier.padding(top = 8.dp)
        )

        OutlinedTextField(
            value = reps,
            onValueChange = { reps = it },
            label = { Text("Repetitions") },
            modifier = Modifier.padding(top = 8.dp)
        )

        Button(
            onClick = {
                // petite validation simple
                if (name.isBlank()) return@Button

                val baseExercise = Exercise(
                    id = 0, // Room va générer l’ID
                    name = name,
                    description = description,
                    series = series.toIntOrNull() ?: 0,
                    repetitions = reps.toIntOrNull() ?: 0,
                    photoUri = null
                )

                scope.launch {
                    // 1️⃣ on persiste dans la base
                    val saved = exercisesRepository.addExercise(baseExercise)

                    // 2️⃣ on met à jour le ViewModel (UI)
                    exercisesViewModel.addExerciseLocal(saved)

                    // 3️⃣ on revient à l’écran précédent (liste)
                    onExerciseCreated()
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Save exercise")
        }
    }
}