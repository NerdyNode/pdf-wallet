package com.pdfwallet.service.pdf

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BarcodeExtractor @Inject constructor() {

    private val scanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_AZTEC,
                Barcode.FORMAT_PDF417,
                Barcode.FORMAT_CODE_128
            )
            .build()
    )

    suspend fun extractBarcodes(pdfPath: String): List<String> = withContext(Dispatchers.IO) {
        val file = File(pdfPath)
        if (!file.exists()) return@withContext emptyList()

        var descriptor: ParcelFileDescriptor? = null
        var renderer: PdfRenderer? = null
        var page: PdfRenderer.Page? = null

        try {
            descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            renderer = PdfRenderer(descriptor)
            if (renderer.pageCount <= 0) return@withContext emptyList()

            page = renderer.openPage(0)
            val bitmap = Bitmap.createBitmap(
                page.width * 2,
                page.height * 2,
                Bitmap.Config.ARGB_8888
            )
            bitmap.eraseColor(android.graphics.Color.WHITE)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            val image = InputImage.fromBitmap(bitmap, 0)
            val barcodes = scanner.process(image).await()
            bitmap.recycle()

            return@withContext barcodes.mapNotNull { it.rawValue }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext emptyList()
        } finally {
            page?.close()
            renderer?.close()
            descriptor?.close()
        }
    }
}
