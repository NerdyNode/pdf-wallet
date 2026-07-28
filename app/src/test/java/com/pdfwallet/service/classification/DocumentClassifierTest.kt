package com.pdfwallet.service.classification

import com.pdfwallet.data.db.DocumentType
import org.junit.Assert.assertEquals
import org.junit.Test

class DocumentClassifierTest {

    private val classifier = DocumentClassifier()

    @Test
    fun classify_Airline() {
        val text = "PNR: ABC123\nFlight 6E401\nBoarding Pass"
        val result = classifier.classify(text, emptyList())
        assertEquals(DocumentType.AIRLINE, result.type)
        assertEquals("ABC123", result.documentId)
        assertEquals("Flight Ticket - ABC123", result.title)
    }

    @Test
    fun classify_Train() {
        val text = "IRCTC e-ticket\nPNR No: 1234567890\nTrain No: 12001"
        val result = classifier.classify(text, emptyList())
        assertEquals(DocumentType.TRAIN, result.type)
        assertEquals("1234567890", result.documentId)
        assertEquals("Train Ticket - 1234567890", result.title)
    }

    @Test
    fun classify_GovernmentId() {
        val text = "Aadhaar Card\nUIDAI\nRepublic of India"
        val result = classifier.classify(text, emptyList())
        assertEquals(DocumentType.GOVERNMENT_ID, result.type)
        assertEquals("Government ID", result.title)
    }

    @Test
    fun classify_Bus() {
        val text = "RedBus ticket\nBoarding at 9:00 PM\nOperator: KSRTC"
        val result = classifier.classify(text, emptyList())
        assertEquals(DocumentType.BUS, result.type)
        assertEquals("Bus Pass", result.title)
    }

    @Test
    fun classify_Other() {
        val text = "Just some random text\nNo keywords here"
        val result = classifier.classify(text, emptyList())
        assertEquals(DocumentType.OTHER, result.type)
        assertEquals("Imported Document", result.title)
    }
}
