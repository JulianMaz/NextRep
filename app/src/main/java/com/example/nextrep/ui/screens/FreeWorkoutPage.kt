package com.example.nextrep.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.nextrep.models.Exercise
import com.example.nextrep.models.WorkoutSetEntity

// 🔹 Représente l'état d'une ligne de set pour un exercice
data class FreeSetRowState(
    val index: Int,
    val previousKg: String = "-",
    val weightKg: String = "",
    val reps: String = "",
    val isCompleted: Boolean = false
)

/**
 * Écran d'entraînement libre (sans session).
 * On part simplement d'une liste d'exercices sélectionnés.
 */
@Composable
fun FreeWorkoutPage(
    selectedExercises: List<Exercise>,
    onFinishWorkout: (List<WorkoutSetEntity>) -> Unit,
    onAddExercisesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 🔹 Timer en secondes depuis l'ouverture de la page
    var elapsedSeconds by remember { mutableIntStateOf(0) }

    // 🔹 Volume manuel (en kg) saisi par l'utilisateur
    var manualVolumeKg by remember { mutableStateOf("0") }

    // 🔹 Nombre total de sets complétés (toutes les ✓)
    var totalCompletedSets by remember { mutableIntStateOf(0) }

    // 🔹 Map des sets par exercice : exerciseId -> List<FreeSetRowState>
    var exerciseSets by remember { mutableStateOf<Map<Int, List<FreeSetRowState>>>(emptyMap()) }

    // 🔹 Démarrage automatique du timer à l'ouverture
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1_000L)
            elapsedSeconds += 1
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // ===== HEADER LIVE =====
        FreeHeaderLiveSection(
            elapsedSeconds = elapsedSeconds,
            allSetsCompleted = exerciseSets.values
                .flatten()              // -> List<FreeSetRowState>
                .notEmptyOrFalse(),     // -> Boolean
            onFinishWorkout = { val completedSets = buildCompletedWorkoutSets(
                    exercises = selectedExercises,
                    exerciseSets = exerciseSets
                )
                onFinishWorkout(completedSets)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ===== STATS CARD =====
        FreeStatsCard(
            elapsedSeconds = elapsedSeconds,
            manualVolumeKg = manualVolumeKg,
            onManualVolumeChange = { manualVolumeKg = it },
            totalCompletedSets = totalCompletedSets
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ===== LISTE D'EXERCICES =====
        if (selectedExercises.isEmpty()) {
            // 🔹 Aucun exercice : invite à en ajouter
            FreeNoExercisesSection(
                onAddExercisesClick = onAddExercisesClick
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
            ) {
                items(selectedExercises, key = { it.id }) { exercise ->
                    FreeExerciseLiveBlock(
                        exercise = exercise,
                        onSetsChanged = { setsForExercise ->
                            exerciseSets = exerciseSets.toMutableMap().apply {
                                put(exercise.id, setsForExercise)
                            }
                        },
                        onSetCompletedChanged = { completed ->
                            if (completed) {
                                totalCompletedSets += 1
                            } else {
                                totalCompletedSets = (totalCompletedSets - 1).coerceAtLeast(0)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

// -------------------------------------------------------
//  Sous-composants Free Workout
// -------------------------------------------------------

@Composable
private fun FreeHeaderLiveSection(
    elapsedSeconds: Int,
    allSetsCompleted: Boolean,
    onFinishWorkout: () -> Unit
) {
    val formattedTimer = formatDuration(elapsedSeconds)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = formattedTimer,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))

            TextButton(
                onClick = onFinishWorkout,
                enabled = allSetsCompleted, // 🔹 Finish cliquable seulement si tout est coché
                modifier = Modifier
                    .background(
                        color = if (allSetsCompleted)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        shape = MaterialTheme.shapes.large
                    )
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Finish",
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun FreeStatsCard(
    elapsedSeconds: Int,
    manualVolumeKg: String,
    onManualVolumeChange: (String) -> Unit,
    totalCompletedSets: Int
) {
    val formattedDuration = formatDuration(elapsedSeconds)

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FreeStatItem(
                    label = "Duration",
                    value = formattedDuration
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Volume",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedTextField(
                        value = manualVolumeKg,
                        onValueChange = onManualVolumeChange,
                        singleLine = true,
                        modifier = Modifier
                            .width(80.dp)
                            .padding(top = 4.dp),
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "kg",
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                FreeStatItem(
                    label = "Sets",
                    value = totalCompletedSets.toString()
                )
            }
        }
    }
}

@Composable
private fun FreeStatItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun FreeNoExercisesSection(
    onAddExercisesClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No exercises added",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap the button below to start adding exercises to your workout.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onAddExercisesClick,
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text(text = "Add exercises")
        }
    }
}

@Composable
private fun FreeExerciseLiveBlock(
    exercise: Exercise,
    onSetsChanged: (List<FreeSetRowState>) -> Unit,
    onSetCompletedChanged: (Boolean) -> Unit
) {
    var sets by remember {
        mutableStateOf(
            listOf(
                FreeSetRowState(index = 1),
                FreeSetRowState(index = 2),
                FreeSetRowState(index = 3)
            )
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialTheme.shapes.medium
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = exercise.name.take(1).uppercase(),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (exercise.description.isNotBlank()) {
                        Text(
                            text = exercise.description,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("SET", Modifier.weight(0.8f), style = MaterialTheme.typography.labelSmall)
                Text("PREVIOUS", Modifier.weight(1f), style = MaterialTheme.typography.labelSmall)
                Text("KG", Modifier.weight(1f), style = MaterialTheme.typography.labelSmall)
                Text("REPS", Modifier.weight(1f), style = MaterialTheme.typography.labelSmall)
                Text("Done", Modifier.weight(0.8f), style = MaterialTheme.typography.labelSmall)
            }

            Divider(modifier = Modifier.padding(vertical = 4.dp))

            sets.forEach { setRow ->
                FreeSetRow(
                    setRow = setRow,
                    onValueChange = { updated ->
                        sets = sets.map { if (it.index == updated.index) updated else it }
                        onSetsChanged(sets)
                    },
                    onCompletedToggle = { updated, newlyCompleted ->
                        sets = sets.map { if (it.index == updated.index) updated else it }
                        onSetsChanged(sets)
                        onSetCompletedChanged(newlyCompleted)
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = {
                    val nextIndex = (sets.maxOfOrNull { it.index } ?: 0) + 1
                    sets = sets + FreeSetRowState(index = nextIndex)
                    onSetsChanged(sets)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "+ Add set", textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun FreeSetRow(
    setRow: FreeSetRowState,
    onValueChange: (FreeSetRowState) -> Unit,
    onCompletedToggle: (FreeSetRowState, Boolean) -> Unit
) {
    var weight by remember(setRow.index) { mutableStateOf(setRow.weightKg) }
    var reps by remember(setRow.index) { mutableStateOf(setRow.reps) }
    var isCompleted by remember(setRow.index) { mutableStateOf(setRow.isCompleted) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(setRow.index.toString(), Modifier.weight(0.8f))

        Text(setRow.previousKg, Modifier.weight(1f))

        OutlinedTextField(
            value = weight,
            onValueChange = {
                weight = it
                onValueChange(setRow.copy(weightKg = it, reps = reps, isCompleted = isCompleted))
            },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = reps,
            onValueChange = {
                reps = it
                onValueChange(setRow.copy(weightKg = weight, reps = it, isCompleted = isCompleted))
            },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            singleLine = true
        )

        Checkbox(
            checked = isCompleted,
            onCheckedChange = { checked ->
                isCompleted = checked
                onCompletedToggle(
                    setRow.copy(weightKg = weight, reps = reps, isCompleted = checked),
                    checked
                )
            },
            modifier = Modifier.weight(0.8f)
        )
    }
}

/**
 * Construit la liste des WorkoutSetEntity à partir :
 *  - de la liste d'exercices
 *  - de la map exerciseId -> List<FreeSetRowState>
 *
 * On ne garde que les sets cochés avec des valeurs valides.
 */
private fun buildCompletedWorkoutSets(
    exercises: List<Exercise>,
    exerciseSets: Map<Int, List<FreeSetRowState>>
): List<WorkoutSetEntity> {
    val result = mutableListOf<WorkoutSetEntity>()
    val now = System.currentTimeMillis()

    exercises.forEach { exercise ->
        val setsForExercise = exerciseSets[exercise.id] ?: emptyList()

        setsForExercise.forEach { row ->
            if (!row.isCompleted) return@forEach
            if (row.weightKg.isBlank() || row.reps.isBlank()) return@forEach

            val weight = row.weightKg.toFloatOrNull() ?: return@forEach
            val reps = row.reps.toIntOrNull() ?: return@forEach

            result.add(
                WorkoutSetEntity(
                    id = 0L,
                    sessionId = -1,
                    sessionName = "Free workout",
                    sessionDate = "",
                    exerciseId = exercise.id,
                    exerciseName = exercise.name,
                    setIndex = row.index,
                    weightKg = weight,
                    reps = reps,
                    timestamp = now
                )
            )
        }
    }

    return result
}

private fun formatDuration(totalSeconds: Int): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

private fun List<FreeSetRowState>.notEmptyOrFalse(): Boolean =
    this.isNotEmpty() && this.all { it.isCompleted }