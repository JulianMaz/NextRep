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
fun CongratulationsPage(
    onNavigateHome: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Congratulations!", style = MaterialTheme.typography.headlineLarge)
        Text("You finished your workout!", style = MaterialTheme.typography.bodyLarge)
        Button(onClick = onNavigateHome) {
            Text("Done")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CongratulationsPagePreview() {
    MaterialTheme {
        CongratulationsPage(onNavigateHome = {})
    }
}
