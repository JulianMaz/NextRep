package com.example.nextrep.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.nextrep.models.Session
import com.example.nextrep.viewmodels.SessionsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionsListPage(
    sessionsViewModel: SessionsViewModel,          // 🔹 ViewModel injecté par NextRepApp
    onSessionClick: (Int) -> Unit,
    onAddSession: () -> Unit,
) {
    val uiState by sessionsViewModel.uiState.collectAsState()   // 🔹 on observe CE viewModel, pas un autre

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddSession,
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Session") },
                text = { Text("New Session") },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {


            SessionListContent(
                sessions = uiState.sessions,                   // 🔹 liste venant du même ViewModel que SessionCreationPage
                onSessionClick = onSessionClick,
                modifier = Modifier.weight(1f)
            )
        }
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
                    text = "Aucune session pour l’instant.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                )
            }
        } else {
            items(sessions, key = { it.id }) { session ->
                Card(
                    onClick = { onSessionClick(session.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = session.name, style = MaterialTheme.typography.titleMedium)
                        Text(text = session.date, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}