package com.example.nextrep.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HomePage(
    newSessionCreated: () -> Unit,
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
            onClick = { newSessionCreated() },
            icon = { Icon(Icons.Default.Add, contentDescription = "Add Session") },
            text = { Text(text = "New Session") },
            modifier = Modifier
                .align(Alignment.BottomEnd) // Place le bouton en bas à droite
                .padding(16.dp)             // Ajoute une marge pour ne pas coller au bord
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomePagePreview(){
    HomePage(
        newSessionCreated = {}
    )
}