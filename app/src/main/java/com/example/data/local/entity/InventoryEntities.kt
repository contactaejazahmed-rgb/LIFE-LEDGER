package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "purchase_entries")
data class PurchaseEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val entryNumber: String,
    val dateEpoch: Long,
    val supplierName: String,
    val notebookId: Long,
    val notebookName: String,
    val quantity: Int,
    val unitCost: Double,
    val totalCost: Double,
    val batchNumber: String,
    val notes: String = ""
)

@Entity(tableName = "sales_entries")
data class SalesEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val invoiceNumber: String,
    val dateEpoch: Long,
    val schoolId: Long,
    val schoolName: String,
    val totalAmount: Double,
    val totalUnits: Int,
    val status: String, // Pending Dispatch, Dispatched, Delivered, Paid, Cancelled
    val paymentTerms: String, // Net 30 Days, Net 15 Days, Advance, Cash on Delivery
    val itemsSummary: String, // e.g. "Single-line 172p x 500, Practical Lab x 200"
    val itemsJson: String, // Detailed JSON string of items
    val notes: String = ""
)
