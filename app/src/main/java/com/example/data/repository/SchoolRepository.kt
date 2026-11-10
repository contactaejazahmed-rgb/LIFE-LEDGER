package com.example.data.repository

import com.example.data.local.dao.SchoolDao
import com.example.data.local.entity.SchoolEntity
import com.example.data.local.entity.SchoolPriceOverrideEntity
import kotlinx.coroutines.flow.Flow

class SchoolRepository(private val schoolDao: SchoolDao) {
    val allSchools: Flow<List<SchoolEntity>> = schoolDao.getAllSchools()
    val schoolCount: Flow<Int> = schoolDao.getSchoolCount()
    val verifiedSchoolCount: Flow<Int> = schoolDao.getVerifiedSchoolCount()

    suspend fun getSchoolById(id: Long): SchoolEntity? =
        schoolDao.getSchoolById(id)

    suspend fun insertSchool(school: SchoolEntity): Long =
        schoolDao.insertSchool(school)

    suspend fun updateSchool(school: SchoolEntity) =
        schoolDao.updateSchool(school)

    suspend fun deleteSchool(school: SchoolEntity) =
        schoolDao.deleteSchool(school)

    fun getPriceOverridesForSchool(schoolId: Long): Flow<List<SchoolPriceOverrideEntity>> =
        schoolDao.getPriceOverridesForSchool(schoolId)

    suspend fun getPriceOverride(schoolId: Long, notebookId: Long): SchoolPriceOverrideEntity? =
        schoolDao.getPriceOverride(schoolId, notebookId)

    suspend fun savePriceOverride(override: SchoolPriceOverrideEntity) =
        schoolDao.insertOrUpdatePriceOverride(override)

    suspend fun deletePriceOverride(schoolId: Long, notebookId: Long) =
        schoolDao.deletePriceOverride(schoolId, notebookId)
}
