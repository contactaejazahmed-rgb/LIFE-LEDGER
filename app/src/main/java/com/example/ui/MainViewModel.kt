package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.NotebookEntity
import com.example.data.local.entity.PurchaseEntryEntity
import com.example.data.local.entity.SalesEntryEntity
import com.example.data.local.entity.SchoolEntity
import com.example.data.local.entity.SchoolPriceOverrideEntity
import com.example.data.repository.InventoryRepository
import com.example.data.repository.NotebookRepository
import com.example.data.repository.SchoolRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val notebookRepository = NotebookRepository(database.notebookDao())
    private val schoolRepository = SchoolRepository(database.schoolDao())
    private val inventoryRepository = InventoryRepository(database.inventoryDao(), database.notebookDao())

    // Raw data flows
    val allNotebooks: StateFlow<List<NotebookEntity>> = notebookRepository.allNotebooks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockNotebooks: StateFlow<List<NotebookEntity>> = notebookRepository.lowStockNotebooks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockCount: StateFlow<Int> = notebookRepository.lowStockCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalStockUnits: StateFlow<Int> = notebookRepository.totalStockUnits
        .combine(MutableStateFlow(0)) { units, _ -> units ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val allSchools: StateFlow<List<SchoolEntity>> = schoolRepository.allSchools
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val verifiedSchoolCount: StateFlow<Int> = schoolRepository.verifiedSchoolCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val allPurchaseEntries: StateFlow<List<PurchaseEntryEntity>> = inventoryRepository.allPurchaseEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSalesEntries: StateFlow<List<SalesEntryEntity>> = inventoryRepository.allSalesEntries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalRevenue: StateFlow<Double> = inventoryRepository.totalRevenue
        .combine(MutableStateFlow(0.0)) { rev, _ -> rev ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val pendingDispatchCount: StateFlow<Int> = inventoryRepository.pendingDispatchCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Catalogue filters
    val searchQuery = MutableStateFlow("")
    val selectedRulingFilter = MutableStateFlow("All")
    val selectedSizeFilter = MutableStateFlow("All")
    val selectedPageFilter = MutableStateFlow<Int?>(null)

    // Filtered Notebooks
    val filteredNotebooks: StateFlow<List<NotebookEntity>> = combine(
        allNotebooks,
        searchQuery,
        selectedRulingFilter,
        selectedSizeFilter,
        selectedPageFilter
    ) { list, query, ruling, size, pages ->
        list.filter { nb ->
            val matchesQuery = query.isBlank() ||
                nb.name.contains(query, ignoreCase = true) ||
                nb.sku.contains(query, ignoreCase = true) ||
                nb.rulingType.contains(query, ignoreCase = true) ||
                nb.description.contains(query, ignoreCase = true)

            val matchesRuling = ruling == "All" || nb.rulingType.contains(ruling, ignoreCase = true)
            val matchesSize = size == "All" || nb.size.contains(size, ignoreCase = true)
            val matchesPages = pages == null || nb.pageCount == pages

            matchesQuery && matchesRuling && matchesSize && matchesPages
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // School Search & Filter
    val schoolSearchQuery = MutableStateFlow("")
    val filteredSchools: StateFlow<List<SchoolEntity>> = combine(
        allSchools,
        schoolSearchQuery
    ) { schools, query ->
        if (query.isBlank()) schools
        else schools.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.schoolCode.contains(query, ignoreCase = true) ||
            it.city.contains(query, ignoreCase = true) ||
            it.affiliation.contains(query, ignoreCase = true) ||
            it.principalName.contains(query, ignoreCase = true) ||
            it.purchaseManagerName.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Actions: Notebooks
    fun addNotebook(notebook: NotebookEntity) {
        viewModelScope.launch {
            notebookRepository.insertNotebook(notebook)
        }
    }

    fun updateNotebook(notebook: NotebookEntity) {
        viewModelScope.launch {
            notebookRepository.updateNotebook(notebook)
        }
    }

    fun deleteNotebook(notebook: NotebookEntity) {
        viewModelScope.launch {
            notebookRepository.deleteNotebook(notebook)
        }
    }

    // Actions: Schools
    fun addSchool(school: SchoolEntity) {
        viewModelScope.launch {
            schoolRepository.insertSchool(school)
        }
    }

    fun updateSchool(school: SchoolEntity) {
        viewModelScope.launch {
            schoolRepository.updateSchool(school)
        }
    }

    fun deleteSchool(school: SchoolEntity) {
        viewModelScope.launch {
            schoolRepository.deleteSchool(school)
        }
    }

    fun getSchoolPriceOverrides(schoolId: Long): Flow<List<SchoolPriceOverrideEntity>> {
        return schoolRepository.getPriceOverridesForSchool(schoolId)
    }

    fun saveSchoolPriceOverride(schoolId: Long, notebookId: Long, customPrice: Double) {
        viewModelScope.launch {
            schoolRepository.savePriceOverride(
                SchoolPriceOverrideEntity(
                    schoolId = schoolId,
                    notebookId = notebookId,
                    customPrice = customPrice
                )
            )
        }
    }

    fun deleteSchoolPriceOverride(schoolId: Long, notebookId: Long) {
        viewModelScope.launch {
            schoolRepository.deletePriceOverride(schoolId, notebookId)
        }
    }

    // Actions: Inventory Purchase & Sales
    fun recordPurchase(
        supplierName: String,
        notebook: NotebookEntity,
        quantity: Int,
        unitCost: Double,
        batchNumber: String,
        notes: String
    ) {
        viewModelScope.launch {
            val entry = PurchaseEntryEntity(
                entryNumber = "PUR-${System.currentTimeMillis() % 100000}",
                dateEpoch = System.currentTimeMillis(),
                supplierName = supplierName.ifBlank { "Standard Mill Supply" },
                notebookId = notebook.id,
                notebookName = notebook.name,
                quantity = quantity,
                unitCost = unitCost,
                totalCost = quantity * unitCost,
                batchNumber = batchNumber.ifBlank { "BATCH-${System.currentTimeMillis() % 10000}" },
                notes = notes
            )
            inventoryRepository.recordPurchaseEntry(entry)
        }
    }

    fun recordSalesOrder(
        school: SchoolEntity,
        items: List<Triple<NotebookEntity, Int, Double>>, // notebook, quantity, agreed unit price
        paymentTerms: String,
        notes: String
    ) {
        viewModelScope.launch {
            val totalUnits = items.sumOf { it.second }
            val totalAmount = items.sumOf { it.second * it.third }
            val summary = items.joinToString(", ") { "${it.first.name} (x${it.second})" }
            val jsonItems = buildString {
                append("[")
                items.forEachIndexed { index, (nb, qty, price) ->
                    append("""{"name":"${nb.name.replace("\"", "\\\"")}","qty":$qty,"price":$price,"total":${qty * price}}""")
                    if (index < items.size - 1) append(",")
                }
                append("]")
            }

            val salesEntry = SalesEntryEntity(
                invoiceNumber = "INV-${school.schoolCode.takeLast(4)}-${System.currentTimeMillis() % 10000}",
                dateEpoch = System.currentTimeMillis(),
                schoolId = school.id,
                schoolName = school.name,
                totalAmount = totalAmount,
                totalUnits = totalUnits,
                status = "Pending Dispatch",
                paymentTerms = paymentTerms,
                itemsSummary = summary,
                itemsJson = jsonItems,
                notes = notes
            )

            val deductions = items.map { Pair(it.first.id, it.second) }
            inventoryRepository.recordSalesEntry(salesEntry, deductions)
        }
    }

    fun updateSalesOrderStatus(entry: SalesEntryEntity, newStatus: String) {
        viewModelScope.launch {
            inventoryRepository.updateSalesStatus(entry, newStatus)
        }
    }

    fun deleteSalesEntry(entry: SalesEntryEntity) {
        viewModelScope.launch {
            inventoryRepository.deleteSalesEntry(entry)
        }
    }
}
