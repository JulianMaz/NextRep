package com.example.nextrep.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun StatsPage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Workout Statistics", style = MaterialTheme.typography.headlineMedium)
        // You'll add charts and stats data here
    }
}

@Preview(showBackground = true)
@Composable
fun StatsPagePreview() {
    MaterialTheme {
        StatsPage()
    }
}
