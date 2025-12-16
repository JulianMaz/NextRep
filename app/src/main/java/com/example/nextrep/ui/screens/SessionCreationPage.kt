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
import com.example.nextrep.models.data.Session
import com.example.nextrep.viewmodels.SessionsUiState
import com.example.nextrep.viewmodels.SessionsViewModel

/**
 * Écran de création d'une session d'entraînement.
 *
 * Cet écran permet à l'utilisateur :
 * - de saisir les informations de base d'une session,
 * - de sélectionner une liste d'exercices existants,
 * - de sauvegarder la session complète.
 *
 * La logique métier (création et stockage de la session) est déléguée
 * au [SessionsViewModel], conformément à l'architecture MVVM.
 *
 * @param sessionsViewModel ViewModel responsable de la gestion des sessions.
 * @param uiState État UI contenant notamment les exercices sélectionnés
 * @param onChooseExercises Callback déclenchant la navigation vers
 * l'écran de sélection des exercices.
 * @param onSessionCreated Callback appelé après la création réussie
 * de la session (navigation retour).
 */
@Composable
fun SessionCreationPage(
    sessionsViewModel: SessionsViewModel,
    uiState: SessionsUiState,
    onChooseExercises: () -> Unit,
    onSessionCreated: () -> Unit
) {
    // Champs persistants (survivent aux recompositions)
    var name by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf("") }

    // Liste temporaire des exercices sélectionnés pour la session
    val selectedExercises = uiState.pendingExercisesForNewSession

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
            onClick = onChooseExercises,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Choisir des exercices")
        }

        if (selectedExercises.isNotEmpty()) {
            Text(
                text = "Exercices sélectionnés (${selectedExercises.size}) :",
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
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
                    id = 0,
                    name = name,
                    date = date,
                    exercises = selectedExercises
                )
                sessionsViewModel.addSession(newSession)
                onSessionCreated()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("Save session")
        }
    }
}