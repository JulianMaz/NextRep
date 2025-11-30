package com.example.nextrep.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nextrep.models.Session
import com.example.nextrep.viewmodels.SessionsUiState
import com.example.nextrep.viewmodels.SessionsViewModel

@Composable
fun SessionCreationPage(
    sessionsViewModel: SessionsViewModel,
    uiState: SessionsUiState,                           // 🔹 on lit pendingExercisesForNewSession ici
    onChooseExercises: () -> Unit,                      // 🔹 nav vers la liste des exos (mode sélection)
    onSessionCreated: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf("") }

    val selectedExercises = uiState.pendingExercisesForNewSession   // 🔹 exos choisis pour cette session

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Session name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Date") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        Button(
            onClick = onChooseExercises,                 // 🔹 ouvre ExercisesListPage en mode sélection
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Choisir des exercices")
        }

        if (selectedExercises.isNotEmpty()) {
            Text(
                text = "Exercices sélectionnés (${selectedExercises.size}) :",
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            // 🔹 Liste scrollable qui prend l’espace restant, pas de verticalScroll parent
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)                         // 🔹 utilise l’espace restant de la colonne
            ) {
                items(selectedExercises) { exo ->
                    Text(
                        text = "- ${exo.name}",
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                val newSession = Session(
                    id = 0,                               // 🔹 sera remplacé dans SessionsViewModel
                    name = name,
                    date = date,
                    exercises = selectedExercises        // 🔹 exos effectivement choisis
                )
                sessionsViewModel.addSession(newSession) // 🔹 met à jour uiState.sessions
                onSessionCreated()                       // 🔹 nav vers SessionsListPage
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("Save session")
        }
    }
}