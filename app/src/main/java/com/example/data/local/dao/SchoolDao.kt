package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.SchoolEntity
import com.example.data.local.entity.SchoolPriceOverrideEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SchoolDao {
    @Query("SELECT * FROM schools ORDER BY name ASC")
    fun getAllSchools(): Flow<List<SchoolEntity>>

    @Query("SELECT * FROM schools WHERE id = :id LIMIT 1")
    suspend fun getSchoolById(id: Long): SchoolEntity?

    @Query("SELECT COUNT(*) FROM schools")
    fun getSchoolCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM schools WHERE verificationStatus = 'Verified' OR verificationStatus = 'Premium Partner'")
    fun getVerifiedSchoolCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchool(school: SchoolEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSchools(schools: List<SchoolEntity>)

    @Update
    suspend fun updateSchool(school: SchoolEntity)

    @Delete
    suspend fun deleteSchool(school: SchoolEntity)

    // Price overrides
    @Query("SELECT * FROM school_price_overrides WHERE schoolId = :schoolId")
    fun getPriceOverridesForSchool(schoolId: Long): Flow<List<SchoolPriceOverrideEntity>>

    @Query("SELECT * FROM school_price_overrides WHERE schoolId = :schoolId AND notebookId = :notebookId LIMIT 1")
    suspend fun getPriceOverride(schoolId: Long, notebookId: Long): SchoolPriceOverrideEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePriceOverride(override: SchoolPriceOverrideEntity)

    @Query("DELETE FROM school_price_overrides WHERE schoolId = :schoolId AND notebookId = :notebookId")
    suspend fun deletePriceOverride(schoolId: Long, notebookId: Long)
}
