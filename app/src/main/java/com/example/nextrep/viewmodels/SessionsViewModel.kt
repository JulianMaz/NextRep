package com.example.nextrep.viewmodels

import androidx.lifecycle.ViewModel
import com.example.nextrep.models.Session
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Represents the UI state for sessions.
 */
data class SessionsUiState(
    val sessions: List<Session> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * ViewModel for managing sessions.
 */
class SessionsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SessionsUiState())
    val uiState: StateFlow<SessionsUiState> = _uiState.asStateFlow()

    private var nextId = 1

    /**
     * Adds a new session to the state.
     */
    fun addSession(session: Session) {
        val newSession = session.copy(id = nextId++)
        val updatedSessions = _uiState.value.sessions + newSession

        _uiState.value = _uiState.value.copy(
            sessions = updatedSessions,
            errorMessage = null
        )
    }

    /**
     * Deletes a session by ID.
     */
    fun deleteSession(sessionId: Int) {
        val updated = _uiState.value.sessions.filterNot { it.id == sessionId }
        _uiState.value = _uiState.value.copy(sessions = updated)
    }

    /**
     * Optional helper if you want to get a session detail later.
     */
    fun getSessionById(sessionId: Int): Session? {
        return _uiState.value.sessions.firstOrNull { it.id == sessionId }
    }
}