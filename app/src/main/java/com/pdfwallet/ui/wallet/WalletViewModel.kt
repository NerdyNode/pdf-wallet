package com.pdfwallet.ui.wallet

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdfwallet.data.db.CaptureSource
import com.pdfwallet.data.repository.DocumentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val repository: DocumentRepository
) : ViewModel() {

    val documents = repository.allDocuments

    fun importManualDocument(uri: Uri) {
        viewModelScope.launch {
            repository.captureDocument(uri, CaptureSource.MANUAL_IMPORT)
        }
    }
}
