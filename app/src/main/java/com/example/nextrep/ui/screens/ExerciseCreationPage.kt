package com.example.nextrep.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.nextrep.models.Exercise
import com.example.nextrep.models.ExercisesRepository
import com.example.nextrep.viewmodels.ExercisesViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@Composable
fun ExerciseCreationPage(
    exercisesViewModel: ExercisesViewModel = viewModel(),
    exercisesRepository: ExercisesRepository,
    onExerciseCreated: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var series by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var imagePath by remember { mutableStateOf<String?>(null) }

    // Launcher pour prendre une photo avec la caméra (retourne un Bitmap)
    val takePicture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            // Dossier interne pour les images d'exos
            val imagesDir = File(context.filesDir, "exercises_images")
            if (!imagesDir.exists()) {
                imagesDir.mkdirs()
            }

            // Fichier unique pour cette photo
            val file = File(imagesDir, "exercise_${System.currentTimeMillis()}.jpg")

            // On écrit le bitmap dans le fichier
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }

            // On garde le chemin du fichier
            imagePath = file.absolutePath
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Exercise name") } ,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)

        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.padding(top = 8.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)

        )

        OutlinedTextField(
            value = series,
            onValueChange = { series = it },
            label = { Text("Series") },
            modifier = Modifier.padding(top = 8.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)

        )

        OutlinedTextField(
            value = reps,
            onValueChange = { reps = it },
            label = { Text("Repetitions") },
            modifier = Modifier.padding(top = 8.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)

        )

        // Bouton pour prendre une photo
        Button(
            onClick = { takePicture.launch(null) },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Prendre une photo")
        }

        // Prévisualisation de l'image si on en a une
        imagePath?.let { path ->
            val file = File(path)
            if (file.exists()) {
                Image(
                    painter = rememberAsyncImagePainter(model = file),
                    contentDescription = "Exercise Image Preview",
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxSize(0.5f),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Sauvegarde de l'exercice
        Button(
            onClick = {
                if (name.isBlank()) return@Button

                val baseExercise = Exercise(
                    id = 0,
                    name = name,
                    description = description,
                    series = series.toIntOrNull() ?: 0,
                    repetitions = reps.toIntOrNull() ?: 0,
                    photoUri = imagePath // on stocke le chemin du fichier
                )

                scope.launch {
                    val saved = exercisesRepository.addExercise(baseExercise)
                    exercisesViewModel.addExerciseLocal(saved)
                    onExerciseCreated()
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Save exercise")
        }
    }
}