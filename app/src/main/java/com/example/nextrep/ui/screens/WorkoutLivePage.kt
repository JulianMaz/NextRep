package com.example.nextrep.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextrep.models.Exercise
import com.example.nextrep.models.Session
import com.example.nextrep.models.WorkoutSetEntity
import com.example.nextrep.viewmodels.SessionsViewModel
import com.example.nextrep.viewmodels.WorkoutExerciseState
import com.example.nextrep.viewmodels.WorkoutSetState
import com.example.nextrep.viewmodels.WorkoutViewModel
import kotlinx.coroutines.delay

/**
 * Écran d'entraînement en direct (WorkoutLivePage).
 *
 * @param sessionId           ID de la session que l'on est en train de faire
 * @param sessionsViewModel   ViewModel des sessions (pour récupérer la session et ses exercices)
 * @param workoutViewModel    ViewModel qui contient l'état live des sets
 * @param onFinishWorkout     Callback appelé quand l'utilisateur termine la séance,
 *                            avec la liste des WorkoutSetEntity complétés.
 * @param onAddExercisesClick Callback pour ouvrir ExercisesListPage en mode sélection
 */
@Composable
fun WorkoutLivePage(
    sessionId: Int,
    sessionsViewModel: SessionsViewModel,
    workoutViewModel: WorkoutViewModel = viewModel(),
    onFinishWorkout: (List<WorkoutSetEntity>) -> Unit,
    onAddExercisesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 🔹 Récupère la session en cours
    val session = sessionsViewModel.getSessionById(sessionId)

    // 🔹 Timer en secondes depuis l'ouverture de la page
    var elapsedSeconds by remember { mutableIntStateOf(0) }

    // 🔹 Volume manuel (en kg) saisi par l'utilisateur
    var manualVolumeKg by remember { mutableStateOf("0") }

    // 🔹 Démarrage automatique du timer à l'ouverture
    LaunchedEffect(Unit) {
        while (true) {
            delay(1_000L)
            elapsedSeconds += 1
        }
    }

    // 🔹 Cas où la session ne serait pas trouvée (sécurité)
    if (session == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Session introuvable.",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    // 🔹 Initialise le ViewModel à partir de la session (une seule fois par sessionId)
    LaunchedEffect(session.id) {
        workoutViewModel.initFromSession(
            sessionId = session.id,
            exerciseNames = session.exercises.map { it.name }
        )
    }

    // 🔹 État live des exercices / sets
    val workoutExercises = workoutViewModel.exercises

    // 🔹 Nombre total de sets complétés (dérivé de l'état du ViewModel)
    val totalCompletedSets = workoutExercises.sumOf { ex ->
        ex.sets.count { it.done }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // ===== HEADER LIVE =====
        HeaderLiveSection(
            elapsedSeconds = elapsedSeconds,
            onFinishWorkout = {
                val completedSets = buildCompletedWorkoutSets(
                    session = session,
                    workoutExercises = workoutExercises
                )
                onFinishWorkout(completedSets)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ===== STATS CARD =====
        StatsCard(
            elapsedSeconds = elapsedSeconds,
            manualVolumeKg = manualVolumeKg,
            onManualVolumeChange = { manualVolumeKg = it },
            totalCompletedSets = totalCompletedSets
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ===== LISTE D'EXERCICES =====
        if (session.exercises.isEmpty()) {
            // 🔹 Cas "No exercises added"
            NoExercisesSection(
                onAddExercisesClick = onAddExercisesClick
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                // On parcourt les états du ViewModel et on matche avec la session par index
                itemsIndexed(workoutExercises) { index, exerciseState ->
                    val exercise = session.exercises.getOrNull(index)
                    if (exercise != null) {
                        ExerciseLiveBlock(
                            exercise = exercise,
                            exerciseState = exerciseState
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

// -------------------------------------------
//  Sous-composants de WorkoutLivePage
// -------------------------------------------

@Composable
private fun HeaderLiveSection(
    elapsedSeconds: Int,
    onFinishWorkout: () -> Unit
) {
    val formattedTimer = formatDuration(elapsedSeconds)

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 🔹 Timer centré en haut
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
}

@Composable
private fun StatsCard(
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
                // 🔹 Duration
                StatItem(
                    label = "Duration",
                    value = formattedDuration
                )

                // 🔹 Volume (manuel, en kg)
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

                // 🔹 Sets complétés
                StatItem(
                    label = "Sets",
                    value = totalCompletedSets.toString()
                )
            }
        }
    }
}

@Composable
private fun StatItem(
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
private fun NoExercisesSection(
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
private fun ExerciseLiveBlock(
    exercise: Exercise,
    exerciseState: WorkoutExerciseState
) {
    val sets = exerciseState.sets    // SnapshotStateList<WorkoutSetState>

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
            // ----- En-tête exercice (image + nom) -----
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

            // ----- Table des sets -----
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

            // Lignes de sets
            sets.forEachIndexed { setIndex, setState ->
                SetRow(
                    set = setState,
                    onWeightChange = { newWeight ->
                        sets[setIndex] = setState.copy(weightKg = newWeight)
                    },
                    onRepsChange = { newReps ->
                        sets[setIndex] = setState.copy(reps = newReps)
                    },
                    onDoneChange = { checked ->
                        sets[setIndex] = setState.copy(done = checked)
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ----- Bouton Add set -----
            TextButton(
                onClick = {
                    val nextIndex = (sets.maxOfOrNull { it.index } ?: 0) + 1
                    sets.add(
                        WorkoutSetState(
                            index = nextIndex
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "+ Add set", textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun SetRow(
    set: WorkoutSetState,
    onWeightChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
    onDoneChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(set.index.toString(), Modifier.weight(0.8f))

        // 🔹 Pour l’instant, pas de "previous" réel
        Text("-", Modifier.weight(1f))

        OutlinedTextField(
            value = set.weightKg,
            onValueChange = onWeightChange,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = set.reps,
            onValueChange = onRepsChange,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            singleLine = true
        )

        Checkbox(
            checked = set.done,
            onCheckedChange = onDoneChange,
            modifier = Modifier.weight(0.8f)
        )
    }
}

/**
 * Construit la liste des WorkoutSetEntity à partir :
 *  - de la session (id / nom / date / exercices)
 *  - de la liste d'états WorkoutExerciseState du ViewModel
 *
 * On ne garde que :
 *  - les sets cochés (done == true)
 *  - avec un weightKg et reps parsables.
 */
private fun buildCompletedWorkoutSets(
    session: Session,
    workoutExercises: List<WorkoutExerciseState>
): List<WorkoutSetEntity> {
    val result = mutableListOf<WorkoutSetEntity>()
    val now = System.currentTimeMillis()

    workoutExercises.forEachIndexed { index, exState ->
        val exercise = session.exercises.getOrNull(index) ?: return@forEachIndexed

        exState.sets.forEach { set ->
            if (!set.done) return@forEach
            if (set.weightKg.isBlank() || set.reps.isBlank()) return@forEach

            val weight = set.weightKg.toFloatOrNull() ?: return@forEach
            val reps = set.reps.toIntOrNull() ?: return@forEach

            result.add(
                WorkoutSetEntity(
                    id = 0L,
                    sessionId = session.id,
                    sessionName = session.name,
                    sessionDate = session.date,
                    exerciseId = exercise.id,
                    exerciseName = exercise.name,
                    setIndex = set.index,
                    weightKg = weight,
                    reps = reps,
                    timestamp = now
                )
            )
        }
    }

    return result
}

/**
 * Format HH:MM:SS à partir d'un nombre de secondes.
 */
private fun formatDuration(totalSeconds: Int): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}