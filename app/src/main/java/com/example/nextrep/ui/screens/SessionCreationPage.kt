package com.example.nextrep.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SessionCreationPage(
    onSessionCreated: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Create a New Session", style = MaterialTheme.typography.headlineMedium)
        // In the future, you'll add TextFields for session name, etc.
        Button(onClick = onSessionCreated) {
            Text("Save Session")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SessionCreationPagePreview() {
    MaterialTheme {
        SessionCreationPage(onSessionCreated = {})
    }
}
