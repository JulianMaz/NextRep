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
import com.example.nextrep.models.data.Exercise
import com.example.nextrep.models.entity.WorkoutSetEntity
import kotlinx.coroutines.delay

data class FreeSetRowState(
    val index: Int,
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
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    var totalCompletedSets by remember { mutableIntStateOf(0) }
    var exerciseSets by remember { mutableStateOf<Map<Int, List<FreeSetRowState>>>(emptyMap()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1_000L)
            elapsedSeconds += 1
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        FreeHeaderLiveSection(
            elapsedSeconds = elapsedSeconds,
            onFinishWorkout = { val completedSets = buildCompletedWorkoutSets(
                    exercises = selectedExercises,
                    exerciseSets = exerciseSets
                )
                onFinishWorkout(completedSets)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        FreeStatsCard(
            elapsedSeconds = elapsedSeconds,
            totalCompletedSets = totalCompletedSets
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedExercises.isEmpty()) {
            FreeNoExercisesSection(
                onAddExercisesClick = onAddExercisesClick
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
            ) {
                items(selectedExercises, key = { it.id }) { exercise ->
                    val setsForExercise = exerciseSets[exercise.id]
                        ?: initialFreeSetsForExercise(exercise).also { initial ->
                            exerciseSets = exerciseSets.toMutableMap().apply {
                                put(exercise.id, initial)
                            }
                        }

                    FreeExerciseLiveBlock(
                        exercise = exercise,
                        sets = setsForExercise,
                        onSetsChanged = { updatedSets ->
                            exerciseSets = exerciseSets.toMutableMap().apply {
                                put(exercise.id, updatedSets)
                            }
                            totalCompletedSets = exerciseSets.values.flatten().count { it.isCompleted }
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



@Composable
private fun FreeHeaderLiveSection(
    elapsedSeconds: Int,
    onFinishWorkout: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 🔹 Titre fixe
        Text(
            text = "Free Workout",
            style = MaterialTheme.typography.headlineLarge
        )

        // 🔹 Bouton Finish
        TextButton(
            onClick = onFinishWorkout,
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primary,
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
@Composable
private fun FreeStatsCard(
    elapsedSeconds: Int,
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // ⏱ Duration (centré)
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                FreeStatItem(
                    label = "Duration",
                    value = formattedDuration
                )
            }

            // 🧮 Sets (centré)
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
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
    sets: List<FreeSetRowState>,
    onSetsChanged: (List<FreeSetRowState>) -> Unit
) {
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
                Text("SET", Modifier.weight(0.9f), style = MaterialTheme.typography.labelSmall)
                Text("KG", Modifier.weight(1.1f), style = MaterialTheme.typography.labelSmall)
                Text("REPS", Modifier.weight(1.1f), style = MaterialTheme.typography.labelSmall)
                Text("Done", Modifier.weight(0.9f), style = MaterialTheme.typography.labelSmall)
            }

            Divider(modifier = Modifier.padding(vertical = 4.dp))

            sets.forEach { setRow ->
                FreeSetRow(
                    setRow = setRow,
                    onValueChange = { updated ->
                        val newList = sets.map { if (it.index == updated.index) updated else it }
                        onSetsChanged(newList)
                    },
                    onCompletedToggle = { updated ->
                        val newList = sets.map { if (it.index == updated.index) updated else it }
                        onSetsChanged(newList)
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = {
                    val nextIndex = (sets.maxOfOrNull { it.index } ?: 0) + 1
                    val defaultReps = if (exercise.repetitions > 0) exercise.repetitions.toString() else ""
                    val newList = sets + FreeSetRowState(
                        index = nextIndex,
                        weightKg = "10",
                        reps = defaultReps
                    )
                    onSetsChanged(newList)
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
    onCompletedToggle: (FreeSetRowState) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(setRow.index.toString(), Modifier.weight(0.9f))

        OutlinedTextField(
            value = setRow.weightKg,
            onValueChange = { newValue ->
                onValueChange(setRow.copy(weightKg = newValue))
            },
            modifier = Modifier
                .weight(1.1f)
                .padding(horizontal = 4.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = setRow.reps,
            onValueChange = { newValue ->
                onValueChange(setRow.copy(reps = newValue))
            },
            modifier = Modifier
                .weight(1.1f)
                .padding(horizontal = 4.dp),
            singleLine = true
        )

        Checkbox(
            checked = setRow.isCompleted,
            onCheckedChange = { checked ->
                onCompletedToggle(setRow.copy(isCompleted = checked))
            },
            modifier = Modifier.weight(0.9f)
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

private fun initialFreeSetsForExercise(exercise: Exercise): List<FreeSetRowState> {
    val count = exercise.series.takeIf { it > 0 } ?: 3
    val defaultReps = if (exercise.repetitions > 0) exercise.repetitions.toString() else ""

    return (1..count).map { idx ->
        FreeSetRowState(
            index = idx,
            weightKg = "10",
            reps = defaultReps
        )
    }
}