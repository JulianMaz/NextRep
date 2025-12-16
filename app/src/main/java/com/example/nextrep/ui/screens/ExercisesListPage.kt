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


/*
* Pour cette page elle fonctionne sous 2 modes :
* 1- Mode normal : affiche la liste des exercices avec un bouton pour en ajouter
* 2- Mode sélection : permet de sélectionner plusieurs exercices et de valider la sélection
* ce mode est pertinant on va l'utiliser pour choisir des exercices lors de la création d'une session d'entraînement mais aussi lors de l'entrainment sans session
* */

// ici le OptIn est nécessaire pour utiliser Scaffold avec Material3
// Expication de OptIn :  cette annotation permet d'utiliser des API expérimentales de Jetpack Compose Material3 pour bénéficier des dernières fonctionnalités de conception d'interface utilisateur.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisesListPage(
    exercisesViewModel: ExercisesViewModel,
    onAddExercise: () -> Unit,
    onExerciseClick: (Int) -> Unit,
    selectionMode: Boolean = false,

    // callback appelé quand on valide la sélection en mode sélection
    onValidateSelection: (List<Exercise>) -> Unit = {}
) {
    val uiState by exercisesViewModel.uiState.collectAsState()

    var selectedIds by remember { mutableStateOf(setOf<Int>()) }

    Scaffold(
        floatingActionButton = {
            // n'affiche le bouton d'ajout que si on n'est pas en mode sélection
            if (!selectionMode) {
                ExtendedFloatingActionButton(
                    onClick = { onAddExercise() },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add Exercise") },
                    text = { Text(text = "New Exercices") },
                )
            }
        },
        bottomBar = {
            // si on est en mode selection l'affichage change pour permettre à l'utilisateur de choisir et valider ses choix
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

// ===== CONTENU DE LA LISTE D'EXERCICES =====
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

// ici c'est la page en mode sélection elle même que la précédente mais avec des checkbox pour sélectionner les exercices
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
                    text = "No exercises yet. Create an exercise to get started.",
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


//  tres important : onClick doit être passé depuis le parent pour gérer la navigation correctement
// A ne pas oublier ! : l'affichage de l'image doit être lié à l'existence du fichier
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