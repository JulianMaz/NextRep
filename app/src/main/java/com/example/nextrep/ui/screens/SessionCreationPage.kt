package com.example.nextrep.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextrep.models.Session
import com.example.nextrep.viewmodels.SessionsViewModel

@Composable
fun SessionCreationPage(
    onSessionCreated: () -> Unit,
    sessionsViewModel: SessionsViewModel = viewModel()
) {
    val sessionName = remember { mutableStateOf("") }
    val sessionDate = remember { mutableStateOf("") }
    val exercises = remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = sessionName.value,
            onValueChange = { sessionName.value = it },
            label = { Text("Session Name") }
        )

        OutlinedTextField(
            value = sessionDate.value,
            onValueChange = { sessionDate.value = it },
            label = { Text("Session Date") }
        )

        OutlinedTextField(
            value = exercises.value,
            onValueChange = { exercises.value = it },
            label = { Text("Exercises (comma‑separated)") }
        )
        Text("Create a New Session", style = MaterialTheme.typography.headlineMedium)
        Button(
            onClick = {
                val session = Session(
                    id = 0,
                    name = sessionName.value,
                    date = sessionDate.value,
                    exercises = exercises.value.split(",").map { it.trim() }
                        .filter { it.isNotEmpty() }
                )
                sessionsViewModel.addSession(session)
                onSessionCreated()
            }
        ) {
            Text("Save Session")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SessionCreationPagePreview() {
    MaterialTheme {
        SessionCreationPage(onSessionCreated = {})
    }
}
