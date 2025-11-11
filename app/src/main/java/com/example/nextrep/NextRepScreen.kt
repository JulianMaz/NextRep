package com.example.nextrep

import androidx.annotation.StringRes
import androidx.compose.foundation.gestures.forEach
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.nextrep.ui.screens.ExercisesListPage
import com.example.nextrep.ui.screens.HomePage
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.nextrep.ui.bottomNavItems
import com.example.nextrep.ui.screens.CongratulationsPage
import com.example.nextrep.ui.screens.ExerciseCreationPage
import com.example.nextrep.ui.screens.MainSessionPage
import com.example.nextrep.ui.screens.SessionCreationPage
import com.example.nextrep.ui.screens.SessionsListPage
import com.example.nextrep.ui.screens.SettingsPage
import com.example.nextrep.ui.screens.StatsPage
import androidx.navigation.NavDestination.Companion.hierarchy


enum class NextRepScreen(@StringRes val title: Int) {
    HomePage(title = R.string.app_name),
    ExercisesListPage(title = R.string.exercises_list_page),
    SessionsListPage(title = R.string.sessions_list_page),
    MainSessionPage(title = R.string.main_session_page),
    ExerciseCreationPage(title = R.string.exercise_creation_page),
    SessionCreationPage(title = R.string.session_creation_page),
    StatsPage(title = R.string.stats_page),
    CongratulationsPage(title = R.string.congratulations_page),
    SettingsPage(title = R.string.settings_page)
}
@Composable
fun NextRepApp(
    navController: NavHostController = rememberNavController()
) {
    // 1. Define the list of routes that should display the bottom navigation bar.
    val bottomBarRoutes = setOf(
        NextRepScreen.HomePage.name,
        NextRepScreen.ExercisesListPage.name,
        NextRepScreen.SessionsListPage.name,
        NextRepScreen.StatsPage.name
    )

    // 2. Get the current back stack entry from the NavController.
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    // 3. Get the current route from the back stack entry.
    val currentRoute = navBackStackEntry?.destination?.route

    // 4. Determine if the bottom bar should be shown.
    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val isSelected = navBackStackEntry?.destination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    // Pop up to the start destination of the graph to
                                    // avoid building up a large stack of destinations
                                    // on the back stack as users select items
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies of the same destination when re-selecting the same item
                                    launchSingleTop = true
                                    // Restore state when re-selecting a previously selected item
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NextRepScreen.HomePage.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = NextRepScreen.HomePage.name) {
                HomePage(
                    onExercisesListButtonClicked = {
                        navController.navigate(NextRepScreen.ExercisesListPage.name)
                    },
                    onSessionCreated = {
                        navController.navigate(NextRepScreen.MainSessionPage.name)
                    }
                )
            }
            composable(route = NextRepScreen.ExercisesListPage.name) {
                ExercisesListPage(
                    onAddExercise = {
                        navController.navigate(NextRepScreen.ExerciseCreationPage.name)
                    },
                    onExerciseClick = { exerciseId ->
                    }
                )
            }
            composable(route = NextRepScreen.ExerciseCreationPage.name) {
                ExerciseCreationPage(
                    onExerciseCreated = {
                        navController.navigate(NextRepScreen.ExercisesListPage.name)
                    }
                )
            }
            composable(route = NextRepScreen.MainSessionPage.name) {
                MainSessionPage(
                    onExerciseAdded = {
                        navController.navigate(NextRepScreen.ExercisesListPage.name)
                    },
                    onFinishWorkout = {
                        navController.navigate(NextRepScreen.CongratulationsPage.name) {
                            // Clear the back stack up to home so you don't go back into the workout
                            popUpTo(NextRepScreen.HomePage.name) { inclusive = false }
                        }
                    }
                )
            }
            composable(route = NextRepScreen.SessionsListPage.name) {
                SessionsListPage(
                    onSessionClick = { sessionId ->
                        navController.navigate(NextRepScreen.MainSessionPage.name)
                    },
                    onAddSession = {
                        navController.navigate(NextRepScreen.SessionCreationPage.name)
                    }
                )
            }
            composable(route = NextRepScreen.SessionCreationPage.name) {
                SessionCreationPage(
                    onSessionCreated = {
                        // Navigate back to the sessions list after creation
                        navController.navigate(NextRepScreen.SessionsListPage.name) {
                            // Optional: Clear the back stack to avoid going back to the creation page
                            popUpTo(NextRepScreen.SessionsListPage.name) { inclusive = true }
                        }
                    }
                )
            }
            composable(route = NextRepScreen.CongratulationsPage.name) {
                CongratulationsPage(
                    onNavigateHome = {
                        navController.navigate(NextRepScreen.HomePage.name) {
                            // Clear the entire back stack up to the home page
                            popUpTo(NextRepScreen.HomePage.name) { inclusive = true }
                        }
                    }
                )
            }
            composable(route = NextRepScreen.SettingsPage.name) {
                SettingsPage()
            }
            composable(route = NextRepScreen.StatsPage.name) {
                StatsPage()
            }
        }
    }
}
