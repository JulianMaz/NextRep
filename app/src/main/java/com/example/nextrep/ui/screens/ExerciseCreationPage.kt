package com.example.nextrep.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nextrep.models.Exercise
import com.example.nextrep.ui.components.LabeledTextField

@Composable
fun ExerciseCreationPage(
    onExerciseCreated: (Exercise) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var series by remember { mutableStateOf("") }
    var repetitions by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        LabeledTextField(
            label = "Nom",
            value = name,
            onValueChange = { name = it }

        )

        LabeledTextField(
            label = "Description",
            value = description,
            onValueChange = { description = it }
        )

        LabeledTextField(
            label = "Séries",
            value = series,
            onValueChange = { series = it }
        )

        LabeledTextField(
            label = "Répétitions",
            value = repetitions,
            onValueChange = { repetitions = it }
        )

        LabeledTextField(
            label = "Photo (URI)",
            value = photoUri,
            onValueChange = { photoUri = it }
        )
        Spacer(modifier = Modifier.height(16.dp))
// push
        Button(
            onClick = {
                val exercise = Exercise(
                    id = 0,
                    name = name,
                    description = description,
                    series = series.toIntOrNull() ?: 0,
                    repetitions = repetitions.toIntOrNull() ?: 0,
                    photoUri = photoUri.ifEmpty { null }
                )
                onExerciseCreated(exercise)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Créer l'exercice")
        }
    }
}
// push