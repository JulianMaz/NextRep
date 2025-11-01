package com.example.nextrep

import androidx.annotation.StringRes

enum class CupcakeScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    ExercisesListPage(title = R.string.exercises_list_page),
    SessionsListPage(title = R.string.sessions_list_page),
    MainSessionPage(title = R.string.main_session_page),
    ExerciseCreationPage(title = R.string.exercise_creation_page),
    SessionCreationPage(title = R.string.session_creation_page),
    StatsPage(title = R.string.stats_page),
    CongratulationsPage(title = R.string.congratulations_page),
    SettingsPage(title = R.string.settings_page)
}