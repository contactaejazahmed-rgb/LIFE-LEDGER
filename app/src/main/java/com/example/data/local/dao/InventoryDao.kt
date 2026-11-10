package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.PurchaseEntryEntity
import com.example.data.local.entity.SalesEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    // Purchase entries
    @Query("SELECT * FROM purchase_entries ORDER BY dateEpoch DESC")
    fun getAllPurchaseEntries(): Flow<List<PurchaseEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchaseEntry(entry: PurchaseEntryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPurchaseEntries(entries: List<PurchaseEntryEntity>)

    @Delete
    suspend fun deletePurchaseEntry(entry: PurchaseEntryEntity)

    // Sales entries (B2B School Orders)
    @Query("SELECT * FROM sales_entries ORDER BY dateEpoch DESC")
    fun getAllSalesEntries(): Flow<List<SalesEntryEntity>>

    @Query("SELECT * FROM sales_entries WHERE schoolId = :schoolId ORDER BY dateEpoch DESC")
    fun getSalesEntriesForSchool(schoolId: Long): Flow<List<SalesEntryEntity>>

    @Query("SELECT * FROM sales_entries WHERE id = :id LIMIT 1")
    suspend fun getSalesEntryById(id: Long): SalesEntryEntity?

    @Query("SELECT SUM(totalAmount) FROM sales_entries WHERE status != 'Cancelled'")
    fun getTotalSalesRevenue(): Flow<Double?>

    @Query("SELECT COUNT(*) FROM sales_entries WHERE status = 'Pending Dispatch'")
    fun getPendingDispatchCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSalesEntry(entry: SalesEntryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSalesEntries(entries: List<SalesEntryEntity>)

    @Update
    suspend fun updateSalesEntry(entry: SalesEntryEntity)

    @Delete
    suspend fun deleteSalesEntry(entry: SalesEntryEntity)
}
