package com.example.nextrep.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HomePage(
    onStartTraining: () -> Unit,              // 🔹 callback unique
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Home Page")
        }

        ExtendedFloatingActionButton(
            onClick = { onStartTraining() },   // 🔹 lance la sélection de session
            icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Start training") },
            text = { Text(text = "Start training") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(15.dp,38.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomePagePreview(){
    HomePage(
        onStartTraining = {}
    )
}