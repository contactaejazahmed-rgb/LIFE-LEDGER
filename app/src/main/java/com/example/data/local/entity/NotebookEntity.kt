package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notebooks")
data class NotebookEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sku: String,
    val name: String,
    val rulingType: String, // Single-line, Four-line, Ruled (Double-line), Square Grid, Plain, Long Book, Practical Book
    val size: String,       // A4, Crown Long, Regular, Small
    val pageCount: Int,     // 80, 120, 172, 240, 300
    val bindingType: String,// Soft Bound, Hard Bound, Spiral, Center Stapled
    val paperGsm: Int,      // 58, 64, 70, 80 GSM
    val wholesalePrice: Double,
    val mrpPrice: Double,
    val unitsPerCarton: Int,
    val currentStock: Int,
    val minStockThreshold: Int = 100,
    val description: String = ""
)
