package com.example.nextrep.viewmodels

import androidx.lifecycle.ViewModel
import com.example.nextrep.models.Exercise          // 🔹 Exercise pour la sélection
import com.example.nextrep.models.Session
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SessionsUiState(
    val sessions: List<Session> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val pendingExercisesForNewSession: List<Exercise> = emptyList()   // 🔹 exos choisis pour la prochaine session
)

class SessionsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SessionsUiState())
    val uiState: StateFlow<SessionsUiState> = _uiState.asStateFlow()

    private var nextId = 1

    fun addSession(session: Session) {
        val newSession = session.copy(id = nextId++)
        val updated = _uiState.value.sessions + newSession

        _uiState.value = _uiState.value.copy(
            sessions = updated,
            errorMessage = null,
            pendingExercisesForNewSession = emptyList()     // 🔹 on nettoie la sélection
        )
    }

    fun setPendingExercisesForNewSession(exercises: List<Exercise>) { // 🔹 appelée depuis chooseExercises
        _uiState.value = _uiState.value.copy(
            pendingExercisesForNewSession = exercises
        )
    }

    fun deleteSession(sessionId: Int) {
        val updated = _uiState.value.sessions.filterNot { it.id == sessionId }
        _uiState.value = _uiState.value.copy(sessions = updated)
    }

    fun getSessionById(sessionId: Int): Session? {
        return _uiState.value.sessions.firstOrNull { it.id == sessionId }
    }
}