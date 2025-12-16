package com.example.nextrep.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.nextrep.models.data.WorkoutHistoryRepository
import com.example.nextrep.models.entity.WorkoutSetEntity
import com.example.nextrep.viewmodels.ExercisesViewModel
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Clé représentant UN run d'entraînement pour une session donnée.
 *
 * Même session refaite plusieurs fois -> plusieurs clés différentes
 * grâce au timestamp utilisé lors de l’enregistrement des sets.
 */
data class WorkoutInstanceKey(
    val sessionId: Int,
    val sessionName: String,
    val sessionDate: String,
    val workoutTimestamp: Long
)

@Composable
fun ExerciseHistoryPage(
    exerciseId: Int,
    exercisesViewModel: ExercisesViewModel,
    workoutHistoryRepository: WorkoutHistoryRepository,
    modifier: Modifier = Modifier
) {
    // 🔹 Récupérer les infos de l'exercice (nom, description)
    val exercisesUiState by exercisesViewModel.uiState.collectAsState()
    val exercise = exercisesUiState.exercises.firstOrNull { it.id == exerciseId }

    // 🔹 Récupérer l'historique des sets pour cet exercice,
    // puis regrouper par "run" (sessionId + timestamp)
    val historyFlow = remember(exerciseId) {
        workoutHistoryRepository
            .getHistoryForExercise(exerciseId)
            .map { list ->
                list.groupBy { set ->
                    WorkoutInstanceKey(
                        sessionId = set.sessionId,
                        sessionName = set.sessionName,
                        sessionDate = set.sessionDate,
                        workoutTimestamp = set.timestamp
                    )
                }
            }
    }

    val groupedHistory by historyFlow.collectAsState(initial = emptyMap())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // ===== HEADER EXERCICE =====
        Text(
            text = exercise?.name ?: "Exercise history",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            textAlign = TextAlign.Start
        )

        if (exercise?.description?.isNotBlank() == true) {
            Text(
                text = exercise.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }

        // ===== CONTENU =====
        if (groupedHistory.isEmpty()) {
            Text(
                text = "Aucune séance enregistrée pour cet exercice pour l’instant.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp)
            )
        } else {
            // 🔹 On trie les runs de la plus récente à la plus ancienne
            val sortedEntries = groupedHistory.entries
                .sortedByDescending { it.key.workoutTimestamp }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(
                    items = sortedEntries,
                    key = { entry ->
                        "${entry.key.sessionId}-${entry.key.workoutTimestamp}"
                    }
                ) { entry ->
                    val key = entry.key
                    val sets = entry.value

                    SessionHistoryCard(
                        sessionName = key.sessionName,
                        sessionDate = key.sessionDate,
                        workoutTimestamp = key.workoutTimestamp,
                        sets = sets
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

@Composable
private fun SessionHistoryCard(
    sessionName: String,
    sessionDate: String,
    workoutTimestamp: Long,
    sets: List<WorkoutSetEntity>
) {
    val dateTimeFormatter = remember {
        SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    }
    val dateTimeText = dateTimeFormatter.format(workoutTimestamp)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 🔹 Titre : nom de la session
            Text(
                text = sessionName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            // 🔹 Sous-titre : date de la session + horodatage du run
            Text(
                text = "Session date: $sessionDate – Run: $dateTimeText",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 🔹 Liste des sets de CE run UNIQUEMENT
            sets.sortedBy { it.setIndex }.forEach { set ->
                Text(
                    text = "Set ${set.setIndex}: ${set.weightKg} kg x ${set.reps}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}