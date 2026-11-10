package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schools")
data class SchoolEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val schoolCode: String,
    val name: String,
    val affiliation: String, // CBSE, ICSE, State Board, Cambridge, IB, etc.
    val address: String,
    val city: String,
    val state: String,
    val pincode: String,
    val principalName: String,
    val purchaseManagerName: String,
    val contactPhone: String,
    val contactEmail: String,
    val verificationStatus: String, // Verified, Pending Verification, Premium Partner
    val contractedDiscountPercent: Double = 10.0, // Base discount off wholesale
    val notes: String = ""
)

@Entity(
    tableName = "school_price_overrides",
    primaryKeys = ["schoolId", "notebookId"]
)
data class SchoolPriceOverrideEntity(
    val schoolId: Long,
    val notebookId: Long,
    val customPrice: Double
)
