package com.pdfwallet.ui.home

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pdfwallet.ui.wallet.DocumentCard
import com.pdfwallet.ui.wallet.WalletViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToWallet: () -> Unit,
    viewModel: WalletViewModel = hiltViewModel()
) {
    val documents by viewModel.documents.collectAsState(initial = emptyList())
    val recentDocs = documents.take(5)

    val pdfPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.importManualDocument(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("PDF Wallet") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { pdfPickerLauncher.launch("application/pdf") }) {
                Icon(Icons.Default.Add, contentDescription = "Add PDF")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text(
                "Recent Documents",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recentDocs) { doc ->
                    DocumentCard(doc = doc)
                }
            }
            if (documents.size > 5) {
                Button(
                    onClick = onNavigateToWallet,
                    modifier = Modifier.padding(16.dp).fillMaxWidth()
                ) {
                    Text("View All Documents")
                }
            }
        }
    }
}
