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
import androidx.compose.ui.unit.dp
import com.example.nextrep.models.data.Exercise
import com.example.nextrep.models.data.Session
import com.example.nextrep.models.entity.WorkoutSetEntity
import com.example.nextrep.viewmodels.SessionsViewModel
import kotlinx.coroutines.delay

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
        // ===== HEADER LIVE (timer + Finish sur une ligne) =====
        HeaderLiveSection(
            elapsedSeconds = elapsedSeconds,
            onFinishWorkout = {
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
            NoExercisesSection(onAddExercisesClick = onAddExercisesClick)
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(session.exercises, key = { it.id }) { exercise ->

                    // 🔹 Récupère les sets existants ou crée la liste initiale pour cet exo
                    val setsForExercise = exerciseSets[exercise.id]
                        ?: initialSetsForExercise(exercise).also { initial ->
                            exerciseSets = exerciseSets.toMutableMap().apply {
                                put(exercise.id, initial)
                            }
                        }

                    ExerciseLiveBlock(
                        exercise = exercise,
                        sets = setsForExercise,
                        onSetsChanged = { updatedList ->
                            // 🔹 met à jour la map globale
                            exerciseSets = exerciseSets.toMutableMap().apply {
                                put(exercise.id, updatedList)
                            }
                            // 🔹 recalcule le nombre total de sets complétés
                            totalCompletedSets = exerciseSets
                                .values
                                .flatten()
                                .count { it.isCompleted }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
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

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Timer
        Text(
            text = formattedTimer,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Bouton Finish
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
                StatItem(label = "Duration", value = formattedDuration)

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
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

                StatItem(label = "Sets", value = totalCompletedSets.toString())
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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

/**
 * Bloc d’un exercice dans le live : affichage + table des sets.
 * L’état des sets est ENTIEREMENT géré par le parent (WorkoutLivePage).
 */
@Composable
private fun ExerciseLiveBlock(
    exercise: Exercise,
    sets: List<SetRowState>,
    onSetsChanged: (List<SetRowState>) -> Unit
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
            // ----- En-tête exercice -----
            Row(verticalAlignment = Alignment.CenterVertically) {
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

            sets.forEach { setRow ->
                SetRow(
                    setRow = setRow,
                    onValueChange = { updated ->
                        val newList = sets.map {
                            if (it.index == updated.index) updated else it
                        }
                        onSetsChanged(newList)
                    },
                    onCompletedToggle = { updated ->
                        val newList = sets.map {
                            if (it.index == updated.index) updated else it
                        }
                        onSetsChanged(newList)
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = {
                    val nextIndex = (sets.maxOfOrNull { it.index } ?: 0) + 1
                    val newList = sets + SetRowState(index = nextIndex)
                    onSetsChanged(newList)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "+ Add set", textAlign = TextAlign.Center)
            }
        }
    }
}

/**
 * Ligne d’un set – totalement "stateless".
 * Toute la donnée vient de SetRowState, et les modifications
 * sont renvoyées au parent via les callbacks.
 */
@Composable
private fun SetRow(
    setRow: SetRowState,
    onValueChange: (SetRowState) -> Unit,
    onCompletedToggle: (SetRowState) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(setRow.index.toString(), Modifier.weight(0.8f))

        Text(setRow.previousKg, Modifier.weight(1f))

        OutlinedTextField(
            value = setRow.weightKg,
            onValueChange = { newWeight ->
                onValueChange(setRow.copy(weightKg = newWeight))
            },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = setRow.reps,
            onValueChange = { newReps ->
                onValueChange(setRow.copy(reps = newReps))
            },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            singleLine = true
        )

        Checkbox(
            checked = setRow.isCompleted,
            onCheckedChange = { checked ->
                onCompletedToggle(setRow.copy(isCompleted = checked))
            },
            modifier = Modifier.weight(0.8f)
        )
    }
}

/**
 * Liste initiale de sets pour un exercice, basée sur :
 *  - exercise.series (nb de sets)
 *  - exercise.repetitions (pré-remplissage des reps)
 */
private fun initialSetsForExercise(exercise: Exercise): List<SetRowState> {
    val count = (exercise.series.takeIf { it > 0 } ?: 3)
    val defaultReps = if (exercise.repetitions > 0) exercise.repetitions.toString() else ""

    return (1..count).map { index ->
        SetRowState(index = index, reps = defaultReps)
    }
}

/**
 * Construit la liste des WorkoutSetEntity à partir
 * de la session et de la map exerciseId -> List<SetRowState>.
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