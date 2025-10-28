package com.example.nextrep

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.nextrep.ui.theme.screens.ExerciseCreationPage
import com.example.nextrep.ui.theme.NextRepTheme
import com.example.nextrep.data.JsonHelper
import androidx.compose.ui.platform.LocalContext
import com.example.nextrep.models.Exercise

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NextRepTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    var showExercisePage by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            if (showExercisePage) {
                ExerciseCreationPage { exercise: Exercise ->
                    JsonHelper.saveExercise(context, exercise)
                }
            } else {
                Button(onClick = { showExercisePage = true }) {
                    Text(text = "Aller à Exercise Creation")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    NextRepTheme {
        MainScreen()
    }
}