package com.pdfwallet.service.pdf

import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TextExtractor @Inject constructor() {

    suspend fun extractText(pdfPath: String): String? = withContext(Dispatchers.IO) {
        var document: PDDocument? = null
        try {
            val file = File(pdfPath)
            if (!file.exists()) return@withContext null

            document = PDDocument.load(file)
            val stripper = PDFTextStripper()
            // Optional: limit to first few pages to speed up
            stripper.startPage = 1
            stripper.endPage = minOf(3, document.numberOfPages)
            
            val text = stripper.getText(document)
            
            // If text is very short, it's likely a scanned image without a text layer
            if (text.trim().length < 50) return@withContext null
            
            return@withContext text
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        } finally {
            document?.close()
        }
    }
}
