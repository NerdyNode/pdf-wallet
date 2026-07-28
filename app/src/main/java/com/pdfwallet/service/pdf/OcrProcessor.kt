package com.pdfwallet.service.pdf

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OcrProcessor @Inject constructor() {
    
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    suspend fun extractTextFromBitmap(pdfPath: String): String? = withContext(Dispatchers.IO) {
        val file = File(pdfPath)
        if (!file.exists()) return@withContext null

        var descriptor: ParcelFileDescriptor? = null
        var renderer: PdfRenderer? = null
        var page: PdfRenderer.Page? = null

        try {
            descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            renderer = PdfRenderer(descriptor)
            if (renderer.pageCount <= 0) return@withContext null

            // For OCR, render at higher resolution
            page = renderer.openPage(0)
            val density = 2 // x2 scale for better OCR
            val bitmap = Bitmap.createBitmap(
                page.width * density,
                page.height * density,
                Bitmap.Config.ARGB_8888
            )
            bitmap.eraseColor(android.graphics.Color.WHITE)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            val image = InputImage.fromBitmap(bitmap, 0)
            val result = recognizer.process(image).await()
            bitmap.recycle()
            
            return@withContext result.text
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        } finally {
            page?.close()
            renderer?.close()
            descriptor?.close()
        }
    }
}
