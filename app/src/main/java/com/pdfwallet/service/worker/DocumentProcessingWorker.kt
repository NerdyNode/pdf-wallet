package com.pdfwallet.service.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.pdfwallet.data.db.DocumentDao
import com.pdfwallet.data.db.ProcessingStatus
import com.pdfwallet.service.classification.DocumentClassifier
import com.pdfwallet.service.pdf.BarcodeExtractor
import com.pdfwallet.service.pdf.OcrProcessor
import com.pdfwallet.service.pdf.TextExtractor
import com.pdfwallet.service.pdf.ThumbnailGenerator
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DocumentProcessingWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val documentDao: DocumentDao,
    private val textExtractor: TextExtractor,
    private val ocrProcessor: OcrProcessor,
    private val barcodeExtractor: BarcodeExtractor,
    private val documentClassifier: DocumentClassifier,
    private val thumbnailGenerator: ThumbnailGenerator
) : CoroutineWorker(context, params) {

    companion object {
        const val KEY_DOCUMENT_ID = "DOCUMENT_ID"
    }

    override suspend fun doWork(): Result {
        val docId = inputData.getLong(KEY_DOCUMENT_ID, -1)
        if (docId == -1L) return Result.failure()

        val document = documentDao.getById(docId) ?: return Result.failure()
        documentDao.updateProcessingStatus(docId, ProcessingStatus.PROCESSING)

        return try {
            val filePath = document.filePath
            
            // 1. Text Extraction
            var text = textExtractor.extractText(filePath)
            if (text.isNullOrBlank()) {
                text = ocrProcessor.extractTextFromBitmap(filePath)
            }
            val finalString = text ?: ""

            // 2. Barcode Extraction
            val barcodes = barcodeExtractor.extractBarcodes(filePath)

            // 3. Classification
            val classification = documentClassifier.classify(finalString, barcodes)

            // 4. Thumbnail
            val thumbnailPath = thumbnailGenerator.generateThumbnail(filePath)

            // 5. Update DB
            val updatedDoc = document.copy(
                processingStatus = ProcessingStatus.COMPLETE,
                thumbnailPath = thumbnailPath,
                rawOcrText = finalString,
                title = classification.title,
                documentType = classification.type,
                documentId = classification.documentId,
                holderName = classification.holderName,
                issueDate = classification.issueDate,
                expiryDate = classification.expiryDate,
                sourceLocation = classification.sourceLocation,
                destinationLocation = classification.destinationLocation
            )
            documentDao.insert(updatedDoc)
            
            // TODO: Fire notification

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            documentDao.updateProcessingStatus(docId, ProcessingStatus.FAILED)
            Result.retry()
        }
    }
}
