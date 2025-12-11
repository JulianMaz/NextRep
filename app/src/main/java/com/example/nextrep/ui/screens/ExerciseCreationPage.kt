package com.example.nextrep.ui.screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextrep.models.Exercise
import com.example.nextrep.models.ExercisesRepository
import com.example.nextrep.viewmodels.ExercisesViewModel
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.activity.compose.rememberLauncherForActivityResult
import coil.compose.rememberImagePainter
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File


class ExerciseCreationPage {
    companion object {
        const val REQUEST_CODE_PERMISSIONS = 10
    }
}
@Composable
fun ExerciseCreationPage(
    exercisesViewModel: ExercisesViewModel = viewModel(),
    exercisesRepository: ExercisesRepository,
    onExerciseCreated: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    // Vérification des permissions
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Demander les permissions si elles ne sont pas accordées
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                ExerciseCreationPage.REQUEST_CODE_PERMISSIONS
            )
        }
    }

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var series by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher for opening gallery
    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            imageUri = uri
        }
    )

    // Launcher for opening camera
    val takePicture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                imageUri?.let {
                    // You can set a flag here if needed, e.g., isCamera = true
                }
            }
        }
    )

    // Temp URI for camera image
    val tempUri = remember { mutableStateOf<Uri?>(null) }

    // Créez un fichier temporaire dans le répertoire de cache
    val tempFile = File(context.cacheDir, "temp_image.jpg")

    // Obtenez l'URI via FileProvider
    val fileUri = FileProvider.getUriForFile(
        context,
        "com.example.nextrep.provider",  // Remplacez par votre authority définie dans AndroidManifest
        tempFile
    )

    // Button to trigger camera or gallery picker
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Exercise name") }
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.padding(top = 8.dp)
        )

        OutlinedTextField(
            value = series,
            onValueChange = { series = it },
            label = { Text("Series") },
            modifier = Modifier.padding(top = 8.dp)
        )

        OutlinedTextField(
            value = reps,
            onValueChange = { reps = it },
            label = { Text("Repetitions") },
            modifier = Modifier.padding(top = 8.dp)
        )

        // Button to launch gallery
        Button(
            onClick = { pickImage.launch("image/*") },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Pick an image from Gallery")
        }

        // Display image from camera or gallery
        imageUri?.let {
            Image(
                painter = rememberImagePainter(it),
                contentDescription = "Selected Exercise Image",
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        // Button to launch camera
        Button(
            onClick = {
                // Utiliser l'URI générée par FileProvider
                fileUri?.let {
                    takePicture.launch(it)
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Take a picture")
        }

        // Save the exercise when the button is clicked
        Button(
            onClick = {
                if (name.isBlank()) return@Button

                val baseExercise = Exercise(
                    id = 0, // Room will generate the ID
                    name = name,
                    description = description,
                    series = series.toIntOrNull() ?: 0,
                    repetitions = reps.toIntOrNull() ?: 0,
                    photoUri = imageUri?.toString() // Save the URI as a string
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