package com.example.nextrep.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextrep.models.Exercise
import com.example.nextrep.viewmodels.ExercisesViewModel

@Composable
fun AllExercisesHistoryPage(
    exercisesViewModel: ExercisesViewModel = viewModel(),
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
                contentPadding = PaddingValues(16.dp)
            ) {
                items(uiState.exercises, key = { it.id }) { exercise ->
                    ExerciseHistoryListItem(
                        exercise = exercise,
                        onClick = { onExerciseClick(exercise.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ExerciseHistoryListItem(
    exercise: Exercise,
    onClick: () -> Unit
) {
    // 🔹 On réutilise le même style qu'une carte simple
    ExerciseItem(
        exercise = exercise,
        onClick = onClick
    )
}