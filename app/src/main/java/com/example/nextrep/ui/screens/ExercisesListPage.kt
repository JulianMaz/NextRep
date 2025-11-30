package com.example.nextrep.ui.screens

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
import com.example.nextrep.models.Exercise
import com.example.nextrep.viewmodels.ExercisesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisesListPage(
    exercisesViewModel: ExercisesViewModel,
    onAddExercise: () -> Unit,
    onExerciseClick: (Int) -> Unit,
    selectionMode: Boolean = false,                          // 🔹 mode sélection pour créer une session
    onValidateSelection: (List<Exercise>) -> Unit = {}       // 🔹 callback utilisé uniquement en mode sélection
) {
    val uiState by exercisesViewModel.uiState.collectAsState()

    var selectedIds by remember { mutableStateOf(setOf<Int>()) }  // 🔹 ids cochés

    Scaffold(
        floatingActionButton = {
            if (!selectionMode) {                             // 🔹 on cache le FAB en mode sélection
                ExtendedFloatingActionButton(
                    onClick = { onAddExercise},
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add Exercise")},
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
                        onValidateSelection(selected)        // 🔹 renvoie la sélection à l’appelant
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
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = exercise.name, style = MaterialTheme.typography.titleMedium)
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
            .padding(vertical = 4.dp)
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