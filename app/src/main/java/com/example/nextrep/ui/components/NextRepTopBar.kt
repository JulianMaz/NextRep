package com.example.nextrep.ui.components

import androidx.compose.foundation.layout.size // Optionnel: pour ajuster la taille du logo
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NextRepTopBar(
    onSettingsClick: () -> Unit,
    onHomeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        // Le titre au milieu
        title = {
            Text("NextRep", style = MaterialTheme.typography.headlineLarge)

            /*
            IconButton(onClick = onHomeClick) {
                Icon(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Go to Home",

                    tint = Color.Unspecified,

                    // Ajuster la taille si le logo est trop grand/petit
                    modifier = Modifier.size(600.dp)
                )
            }*/
        },

        // L'icône à gauche (Settings)
        navigationIcon = {
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    // Ici on garde une teinte, car c'est une icône système
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        },

        actions = {
            // Vide pour l'instant
        },

        // Changement de la couleur de fond
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(

            containerColor = MaterialTheme.colorScheme.primaryContainer,

            // Changez aussi la couleur des titres/icônes par défaut pour que ça reste lisible
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = modifier
    )
}
