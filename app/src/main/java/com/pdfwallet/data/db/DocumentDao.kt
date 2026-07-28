package com.pdfwallet.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(document: Document): Long

    @Query("SELECT * FROM documents ORDER BY importDate DESC")
    fun getAllDocuments(): Flow<List<Document>>

    @Query("SELECT * FROM documents WHERE documentType = :type ORDER BY importDate DESC")
    fun getByType(type: DocumentType): Flow<List<Document>>

    @Query("SELECT * FROM documents WHERE rawOcrText LIKE :query OR holderName LIKE :query OR documentId LIKE :query OR title LIKE :query ORDER BY importDate DESC")
    fun search(query: String): Flow<List<Document>>

    @Delete
    suspend fun delete(document: Document)

    @Query("SELECT * FROM documents WHERE id = :id")
    suspend fun getById(id: Long): Document?

    @Query("SELECT * FROM documents WHERE contentHash = :hash LIMIT 1")
    suspend fun getByContentHash(hash: String): Document?

    @Query("UPDATE documents SET processingStatus = :status WHERE id = :id")
    suspend fun updateProcessingStatus(id: Long, status: ProcessingStatus)
}
