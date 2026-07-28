package com.pdfwallet.service.classification

import com.pdfwallet.data.db.DocumentType
import javax.inject.Inject
import javax.inject.Singleton

data class ClassificationResult(
    val type: DocumentType,
    val documentId: String? = null,
    val holderName: String? = null,
    val issueDate: String? = null,
    val expiryDate: String? = null,
    val sourceLocation: String? = null,
    val destinationLocation: String? = null,
    val title: String
)

@Singleton
class DocumentClassifier @Inject constructor() {

    fun classify(text: String, barcodes: List<String>): ClassificationResult {
        val lowerText = text.lowercase()
        
        // Simple heuristic rules for Phase 1
        
        // Check for Airline
        if (lowerText.contains("boarding pass") || lowerText.contains("flight") || lowerText.contains("pnr") || lowerText.contains("terminal")) {
            // Attempt to parse PNR
            val pnrRegex = "(?:pnr|booking ref)[:\\s]*([A-Z0-9]{6})".toRegex(RegexOption.IGNORE_CASE)
            val pnr = pnrRegex.find(text)?.groupValues?.get(1)
            
            return ClassificationResult(
                type = DocumentType.AIRLINE,
                documentId = pnr,
                title = "Flight Ticket" + (if (pnr != null) " - $pnr" else "")
            )
        }
        
        // Check for Train
        if (lowerText.contains("irctc") || lowerText.contains("train no") || lowerText.contains("coach")) {
            val pnrRegex = "pnr\\s*(?:no[:\\.])?\\s*(\\d{10})".toRegex(RegexOption.IGNORE_CASE)
            val pnr = pnrRegex.find(text)?.groupValues?.get(1)
            return ClassificationResult(
                type = DocumentType.TRAIN,
                documentId = pnr,
                title = "Train Ticket" + (if (pnr != null) " - $pnr" else "")
            )
        }
        
        // Check for Government ID
        if (lowerText.contains("aadhaar") || lowerText.contains("passport") || lowerText.contains("republic of india") || lowerText.contains("uidai")) {
            return ClassificationResult(
                type = DocumentType.GOVERNMENT_ID,
                title = "Government ID"
            )
        }
        
        // Check for Bus
        if (lowerText.contains("bus") && (lowerText.contains("boarding") || lowerText.contains("redbus") || lowerText.contains("operator"))) {
             return ClassificationResult(
                type = DocumentType.BUS,
                title = "Bus Pass"
            )
        }

        return ClassificationResult(
            type = DocumentType.OTHER,
            title = "Imported Document"
        )
    }
}
