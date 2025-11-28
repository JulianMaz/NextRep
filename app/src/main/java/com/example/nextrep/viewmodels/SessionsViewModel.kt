package com.example.nextrep.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nextrep.data.models.Session
import com.example.nextrep.data.session.SessionDao
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SessionsUiState(
    val sessions: List<Session> = emptyList()
)

class SessionsViewModel(
    private val sessionDao: SessionDao
) : ViewModel() {

    val uiState: StateFlow<SessionsUiState> =
        sessionDao.getSessions()
            .map { SessionsUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = SessionsUiState()
            )

    fun addSession(session: Session) {
        viewModelScope.launch {
            sessionDao.insertSession(session)
        }
    }

    fun deleteSession(session: Session) {
        viewModelScope.launch {
            sessionDao.deleteSession(session)
        }
    }
}
