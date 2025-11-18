package com.example.nextrep.viewmodels

import androidx.lifecycle.ViewModel
import com.example.nextrep.models.Session
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * A data class to represent the state of the SessionsList screen.
 * This makes state management more predictable and scalable.
 */
data class SessionsUiState(
    val sessions: List<Session> = emptyList(),
    val isLoading: Boolean = false
    // You can add other state properties here, like an error message
)


/**
 * ViewModel for the SessionsListPage.
 * It holds and manages UI-related data in a lifecycle-conscious way, separate from the UI.
 */
class SessionsViewModel : ViewModel() {
    // The private mutable state flow that can be changed only within the ViewModel.
    private val _uiState = MutableStateFlow(SessionsUiState())

    // The public, read-only state flow that the UI can observe.
    val uiState: StateFlow<SessionsUiState> = _uiState.asStateFlow()

    private var nextId = 1

    fun addSession(session: Session) {
        val newSession = session.copy(id = nextId++)
        val updated = _uiState.value.sessions + newSession

        _uiState.value = _uiState.value.copy(
            sessions = updated
        )
    }
    // The init block is called when the ViewModel is first created.
    init {
        // loadSessions()
    }

    /**
     * Loads the sessions. In a real app, this would fetch data from a repository
     * which in turn gets it from a database or a network API.
     */
//    private fun loadSessions() {
//        // You can set a loading state here if the operation takes time
//        // _uiState.value = _uiState.value.copy(isLoading = true)
//
//        // --- Example Data for Demonstration ---
//        // Replace this with your actual data fetching logic from your repository.
//        val exampleSessions = List(5) { i ->
//            Session(
//                id = i,
//                name = "Workout Session ${i + 1}",
//                date = "2025-11-1${i}",
//                exercises = emptyList() // Assuming Session model has this property
//            )
//        }
//
//        // Update the state with the loaded sessions.
//        _uiState.value = SessionsUiState(sessions = exampleSessions, isLoading = false)
//    }

    // You can add other functions here to handle user actions, for example:


    fun deleteSession(sessionId: Int) {
        // Logic to delete a session...
        // After deleting, you would call loadSessions() again.
    }
}
