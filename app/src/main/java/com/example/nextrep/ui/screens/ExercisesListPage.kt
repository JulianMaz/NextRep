package com.example.nextrep.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextrep.models.Exercise
import com.example.nextrep.viewmodels.ExercisesViewModel

@Composable
fun ExercisesListPage(
    // These are the correct parameters that match the NavHost calls
    exercisesViewModel: ExercisesViewModel = viewModel(),
    onAddExercise: () -> Unit,
    onExerciseClick: (Int) -> Unit
) {
    // The UI observes the state from the ViewModel.
    // Any changes to the state will automatically trigger a recomposition.
    val uiState by exercisesViewModel.uiState.collectAsState()

    // The Scaffold is in NextRepScreen.kt, so we only need the content here.
    // We pass onAddExercise down to the content, perhaps for a button.
    ExerciseListContent(
        exercises = uiState.exercises,
        onExerciseClick = onExerciseClick
    )
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
                    text = "No exercises found. You can add them from the session screen or create new ones.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                )
            }
        } else {
            // The key helps Compose efficiently update the list
            items(exercises, key = { it.id }) { exercise ->
                ExerciseItem(exercise = exercise, onClick = { onExerciseClick(exercise.id) })
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

// --- Previews ---

@Preview(showBackground = true, name = "Exercises List With Items")
@Composable
fun ExerciseListContentPreview() {
    val previewExercises = listOf(
        Exercise(id = 1, name = "Bench Press", description = "Description for Bench Press", series = 3, repetitions = 12),
        Exercise(id = 2, name = "Squat", description = "Description for Squat", series = 3, repetitions = 12),
        Exercise(id = 3, name = "Bicep Curl", description = "Description for Biceps Curl", series = 3, repetitions = 12)
    )
    MaterialTheme {
        ExerciseListContent(
            exercises = previewExercises,
            onExerciseClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Exercises List Empty")
@Composable
fun ExerciseListContentEmptyPreview() {
    MaterialTheme {
        ExerciseListContent(
            exercises = emptyList(),
            onExerciseClick = {}
        )
    }
}
