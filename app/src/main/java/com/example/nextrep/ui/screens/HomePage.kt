package com.example.nextrep.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextrep.R
import com.example.nextrep.models.Exercise
import com.example.nextrep.viewmodels.ExercisesViewModel

@Composable
fun HomePage(
    onStartTraining: () -> Unit,
    onNewSessionClick: () -> Unit,
    onNewExerciseClick: () -> Unit,
    modifier: Modifier = Modifier,
    exercisesViewModel: ExercisesViewModel = viewModel() // 🔹 pour récupérer les exos
) {
    // 🔹 Observe la liste des exercices existants
    val exercisesUiState by exercisesViewModel.uiState.collectAsState()
    val allExercises = exercisesUiState.exercises

    // 🔹 On prend simplement les 3 derniers exercices ajoutés
    val topExercises: List<Exercise> = remember(allExercises) {
        if (allExercises.size <= 3) allExercises
        else allExercises.takeLast(3)
    }

    val quoteOfTheDay = remember {
        val quotes = listOf(
            "The only bad workout is the one you didn’t do.",
            "Small progress is still progress.",
            "One more rep. That’s where you grow.",
            "Discipline beats motivation.",
            "Stronger than yesterday.",
            "You don’t have to be extreme, just consistent.",
            "Results happen over time, not overnight."
        )
        quotes.random()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ===== IMAGE HERO =====
        HeroImageCard(
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ===== QUOTE DU JOUR =====
        QuoteCard(
            quote = quoteOfTheDay,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ===== TOP EXERCISES =====
        TopExercisesCard(
            exercises = topExercises,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ===== 4 ACTIONS =====
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HomeActionButton(
                    title = "New session",
                    onClick = onNewSessionClick,
                    modifier = Modifier.weight(1f)
                )
                HomeActionButton(
                    title = "New exercise",
                    onClick = onNewExerciseClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ===== START TRAINING =====
        ExtendedFloatingActionButton(
            onClick = { onStartTraining() },
            icon = { Icon(Icons.Default.Add, contentDescription = "Start Training") },
            text = { Text("Start training") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )
    }
}

// --------------------------------------------------------
//  Sous-composants
// --------------------------------------------------------

@Composable
private fun HeroImageCard(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .height(300.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_1),
            contentDescription = "NextRep Hero",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(4.dp)
                .clip(MaterialTheme.shapes.extraLarge) // bords arrondis
        )
    }
}

@Composable
private fun HomeActionButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(64.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(text = title)
    }
}

@Composable
private fun QuoteCard(
    quote: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Petit label au dessus
            Text(
                text = "Today’s quote",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )

            // Grosse quote au centre
            Text(
                text = "“$quote”",
                style = MaterialTheme.typography.headlineSmall,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth()
            )

            // Ligne de base en bas (style moderne)
            Text(
                text = "Keep pushing • NextRep",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun TopExercisesCard(
    exercises: List<Exercise>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Your top exercises",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (exercises.isEmpty()) {
                Text(
                    text = "You haven’t created any exercises yet.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                exercises.forEachIndexed { index, exercise ->
                    Text(
                        text = "${index + 1}. ${exercise.name}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}