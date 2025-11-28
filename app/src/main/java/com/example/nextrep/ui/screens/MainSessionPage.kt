package com.example.nextrep.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nextrep.models.Exercise
import com.example.nextrep.viewmodels.SessionsViewModel

@Composable
fun MainSessionPage(
    sessionId: Int,                           // 🔹 l'ID de la session à afficher
    sessionsViewModel: SessionsViewModel,     // 🔹 ViewModel partagé des sessions
    onExerciseAdded: () -> Unit,
    onFinishWorkout: () -> Unit
) {
    val uiState by sessionsViewModel.uiState.collectAsState()      // 🔹 on observe les sessions
    val session = uiState.sessions.firstOrNull { it.id == sessionId }

    if (session == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Session not found", style = MaterialTheme.typography.titleLarge)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // 🔹 Infos de base de la session
        Text(
            text = session.name,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Date: ${session.date}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // 🔹 Liste des exercices de cette session
        Text(
            text = "Exercises in this session:",
            style = MaterialTheme.typography.titleMedium
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(top = 8.dp)
        ) {
            if (session.exercises.isEmpty()) {
                item {
                    Text(
                        text = "No exercises selected for this session.",
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            } else {
                items(session.exercises, key = { it.id }) { exercise: Exercise ->
                    Text(
                        text = "- ${exercise.name}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }

        // 🔹 Boutons d'action
        Button(
            onClick = onExerciseAdded,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxSize(fraction = 0.1f)
        ) {
            Text("Add exercise")
        }

        Button(
            onClick = onFinishWorkout,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxSize(fraction = 0.1f)
        ) {
            Text("Finish workout")
        }
    }
}