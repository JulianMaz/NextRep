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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nextrep.models.Exercise
import com.example.nextrep.models.Session
import com.example.nextrep.models.WorkoutSetEntity
import com.example.nextrep.viewmodels.SessionsViewModel
import kotlinx.coroutines.delay
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextrep.viewmodels.WorkoutViewModel
import com.example.nextrep.viewmodels.WorkoutExerciseState
import com.example.nextrep.viewmodels.WorkoutSetState

// 🔹 Représente l'état d'une ligne de set pour un exercice
data class SetRowState(
    val index: Int,
    val previousKg: String = "-",   // 🔹 pour plus tard si tu veux stocker l'historique
    val weightKg: String = "",
    val reps: String = "",
    val isCompleted: Boolean = false
)

/**
 * Écran d'entraînement en direct (WorkoutLivePage).
 *
 * @param sessionId           ID de la session que l'on est en train de faire
 * @param sessionsViewModel   ViewModel des sessions (pour récupérer la session et ses exercices)
 * @param onFinishWorkout     Callback appelé quand l'utilisateur termine la séance,
 *                            avec la liste des WorkoutSetEntity complétés.
 * @param onAddExercisesClick Callback pour ouvrir ExercisesListPage en mode sélection
 */
@Composable
fun WorkoutLivePage(
    sessionId: Int,
    sessionsViewModel: SessionsViewModel,
    workoutViewModel: WorkoutViewModel = viewModel(),  // 🔹 nouveau param avec valeur par défaut
    onFinishWorkout: (List<WorkoutSetEntity>) -> Unit,
    onAddExercisesClick: () -> Unit,                 // 🔹 ouvre la sélection d'exercices
    modifier: Modifier = Modifier
) {
    // 🔹 Récupère la session en cours
    val session = sessionsViewModel.getSessionById(sessionId)

    // 🔹 Timer en secondes depuis l'ouverture de la page
    var elapsedSeconds by remember { mutableIntStateOf(0) }

    // 🔹 Volume manuel (en kg) saisi par l'utilisateur
    var manualVolumeKg by remember { mutableStateOf("0") }

    // 🔹 Nombre total de sets complétés (toutes les ✓)
    var totalCompletedSets by remember { mutableIntStateOf(0) }

    // 🔹 Map des sets par exercice : exerciseId -> List<SetRowState>
    var exerciseSets by remember { mutableStateOf<Map<Int, List<SetRowState>>>(emptyMap()) }

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

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // ===== HEADER LIVE =====
        HeaderLiveSection(
            elapsedSeconds = elapsedSeconds,
            onFinishWorkout = {
                // 🔹 Construction de la liste des WorkoutSetEntity complétés
                val completedSets = buildCompletedWorkoutSets(
                    session = session,
                    exerciseSets = exerciseSets
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
            // 🔹 Cas "No exercises added" (comme Lyfta au début)
            NoExercisesSection(
                onAddExercisesClick = onAddExercisesClick
            )
        } else {
            // 🔹 Liste des exercices de la session
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
            ) {
                items(session.exercises, key = { it.id }) { exercise ->
                    ExerciseLiveBlock(
                        exercise = exercise,
                        onSetsChanged = { setsForExercise ->
                            // 🔹 On met à jour la map globale des sets par exercice
                            exerciseSets = exerciseSets.toMutableMap().apply {
                                put(exercise.id, setsForExercise)
                            }
                        },
                        onSetCompletedChanged = { completed ->
                            // 🔹 ICI : on tient à jour le compteur global de sets terminés
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

            // Bouton Finish à droite
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
    onSetsChanged: (List<SetRowState>) -> Unit,      // 🔹 informe la page de TOUTES les lignes
    onSetCompletedChanged: (Boolean) -> Unit         // 🔹 informe la page qu'un set est (dé)coché
) {
    // 🔹 Nombre de séries défini dans la fiche de l'exercice.
    // Si series <= 0, on garde 3 par défaut.
    val initialSetCount = (exercise.series.takeIf { it > 0 } ?: 3)

    // 🔹 On pré-remplit éventuellement les reps avec exercise.repetitions
    val defaultReps = if (exercise.repetitions > 0) {
        exercise.repetitions.toString()
    } else {
        ""
    }

    // 🔹 Liste des sets pour cet exercice, basée sur "series"
    var sets by remember(exercise.id, initialSetCount) {
        mutableStateOf(
            (1..initialSetCount).map { index ->
                SetRowState(
                    index = index,
                    reps = defaultReps
                )
            }
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
            // ----- En-tête exercice (image + nom) -----
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 🔹 Placeholder pour l'image (tu pourras brancher Coil plus tard)
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
            // Header
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
            sets.forEach { setRow ->
                SetRow(
                    setRow = setRow,
                    onValueChange = { updated ->
                        // 🔹 ICI : on met à jour la liste des sets pour cet exercice
                        sets = sets.map { if (it.index == updated.index) updated else it }
                        onSetsChanged(sets)
                    },
                    onCompletedToggle = { updated, newlyCompleted ->
                        // 🔹 Met à jour la ligne
                        sets = sets.map { if (it.index == updated.index) updated else it }
                        onSetsChanged(sets)
                        // 🔹 Informe la page que le nombre de sets complétés a changé
                        onSetCompletedChanged(newlyCompleted)
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ----- Bouton Add set -----
            TextButton(
                onClick = {
                    val nextIndex = (sets.maxOfOrNull { it.index } ?: 0) + 1
                    // 🔹 Ajoute un nouveau set vide
                    sets = sets + SetRowState(index = nextIndex)
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
private fun SetRow(
    setRow: SetRowState,
    onValueChange: (SetRowState) -> Unit,
    onCompletedToggle: (SetRowState, Boolean) -> Unit
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
 *  - de la session (id / nom / date / exercices)
 *  - de la map exerciseId -> List<SetRowState>
 *
 * On ne garde que :
 *  - les sets cochés (isCompleted == true)
 *  - avec un weightKg et reps parsables.
 */
private fun buildCompletedWorkoutSets(
    session: Session,
    exerciseSets: Map<Int, List<SetRowState>>
): List<WorkoutSetEntity> {
    val result = mutableListOf<WorkoutSetEntity>()
    val now = System.currentTimeMillis()

    session.exercises.forEach { exercise ->
        val setsForExercise = exerciseSets[exercise.id] ?: emptyList()

        setsForExercise.forEach { row ->
            if (!row.isCompleted) return@forEach
            if (row.weightKg.isBlank() || row.reps.isBlank()) return@forEach

            val weight = row.weightKg.toFloatOrNull() ?: return@forEach
            val reps = row.reps.toIntOrNull() ?: return@forEach

            result.add(
                WorkoutSetEntity(
                    id = 0L,
                    sessionId = session.id,
                    sessionName = session.name,
                    sessionDate = session.date,
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

/**
 * Format HH:MM:SS à partir d'un nombre de secondes.
 */
private fun formatDuration(totalSeconds: Int): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}