package com.example.nextrep.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.nextrep.NextRepScreen

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = NextRepScreen.HomePage.name,
        label = "Home",
        icon = Icons.Filled.Home
    ),
    BottomNavItem(
        route = NextRepScreen.SessionsListPage.name,
        label = "Sessions",
        icon = Icons.Filled.List
    ),
    BottomNavItem(
        route = NextRepScreen.ExercisesListPage.name,
        label = "Exercises",
        icon = Icons.Filled.FitnessCenter
    ),
    BottomNavItem(
        route = NextRepScreen.AllExercisesHistoryPage.name,
        label = "History",
        icon = Icons.Filled.History
    )
)