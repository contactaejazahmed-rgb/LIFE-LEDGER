package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.NotebookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotebookDao {
    @Query("SELECT * FROM notebooks ORDER BY name ASC")
    fun getAllNotebooks(): Flow<List<NotebookEntity>>

    @Query("SELECT * FROM notebooks WHERE id = :id LIMIT 1")
    suspend fun getNotebookById(id: Long): NotebookEntity?

    @Query("SELECT * FROM notebooks WHERE currentStock <= minStockThreshold ORDER BY currentStock ASC")
    fun getLowStockNotebooks(): Flow<List<NotebookEntity>>

    @Query("SELECT * FROM notebooks WHERE rulingType = :rulingType ORDER BY name ASC")
    fun getNotebooksByRuling(rulingType: String): Flow<List<NotebookEntity>>

    @Query("SELECT COUNT(*) FROM notebooks WHERE currentStock <= minStockThreshold")
    fun getLowStockCount(): Flow<Int>

    @Query("SELECT SUM(currentStock) FROM notebooks")
    fun getTotalStockUnits(): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotebook(notebook: NotebookEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notebooks: List<NotebookEntity>)

    @Update
    suspend fun updateNotebook(notebook: NotebookEntity)

    @Query("UPDATE notebooks SET currentStock = currentStock + :quantity WHERE id = :notebookId")
    suspend fun addStock(notebookId: Long, quantity: Int)

    @Query("UPDATE notebooks SET currentStock = MAX(0, currentStock - :quantity) WHERE id = :notebookId")
    suspend fun deductStock(notebookId: Long, quantity: Int)

    @Delete
    suspend fun deleteNotebook(notebook: NotebookEntity)
}
