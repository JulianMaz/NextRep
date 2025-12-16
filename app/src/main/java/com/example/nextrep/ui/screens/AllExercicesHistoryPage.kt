package com.example.nextrep.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextrep.models.data.Exercise
import com.example.nextrep.models.data.WorkoutHistoryRepository
import com.example.nextrep.models.entity.WorkoutSetEntity
import com.example.nextrep.viewmodels.ExercisesViewModel
import kotlinx.coroutines.flow.map

@Composable
fun AllExercisesHistoryPage(
    exercisesViewModel: ExercisesViewModel = viewModel(),
    workoutHistoryRepository: WorkoutHistoryRepository,
    onExerciseClick: (Int) -> Unit
) {
    val uiState by exercisesViewModel.uiState.collectAsState()

    Scaffold { innerPadding ->
        if (uiState.exercises.isEmpty()) {
            Text(
                text = "Aucun exercice pour l’instant.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.exercises, key = { it.id }) { exercise ->
                    ExerciseHistoryPreviewItem(
                        exercise = exercise,
                        workoutHistoryRepository = workoutHistoryRepository,
                        onHistoryClick = { onExerciseClick(exercise.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ExerciseHistoryPreviewItem(
    exercise: Exercise,
    workoutHistoryRepository: WorkoutHistoryRepository,
    onHistoryClick: () -> Unit
) {
    // 🔹 On récupère tous les sets pour cet exercice
    val historyFlow = remember(exercise.id) {
        workoutHistoryRepository
            .getHistoryForExercise(exercise.id)
            // On groupe par "run" d'entraînement (timestamp commun à un même run)
            .map { list ->
                list.groupBy { it.timestamp }
            }
    }

    val groupedByRun by historyFlow.collectAsState(initial = emptyMap<Long, List<WorkoutSetEntity>>())

    // 🔹 On prend le dernier run (timestamp le plus récent)
    val lastRunSets: List<WorkoutSetEntity> = remember(groupedByRun) {
        if (groupedByRun.isEmpty()) {
            emptyList()
        } else {
            val lastTimestamp = groupedByRun.keys.maxOrNull()
            val sets = groupedByRun[lastTimestamp] ?: emptyList()
            sets.sortedBy { it.setIndex }
        }
    }

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
                .padding(12.dp)
        ) {
            // ----- En-tête : nom de l'exercice + bouton flèche -----
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onHistoryClick) {
                    Icon(
                        imageVector = Icons.Filled.ArrowForward,
                        contentDescription = "Voir tout l’historique"
                    )
                }
            }

            Spacer(modifier = Modifier.padding(top = 4.dp))

            // ----- Contenu : dernière séance (preview) -----
            if (lastRunSets.isEmpty()) {
                Text(
                    text = "Aucune donnée encore pour cet exercice.",
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                Text(
                    text = "Dernière séance :",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                lastRunSets.forEach { set ->
                    Text(
                        text = "Set ${set.setIndex}: ${set.weightKg} kg x ${set.reps}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}