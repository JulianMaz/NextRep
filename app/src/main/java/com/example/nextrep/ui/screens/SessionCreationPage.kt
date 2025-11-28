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
import com.example.nextrep.data.models.Exercise
import com.example.nextrep.data.models.Session
import com.example.nextrep.viewmodels.SessionsViewModel

@Composable
fun SessionCreationPage(
    sessionsViewModel: SessionsViewModel,
    onSessionCreated: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var exercisesText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Session name field
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Session name") }
        )

        // Date field
        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Date") },
            modifier = Modifier.padding(top = 16.dp)
        )

        // Exercises field (comma-separated names)
        OutlinedTextField(
            value = exercisesText,
            onValueChange = { exercisesText = it },
            label = { Text("Exercises (comma-separated)") },
            modifier = Modifier.padding(top = 16.dp)
        )

        // Save button
        Button(
            onClick = {
                // On convertit le texte en List<Exercise>
                val exercisesList: List<Exercise> =
                    if (exercisesText.isBlank()) {
                        emptyList()
                    } else {
                        exercisesText
                            .split(",")
                            .mapIndexed { index, rawName ->
                                val trimmedName = rawName.trim()
                                Exercise(
                                    id = index,              // provisoire, en attendant une vraie logique d’ID
                                    name = trimmedName,
                                    description = "",
                                    series = 0,
                                    repetitions = 0
                                )
                            }
                    }

                val newSession = Session(
                    name = name,
                    date = date
                )

                sessionsViewModel.addSessionWithExercises(
                    session = newSession,
                    exercises = exercisesList
                )
                onSessionCreated()
            },
            modifier = Modifier.padding(top = 24.dp)
        ) {
            Text("Save")
        }
    }
}