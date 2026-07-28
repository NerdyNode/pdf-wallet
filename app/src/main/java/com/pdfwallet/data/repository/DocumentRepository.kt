package com.pdfwallet.data.repository

import android.net.Uri
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.pdfwallet.data.db.CaptureSource
import com.pdfwallet.data.db.Document
import com.pdfwallet.data.db.DocumentDao
import com.pdfwallet.data.db.ProcessingStatus
import com.pdfwallet.data.local.PdfFileManager
import com.pdfwallet.service.worker.DocumentProcessingWorker
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentRepository @Inject constructor(
    private val documentDao: DocumentDao,
    private val pdfFileManager: PdfFileManager,
    private val workManager: WorkManager
) {
    val allDocuments: Flow<List<Document>> = documentDao.getAllDocuments()

    suspend fun captureDocument(uri: Uri, source: CaptureSource) {
        val (filePath, contentHash) = pdfFileManager.copyToPrivateStorage(uri)
        
        // Deduplication
        val existingDoc = documentDao.getByContentHash(contentHash)
        if (existingDoc != null) {
            // Already processed this exact file
            return
        }

        val newDoc = Document(
            importDate = System.currentTimeMillis(),
            filePath = filePath,
            thumbnailPath = null,
            rawOcrText = null,
            title = "Processing...",
            documentId = null,
            holderName = null,
            issueDate = null,
            expiryDate = null,
            sourceLocation = null,
            destinationLocation = null,
            additionalMeta = null,
            processingStatus = ProcessingStatus.PENDING,
            contentHash = contentHash,
            captureSource = source
        )
        val docId = documentDao.insert(newDoc)

        val workRequest = OneTimeWorkRequestBuilder<DocumentProcessingWorker>()
            .setInputData(Data.Builder().putLong(DocumentProcessingWorker.KEY_DOCUMENT_ID, docId).build())
            .build()
        
        workManager.enqueue(workRequest)
    }
}
