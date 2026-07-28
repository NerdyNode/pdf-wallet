package com.pdfwallet.service.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThumbnailGenerator @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun generateThumbnail(pdfPath: String): String? = withContext(Dispatchers.IO) {
        val file = File(pdfPath)
        if (!file.exists()) return@withContext null

        var descriptor: ParcelFileDescriptor? = null
        var renderer: PdfRenderer? = null
        var page: PdfRenderer.Page? = null

        try {
            descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            renderer = PdfRenderer(descriptor)
            if (renderer.pageCount <= 0) return@withContext null

            page = renderer.openPage(0)
            
            // Render to 200x280 (aspect ratio ~ 1:1.4)
            val bitmap = Bitmap.createBitmap(200, 280, Bitmap.Config.ARGB_8888)
            // Fill with white background
            bitmap.eraseColor(android.graphics.Color.WHITE)
            
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            
            val cacheDir = File(context.cacheDir, "thumbnails").apply { mkdirs() }
            val thumbFile = File(cacheDir, "${UUID.randomUUID()}.jpg")
            
            FileOutputStream(thumbFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            bitmap.recycle()
            
            return@withContext thumbFile.absolutePath
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
