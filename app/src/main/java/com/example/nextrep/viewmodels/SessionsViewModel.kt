package com.example.nextrep.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextrep.data.exercise.ExerciseDao
import com.example.nextrep.data.models.Exercise
import com.example.nextrep.data.models.Session
import com.example.nextrep.data.models.SessionExerciseCrossRef
import com.example.nextrep.data.models.SessionWithExercises
import com.example.nextrep.data.session.SessionDao
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SessionsUiState(
    val sessions: List<SessionWithExercises> = emptyList()
)

class SessionsViewModel(
    private val sessionDao: SessionDao,
    private val exerciseDao: ExerciseDao
) : ViewModel() {

    val uiState: StateFlow<SessionsUiState> =
        sessionDao.getAllSessionsWithExercises()
            .map { SessionsUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = SessionsUiState()
            )

    fun addSessionWithExercises(session: Session, exercises: List<Exercise>) {
        viewModelScope.launch {
            val sessionId = sessionDao.insertSession(session).toInt()

            exercises.forEach { exercise ->
                val exerciseId = exerciseDao.insertExercise(exercise).toInt()
                sessionDao.insertCrossRef(
                    SessionExerciseCrossRef(
                        sessionId = sessionId,
                        exerciseId = exerciseId
                    )
                )
            }
        }
    }

    fun deleteSession(session: Session) {
        viewModelScope.launch {
            sessionDao.deleteSession(session)
        }
    }

    fun getSessionWithExercises(sessionId: Int): Flow<SessionWithExercises> {
        return sessionDao.getSessionWithExercises(sessionId)
    }
}
