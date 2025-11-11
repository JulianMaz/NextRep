package com.example.nextrep.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.nextrep.NextRepScreen

// Data class to represent each item in the bottom navigation bar
data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

// List of items to display in the bottom navigation bar
val bottomNavItems = listOf(
    BottomNavItem(
        label = "Home",
        icon = Icons.Default.Home,
        route = NextRepScreen.HomePage.name
    ),
    BottomNavItem(
        label = "Sessions",
        icon = Icons.Default.AddCircle,
        route = NextRepScreen.SessionsListPage.name
    ),
    BottomNavItem(
        label = "Exercises",
        icon = Icons.Default.Build,
        route = NextRepScreen.ExercisesListPage.name
    ),
    BottomNavItem(
        label = "Stats",
        icon = Icons.Default.Star,
        route = NextRepScreen.StatsPage.name
    )
)
