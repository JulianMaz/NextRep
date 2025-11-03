package com.example.nextrep

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.nextrep.ui.screens.ExercisesListPage
import com.example.nextrep.ui.screens.HomePage
import com.example.nextrep.ui.OrderViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nextrep.ui.screens.ExerciseCreationPage


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
    viewModel: OrderViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        topBar = {
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
                    }
                )
            }
            composable(route = NextRepScreen.ExercisesListPage.name) {
                ExercisesListPage(
                    onHomePageButtonClicked = {
                        navController.navigate(NextRepScreen.HomePage.name)
                    },
                    onCreationExerciseButtonClicked = {
                        navController.navigate(NextRepScreen.ExerciseCreationPage.name)
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
        }
    }
}
