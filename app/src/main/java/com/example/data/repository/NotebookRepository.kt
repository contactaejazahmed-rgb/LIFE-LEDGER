package com.example.data.repository

import com.example.data.local.dao.NotebookDao
import com.example.data.local.entity.NotebookEntity
import kotlinx.coroutines.flow.Flow

class NotebookRepository(private val notebookDao: NotebookDao) {
    val allNotebooks: Flow<List<NotebookEntity>> = notebookDao.getAllNotebooks()
    val lowStockNotebooks: Flow<List<NotebookEntity>> = notebookDao.getLowStockNotebooks()
    val lowStockCount: Flow<Int> = notebookDao.getLowStockCount()
    val totalStockUnits: Flow<Int?> = notebookDao.getTotalStockUnits()

    fun getNotebooksByRuling(rulingType: String): Flow<List<NotebookEntity>> =
        notebookDao.getNotebooksByRuling(rulingType)

    suspend fun getNotebookById(id: Long): NotebookEntity? =
        notebookDao.getNotebookById(id)

    suspend fun insertNotebook(notebook: NotebookEntity): Long =
        notebookDao.insertNotebook(notebook)

    suspend fun updateNotebook(notebook: NotebookEntity) =
        notebookDao.updateNotebook(notebook)

    suspend fun deleteNotebook(notebook: NotebookEntity) =
        notebookDao.deleteNotebook(notebook)

    suspend fun addStock(notebookId: Long, quantity: Int) =
        notebookDao.addStock(notebookId, quantity)

    suspend fun deductStock(notebookId: Long, quantity: Int) =
        notebookDao.deductStock(notebookId, quantity)
}
