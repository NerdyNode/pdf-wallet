package com.pdfwallet.ui.wallet

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.pdfwallet.data.db.Document
import com.pdfwallet.data.db.ProcessingStatus
import java.io.File

@Composable
fun DocumentCard(doc: Document) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (doc.thumbnailPath != null) {
                AsyncImage(
                    model = File(doc.thumbnailPath),
                    contentDescription = "Document Thumbnail",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(60.dp, 84.dp)
                )
            } else {
                Box(
                    modifier = Modifier.size(60.dp, 84.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("PDF")
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = doc.title, style = MaterialTheme.typography.titleMedium)
                doc.holderName?.let {
                    Text(text = it, style = MaterialTheme.typography.bodyMedium)
                }
                Text(text = doc.documentType.name, style = MaterialTheme.typography.labelSmall)
            }

            when (doc.processingStatus) {
                ProcessingStatus.PENDING, ProcessingStatus.PROCESSING -> {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
                ProcessingStatus.FAILED -> {
                    Text("Failed", color = MaterialTheme.colorScheme.error)
                }
                ProcessingStatus.COMPLETE -> {
                    // Nothing extra
                }
            }
        }
    }
}
