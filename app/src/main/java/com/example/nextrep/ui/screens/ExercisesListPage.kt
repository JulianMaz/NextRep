package com.example.nextrep.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.layout.ContentScale
import com.example.nextrep.models.data.Exercise
import com.example.nextrep.viewmodels.ExercisesViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisesListPage(
    exercisesViewModel: ExercisesViewModel,
    onAddExercise: () -> Unit,
    onExerciseClick: (Int) -> Unit,
    selectionMode: Boolean = false,
    onValidateSelection: (List<Exercise>) -> Unit = {}
) {
    val uiState by exercisesViewModel.uiState.collectAsState()

    var selectedIds by remember { mutableStateOf(setOf<Int>()) }

    Scaffold(
        floatingActionButton = {
            if (!selectionMode) {
                ExtendedFloatingActionButton(
                    onClick = { onAddExercise() },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add Exercise") },
                    text = { Text(text = "New Exercices") },
                )
            }
        },
        bottomBar = {
            if (selectionMode) {
                Button(
                    onClick = {
                        val selected = uiState.exercises
                            .filter { selectedIds.contains(it.id) }
                        onValidateSelection(selected)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Enregistrer la session avec ${selectedIds.size} exercice(s)")
                }
            }
        }
    ) { innerPadding ->
        if (selectionMode) {
            ExerciseListSelectableContent(
                exercises = uiState.exercises,
                selectedIds = selectedIds,
                onToggle = { id, checked ->
                    selectedIds =
                        if (checked) selectedIds + id else selectedIds - id
                },
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            ExerciseListContent(
                exercises = uiState.exercises,
                onExerciseClick = onExerciseClick,
                modifier = Modifier.padding(innerPadding)
            )
        }
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
                    text = "No exercises found. You can create new ones.",
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
                    onClick = { onExerciseClick(exercise.id) }
                )
            }
        }
    }
}

@Composable
fun ExerciseListSelectableContent(
    exercises: List<Exercise>,
    selectedIds: Set<Int>,
    onToggle: (Int, Boolean) -> Unit,
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
                    text = "Aucun exercice. Crée d'abord des exercices.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                )
            }
        } else {
            items(exercises, key = { it.id }) { exercise ->
                SelectableExerciseItem(
                    exercise = exercise,
                    isSelected = selectedIds.contains(exercise.id),
                    onToggle = { checked -> onToggle(exercise.id, checked) }
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
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Affiche la photo si on a un chemin valide
            exercise.photoUri?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    Image(
                        painter = rememberAsyncImagePainter(model = file),
                        contentDescription = "Exercise Image",
                        modifier = Modifier
                            .size(80.dp)
                            .padding(end = 16.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Column {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleMedium
                )
                if (!exercise.description.isNullOrEmpty()) {
                    Text(
                        text = exercise.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun SelectableExerciseItem(
    exercise: Exercise,
    isSelected: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = onToggle
        )
        Text(
            text = exercise.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}