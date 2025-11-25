package com.example.nextrep.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.nextrep.models.Exercise
import com.example.nextrep.viewmodels.ExercisesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisesListPage(
    exercisesViewModel: ExercisesViewModel,     // 🔹 ViewModel partagé injecté depuis NextRepApp
    onAddExercise: () -> Unit,
    onExerciseClick: (Int) -> Unit
) {
    val uiState by exercisesViewModel.uiState.collectAsState()   // 🔹 observe la liste des exercices

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddExercise) {
                Icon(Icons.Default.Add, contentDescription = "Add Exercise")
            }
        }
    ) { innerPadding ->
        ExerciseListContent(
            exercises = uiState.exercises,                  // 🔹 liste mise à jour automatiquement
            onExerciseClick = onExerciseClick,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun ExerciseListContent(
    exercises: List<Exercise>,
    onExerciseClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        if (exercises.isEmpty()) {
            item {
                Text(
                    text = "No exercises found.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                )
            }
        } else {
            items(exercises, key = { it.id }) { exercise ->
                ExerciseItem(
                    exercise = exercise,
                    onClick = { onExerciseClick(exercise.id) }   // 🔹 navigation vers un détail plus tard
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseItem(
    exercise: Exercise,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = exercise.name, style = MaterialTheme.typography.titleMedium)
        }
    }
}