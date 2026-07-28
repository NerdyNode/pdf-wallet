package com.pdfwallet.ui.capture

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.pdfwallet.data.db.CaptureSource
import com.pdfwallet.data.repository.DocumentRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ShareReceiverActivity : ComponentActivity() {
    
    @Inject
    lateinit var repository: DocumentRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        when (intent?.action) {
            Intent.ACTION_SEND -> {
                if ("application/pdf" == intent.type) {
                    val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
                    uri?.let { handlePdf(it) }
                }
            }
            Intent.ACTION_SEND_MULTIPLE -> {
                if ("application/pdf" == intent.type) {
                    val uris = intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)
                    uris?.forEach { handlePdf(it) }
                }
            }
        }
        
        Toast.makeText(this, "Added to wallet â€” processing...", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun handlePdf(uri: Uri) {
        lifecycleScope.launch {
            try {
                repository.captureDocument(uri, CaptureSource.SHARE_INTENT)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
