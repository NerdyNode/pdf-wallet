package com.pdfwallet.ui.wallet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pdfwallet.data.db.DocumentType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    onNavigateBack: () -> Unit,
    viewModel: WalletViewModel = hiltViewModel()
) {
    val documents by viewModel.documents.collectAsState(initial = emptyList())
    var selectedFilter by remember { mutableStateOf<DocumentType?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredDocs = documents.filter { doc ->
        val matchesFilter = selectedFilter == null || doc.documentType == selectedFilter
        val matchesSearch = doc.title.contains(searchQuery, ignoreCase = true) ||
                            doc.holderName?.contains(searchQuery, ignoreCase = true) == true ||
                            doc.documentId?.contains(searchQuery, ignoreCase = true) == true
        matchesFilter && matchesSearch
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Documents") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search") },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            )
            
            // Filters
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { selectedFilter = null },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = selectedFilter == DocumentType.AIRLINE,
                    onClick = { selectedFilter = DocumentType.AIRLINE },
                    label = { Text("Flights") }
                )
                FilterChip(
                    selected = selectedFilter == DocumentType.GOVERNMENT_ID,
                    onClick = { selectedFilter = DocumentType.GOVERNMENT_ID },
                    label = { Text("Gov ID") }
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredDocs) { doc ->
                    DocumentCard(doc = doc)
                }
            }
        }
    }
}
