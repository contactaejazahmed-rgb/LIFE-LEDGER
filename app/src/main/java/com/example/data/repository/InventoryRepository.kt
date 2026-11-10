package com.example.data.repository

import com.example.data.local.dao.InventoryDao
import com.example.data.local.dao.NotebookDao
import com.example.data.local.entity.PurchaseEntryEntity
import com.example.data.local.entity.SalesEntryEntity
import kotlinx.coroutines.flow.Flow

class InventoryRepository(
    private val inventoryDao: InventoryDao,
    private val notebookDao: NotebookDao
) {
    val allPurchaseEntries: Flow<List<PurchaseEntryEntity>> = inventoryDao.getAllPurchaseEntries()
    val allSalesEntries: Flow<List<SalesEntryEntity>> = inventoryDao.getAllSalesEntries()
    val totalRevenue: Flow<Double?> = inventoryDao.getTotalSalesRevenue()
    val pendingDispatchCount: Flow<Int> = inventoryDao.getPendingDispatchCount()

    fun getSalesEntriesForSchool(schoolId: Long): Flow<List<SalesEntryEntity>> =
        inventoryDao.getSalesEntriesForSchool(schoolId)

    suspend fun recordPurchaseEntry(entry: PurchaseEntryEntity): Long {
        val entryId = inventoryDao.insertPurchaseEntry(entry)
        // Automatically add stock to notebook
        notebookDao.addStock(entry.notebookId, entry.quantity)
        return entryId
    }

    suspend fun recordSalesEntry(entry: SalesEntryEntity, itemDeductions: List<Pair<Long, Int>> = emptyList()): Long {
        val salesId = inventoryDao.insertSalesEntry(entry)
        // Deduct ordered quantities from inventory
        for ((notebookId, qty) in itemDeductions) {
            notebookDao.deductStock(notebookId, qty)
        }
        return salesId
    }

    suspend fun updateSalesStatus(entry: SalesEntryEntity, newStatus: String) {
        inventoryDao.updateSalesEntry(entry.copy(status = newStatus))
    }

    suspend fun deletePurchaseEntry(entry: PurchaseEntryEntity) {
        inventoryDao.deletePurchaseEntry(entry)
    }

    suspend fun deleteSalesEntry(entry: SalesEntryEntity) {
        inventoryDao.deleteSalesEntry(entry)
    }
}
