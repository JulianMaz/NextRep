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
fun SettingsPage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("App Settings", style = MaterialTheme.typography.headlineMedium)
        // You'll add settings options here (e.g., dark mode toggle, units)
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPagePreview() {
    MaterialTheme {
        SettingsPage()
    }
}
