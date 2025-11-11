package com.example.nextrep.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nextrep.models.Session
import com.example.nextrep.ui.viewmodels.SessionsViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// --- ViewModel and State (Often in their own files) ---

/**
 * A data class to represent the state of the SessionsList screen.
 * This makes state management more predictable and scalable.
 */
data class SessionsUiState(
    val sessions: List<Session> = emptyList()
)

/**
 * ViewModel for the SessionsListPage.
 * It holds and manages UI-related data in a lifecycle-conscious way.
 */



// --- UI Composables ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionsListPage(
    sessionsViewModel: SessionsViewModel = viewModel(),
    onSessionClick: (Int) -> Unit,
    onAddSession: () -> Unit
) {
    // The UI observes the state from the ViewModel.
    // Any changes to the state will automatically trigger a recomposition.
    val uiState by sessionsViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workout Sessions") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddSession) {
                Icon(Icons.Default.Add, contentDescription = "Add Session")
            }
        }
    ) { innerPadding ->
        SessionListContent(
            sessions = uiState.sessions,
            onSessionClick = onSessionClick,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun SessionListContent(
    sessions: List<Session>,
    onSessionClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (sessions.isEmpty()) {
            item {
                Text(
                    text = "No sessions yet. Tap the '+' button to add a new one!",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                )
            }
        } else {
            items(sessions, key = { it.id }) { session ->
                SessionItem(session = session, onClick = { onSessionClick(session.id) })
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionItem(session: Session, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = session.name, style = MaterialTheme.typography.titleMedium)
            Text(text = "Date: ${session.date}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SessionsListContentPreview() {
    // 1. Create some fake data for the preview
    val previewSessions = listOf(
        Session(id = 1, name = "Morning Workout", date = "2025-11-10", exercises = emptyList()),
        Session(id = 2, name = "Leg Day", date = "2025-11-12", exercises = emptyList())
    )

    // 2. Wrap your content composable in your app's theme to see correct styling
    MaterialTheme { // Or your specific app theme if you have one
        SessionListContent(
            sessions = previewSessions,
            onSessionClick = {}, // Previews don't need real click logic
            modifier = Modifier.padding(PaddingValues(0.dp))
        )
    }
}

@Preview(showBackground = true, name = "Sessions List Empty")
@Composable
fun SessionsListContentEmptyPreview() {
    MaterialTheme {
        SessionListContent(
            sessions = emptyList(), // Also preview the empty state!
            onSessionClick = {},
            modifier = Modifier.padding(PaddingValues(0.dp))
        )
    }
}

