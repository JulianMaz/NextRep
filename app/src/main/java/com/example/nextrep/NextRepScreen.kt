package com.example.nextrep

import ExercisesViewModelFactory
import android.app.Application
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.example.nextrep.data.AppDatabase
import com.example.nextrep.ui.components.NextRepTopBar
import com.example.nextrep.viewmodels.ExercisesViewModel
import com.example.nextrep.viewmodels.SessionsViewModel
import com.example.nextrep.viewmodels.SessionsViewModelFactory

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

    val context = LocalContext.current
    val database = AppDatabase.buildDatabase(context.applicationContext as Application)

    val exercisesViewModel: ExercisesViewModel = viewModel(
        factory = ExercisesViewModelFactory(database.exerciseDao())
    )
    val sessionsViewModel: SessionsViewModel = viewModel(
        factory = SessionsViewModelFactory(database.sessionDao(), database.exerciseDao())
    )
    // Define the list of routes that should display the bottom navigation bar.

    val bottomBarRoutes = setOf(
        NextRepScreen.HomePage.name,
        NextRepScreen.ExercisesListPage.name,
        NextRepScreen.SessionsListPage.name,
        NextRepScreen.StatsPage.name
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomBarRoutes
    val showTopBar = currentRoute != NextRepScreen.CongratulationsPage.name // Exemple pour l'instant


    Scaffold(
        topBar = {
            if (showTopBar) {
                NextRepTopBar(
                    onSettingsClick = {
                        navController.navigate(NextRepScreen.SettingsPage.name)
                    },
                    onHomeClick = {
                        navController.navigate(NextRepScreen.HomePage.name) {
                            // Nettoyer la pile de retour pour éviter d'empiler les pages
                            popUpTo(NextRepScreen.HomePage.name) { inclusive = true }
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val isSelected = navBackStackEntry?.destination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
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
                    newSessionCreated = {
                        navController.navigate(NextRepScreen.MainSessionPage.name)
                    }
                )
            }
            composable(route = NextRepScreen.ExercisesListPage.name) {
                ExercisesListPage(
                    exercisesViewModel = exercisesViewModel,       // 🔹 même instance
                    onAddExercise = {
                        navController.navigate(NextRepScreen.ExerciseCreationPage.name)
                    },
                    onExerciseClick = { id ->
                        // 🔹 plus tard: page de détail
                    }
                )
            }
            composable(route = NextRepScreen.ExerciseCreationPage.name) {
                ExerciseCreationPage(
                    exercisesViewModel = exercisesViewModel,       // 🔹 même instance
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
                    sessionsViewModel = sessionsViewModel,
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
                    sessionsViewModel = sessionsViewModel,
                    onSessionCreated = {
                        // On revient simplement à la liste
                        navController.popBackStack(
                            NextRepScreen.SessionsListPage.name,
                            inclusive = false
                        )
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
