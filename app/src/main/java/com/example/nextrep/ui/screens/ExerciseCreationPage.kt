package com.example.nextrep.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.nextrep.models.Exercise
import com.example.nextrep.ui.components.LabeledTextField


import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextrep.viewmodels.ExercisesViewModel

@Composable
fun ExerciseCreationPage(
    exercisesViewModel: ExercisesViewModel = viewModel()
) {
    // UI State local pour la création
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var series by remember { mutableStateOf("") }
    var repetitions by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf("") }

    ExerciseCreationContent(
        name = name,
        description = description,
        series = series,
        repetitions = repetitions,
        photoUri = photoUri,
        onNameChange = { name = it },
        onDescriptionChange = { description = it },
        onSeriesChange = { series = it },
        onRepetitionsChange = { repetitions = it },
        onPhotoUriChange = { photoUri = it },
        onSave = {
            val exercise = Exercise(
                id = 0,
                name = name,
                description = description,
                series = series.toIntOrNull() ?: 0,
                repetitions = repetitions.toIntOrNull() ?: 0,
                photoUri = photoUri.ifEmpty { null }
            )

            exercisesViewModel.addExercise(exercise)
        }
    )
}

@Composable
fun ExerciseCreationContent(
    name: String,
    description: String,
    series: String,
    repetitions: String,
    photoUri: String,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSeriesChange: (String) -> Unit,
    onRepetitionsChange: (String) -> Unit,
    onPhotoUriChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        LabeledTextField(label = "Exercise name", value = name, onValueChange = onNameChange)
        LabeledTextField(label = "Description", value = description, onValueChange = onDescriptionChange)
        LabeledTextField(label = "Series", value = series, onValueChange = onSeriesChange)
        LabeledTextField(label = "Repetitions", value = repetitions, onValueChange = onRepetitionsChange)
        LabeledTextField(label = "Photo URI", value = photoUri, onValueChange = onPhotoUriChange)

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Exercise")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExerciseCreationPagePreview() {
    ExerciseCreationContent(
        name = "",
        description = "",
        series = "",
        repetitions = "",
        photoUri = "",
        onNameChange = {},
        onDescriptionChange = {},
        onSeriesChange = {},
        onRepetitionsChange = {},
        onPhotoUriChange = {},
        onSave = {}
    )
}