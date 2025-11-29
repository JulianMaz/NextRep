package com.example.nextrep

import androidx.room.Room
import androidx.compose.ui.platform.LocalContext
import com.example.nextrep.data.NextRepDatabase
import androidx.compose.runtime.collectAsState           // 🔹 pour Flow/StateFlow.collectAsState()
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.nextrep.ui.screens.InfoSessionPage
import com.example.nextrep.ui.screens.SessionCreationPage
import com.example.nextrep.ui.screens.SessionsListPage
import com.example.nextrep.ui.screens.SettingsPage
import androidx.navigation.NavDestination.Companion.hierarchy
import com.example.nextrep.models.Session
import com.example.nextrep.ui.components.NextRepTopBar
import com.example.nextrep.ui.screens.WorkoutLivePage
import com.example.nextrep.viewmodels.ExercisesViewModel
import com.example.nextrep.viewmodels.SessionsViewModel
import com.example.nextrep.models.WorkoutHistoryRepository
import com.example.nextrep.ui.screens.ExerciseHistoryPage
import kotlinx.coroutines.launch

enum class NextRepScreen(@StringRes val title: Int) {
    HomePage(title = R.string.app_name),
    ExercisesListPage(title = R.string.exercises_list_page),
    SessionsListPage(title = R.string.sessions_list_page),

    InfoSessionPage(title = R.string.main_session_page),  // tu pourras renommer la string plus tard
    ExerciseCreationPage(title = R.string.exercise_creation_page),
    SessionCreationPage(title = R.string.session_creation_page),

    // 🔹 Nouvel écran pour l'onglet "Historique exos" (on réutilise le titre des exos)
    ExercisesHistoryPage(title = R.string.exercises_list_page),

    CongratulationsPage(title = R.string.congratulations_page),
    SettingsPage(title = R.string.settings_page)
}

@Composable
fun NextRepApp(
    navController: NavHostController = rememberNavController()
) {
    val sessionsViewModel: SessionsViewModel = viewModel()
    val exercisesViewModel: ExercisesViewModel = viewModel()

    // Routes qui affichent la bottom bar
    val bottomBarRoutes = setOf(
        NextRepScreen.HomePage.name,
        NextRepScreen.ExercisesListPage.name,
        NextRepScreen.SessionsListPage.name,
        NextRepScreen.ExercisesHistoryPage.name        // 🔹 remplace l’ancien Stats
    )

    //======= DB REPO =======
    val context = LocalContext.current.applicationContext

    // 🔹 Initialisation DATABASE
    val db = remember {
        Room.databaseBuilder(
            context,
            NextRepDatabase::class.java,
            "nextrep-db"
        ).build()
    }

    // 🔹 DAO
    val workoutSetDao = db.workoutSetDao()

    // 🔹 Repository
    val workoutHistoryRepository = remember {
        WorkoutHistoryRepository(workoutSetDao)
    }

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
                        val isSelected =
                            navBackStackEntry?.destination?.hierarchy?.any { it.route == item.route } == true
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
            // ===== HOME =====
            composable(route = NextRepScreen.HomePage.name) {
                HomePage(
                    newSessionCreated = {
                        navController.navigate(NextRepScreen.SessionCreationPage.name)   // 🔹 On va créer une session
                    }
                )
            }

            // ===== EXERCISES (onglet classique) =====
            composable(route = NextRepScreen.ExercisesListPage.name) {
                ExercisesListPage(
                    exercisesViewModel = exercisesViewModel,
                    onAddExercise = {
                        navController.navigate(NextRepScreen.ExerciseCreationPage.name)
                    },
                    // 🔹 Ici tu peux plus tard ouvrir une page "détail exo" si tu veux
                    onExerciseClick = { /* rien pour l’instant ou futur détail exo */ }
                )
            }

            // ===== CREATION EXO =====
            composable(route = NextRepScreen.ExerciseCreationPage.name) {
                ExerciseCreationPage(
                    exercisesViewModel = exercisesViewModel,
                    onExerciseCreated = {
                        navController.navigate(NextRepScreen.ExercisesListPage.name)
                    }
                )
            }

            // ===== INFO SESSION =====
            composable(
                route = "${NextRepScreen.InfoSessionPage.name}/{sessionId}",    // 🔹 route avec argument
                arguments = listOf(
                    navArgument("sessionId") { type = NavType.IntType }         // 🔹 définition de l'argument
                )
            ) { backStackEntry ->
                val sessionId =
                    backStackEntry.arguments?.getInt("sessionId") ?: return@composable  // 🔹 on récupère l'ID

                InfoSessionPage(
                    sessionId = sessionId,
                    sessionsViewModel = sessionsViewModel,
                    onExerciseAdded = {
                        navController.navigate(NextRepScreen.ExercisesListPage.name)
                    },
                    onStartWorkout = { id ->
                        navController.navigate("WorkoutLive/$id")     // 🔹 lancement direct
                    },
                    onFinishWorkout = {
                        navController.navigate(NextRepScreen.CongratulationsPage.name) {
                            popUpTo(NextRepScreen.HomePage.name) { inclusive = false }
                        }
                    }
                )
            }

            // ===== SESSIONS LIST =====
            composable(route = NextRepScreen.SessionsListPage.name) {
                SessionsListPage(
                    sessionsViewModel = sessionsViewModel,
                    onSessionClick = { sessionId ->
                        navController.navigate("${NextRepScreen.InfoSessionPage.name}/$sessionId")
                    },
                    onAddSession = {
                        navController.navigate(NextRepScreen.SessionCreationPage.name)
                    }
                )
            }

            // ===== CREATION SESSION =====
            composable(route = NextRepScreen.SessionCreationPage.name) {
                val sessionsUiState by sessionsViewModel.uiState.collectAsState()

                SessionCreationPage(
                    sessionsViewModel = sessionsViewModel,
                    uiState = sessionsUiState,
                    onChooseExercises = {
                        navController.navigate("chooseExercises")                   // 🔹 ouvre la sélection d'exos
                    },
                    onSessionCreated = {
                        // 🔹 On revient simplement à l'écran précédent (souvent SessionsListPage)
                        navController.popBackStack()
                    }
                )
            }

            // ===== CONGRATS =====
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

            // ===== SETTINGS =====
            composable(route = NextRepScreen.SettingsPage.name) {
                SettingsPage()
            }

            // ===== EXERCICES POUR NOUVELLE SESSION =====
            composable(route = "ExercisesForNewSession") {
                ExercisesListPage(
                    exercisesViewModel = exercisesViewModel,
                    onAddExercise = {
                        navController.navigate(NextRepScreen.ExerciseCreationPage.name)
                    },
                    onExerciseClick = { /* en mode sélection on ignore le clic simple */ },
                    selectionMode = true,
                    onValidateSelection = { selectedExercises ->
                        val newSession = Session(
                            id = 0,
                            name = "Nouvelle session",
                            date = "Date à définir",
                            exercises = selectedExercises
                        )
                        sessionsViewModel.addSession(newSession)

                        navController.popBackStack(
                            NextRepScreen.SessionsListPage.name,
                            inclusive = false
                        )
                    }
                )
            }

            // ===== CHOIX EXOS POUR CREATION SESSION =====
            composable(route = "chooseExercises") {
                ExercisesListPage(
                    exercisesViewModel = exercisesViewModel,
                    onAddExercise = {
                        navController.navigate(NextRepScreen.ExerciseCreationPage.name)
                    },
                    onExerciseClick = { /* pas utilisé en mode sélection */ },
                    selectionMode = true,
                    onValidateSelection = { selectedExercises ->
                        sessionsViewModel.setPendingExercisesForNewSession(selectedExercises)
                        navController.popBackStack()
                    }
                )
            }

            // ===== WORKOUT LIVE =====
            composable(
                route = "WorkoutLive/{sessionId}",
                arguments = listOf(
                    navArgument("sessionId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val sessionId =
                    backStackEntry.arguments?.getInt("sessionId") ?: return@composable

                val scope = rememberCoroutineScope()

                WorkoutLivePage(
                    sessionId = sessionId,
                    sessionsViewModel = sessionsViewModel,
                    onFinishWorkout = { completedSets ->
                        scope.launch {
                            workoutHistoryRepository.saveWorkoutSets(completedSets)
                        }
                        navController.navigate(NextRepScreen.CongratulationsPage.name) {
                            popUpTo(NextRepScreen.HomePage.name) { inclusive = false }
                        }
                    },
                    onAddExercisesClick = {
                        navController.navigate("chooseExercisesForWorkout/$sessionId")
                    }
                )
            }

            // ===== HISTORIQUE D'UN EXO (détail) =====
            composable(
                route = "ExerciseHistory/{exerciseId}",
                arguments = listOf(
                    navArgument("exerciseId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val exerciseId =
                    backStackEntry.arguments?.getInt("exerciseId") ?: return@composable

                ExerciseHistoryPage(
                    exerciseId = exerciseId,
                    exercisesViewModel = exercisesViewModel,
                    workoutHistoryRepository = workoutHistoryRepository
                )
            }

            // ===== NOUVEL ONGLET : LISTE DES EXOS POUR HISTORIQUE =====
            composable(route = NextRepScreen.ExercisesHistoryPage.name) {
                ExercisesListPage(
                    exercisesViewModel = exercisesViewModel,
                    onAddExercise = {
                        navController.navigate(NextRepScreen.ExerciseCreationPage.name)
                    },
                    // 🔹 Ici : clic sur un exo -> historique de cet exo
                    onExerciseClick = { exerciseId ->
                        navController.navigate("ExerciseHistory/$exerciseId")
                    }
                )
            }
        }
    }
}