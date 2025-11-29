package com.example.nextrep.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.nextrep.data.exercise.ExerciseDao
import com.example.nextrep.data.session.SessionDao

class SessionsViewModelFactory(
    private val sessionDao: SessionDao,
    private val exerciseDao: ExerciseDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SessionsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SessionsViewModel(sessionDao, exerciseDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
