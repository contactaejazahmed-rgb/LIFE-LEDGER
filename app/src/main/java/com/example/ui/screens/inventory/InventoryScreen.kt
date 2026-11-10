package com.example.ui.screens.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.NotebookEntity
import com.example.data.local.entity.PurchaseEntryEntity
import com.example.ui.MainViewModel
import com.example.ui.components.MetricStatCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.StatusBadgeType
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AlertRedContainer
import com.example.ui.theme.EduGoldContainer
import com.example.ui.theme.EduGoldTertiary
import com.example.ui.theme.EduNavyContainer
import com.example.ui.theme.EduNavyPrimary
import com.example.ui.theme.EduTealContainer
import com.example.ui.theme.EduTealSecondary
import com.example.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InventoryScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val allNotebooks by viewModel.allNotebooks.collectAsStateWithLifecycle()
    val lowStockNotebooks by viewModel.lowStockNotebooks.collectAsStateWithLifecycle()
    val lowStockCount by viewModel.lowStockCount.collectAsStateWithLifecycle()
    val totalUnits by viewModel.totalStockUnits.collectAsStateWithLifecycle()
    val purchaseEntries by viewModel.allPurchaseEntries.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showPurchaseDialog by remember { mutableStateOf(false) }
    var notebookToRestock by remember { mutableStateOf<NotebookEntity?>(null) }
    var filterOnlyLowStock by remember { mutableStateOf(false) }

    val displayedNotebooks = if (filterOnlyLowStock) lowStockNotebooks else allNotebooks

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    notebookToRestock = null
                    showPurchaseDialog = true
                },
                containerColor = EduNavyPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_purchase_entry_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Purchase Entry")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Header Title
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Warehouse Inventory & Restock",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Real-time stock availability, supplier purchase logs & low-stock alerts",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Metrics Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricStatCard(
                        title = "Total Physical Stock",
                        value = "${totalUnits / 1000}k",
                        subtitle = "$totalUnits total books",
                        icon = Icons.Default.Inventory,
                        iconTint = EduNavyPrimary,
                        iconBackground = EduNavyContainer,
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        title = "Low Stock Alerts",
                        value = "$lowStockCount",
                        subtitle = "Below reorder level",
                        icon = Icons.Default.Warning,
                        iconTint = if (lowStockCount > 0) AlertRed else SuccessGreen,
                        iconBackground = if (lowStockCount > 0) AlertRedContainer else Color(0xFFDCFCE7),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedTabIndex = 0
                            filterOnlyLowStock = true
                        }
                    )
                    MetricStatCard(
                        title = "Inward Batches",
                        value = "${purchaseEntries.size}",
                        subtitle = "Recorded purchases",
                        icon = Icons.Default.LocalShipping,
                        iconTint = EduTealSecondary,
                        iconBackground = EduTealContainer,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedTabIndex = 1 }
                    )
                }
            }

            // Low Stock Notification Alert Banner
            if (lowStockCount > 0) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .testTag("low_stock_notification_banner"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = AlertRedContainer)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = AlertRed,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "LOW-STOCK ALERT ($lowStockCount items need restock)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AlertRed
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "The following notebooks have reached or dropped below their minimum replenishment threshold. Replenish with mill purchase entries before school orders are delayed:",
                                fontSize = 11.sp,
                                color = Color(0xFF7F1D1D),
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // List of low items
                            lowStockNotebooks.take(3).forEach { nb ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = nb.name,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF7F1D1D)
                                        )
                                        Text(
                                            text = "${nb.currentStock} units remaining (Threshold: ${nb.minStockThreshold})",
                                            fontSize = 10.sp,
                                            color = AlertRed
                                        )
                                    }
                                    Button(
                                        onClick = {
                                            notebookToRestock = nb
                                            showPurchaseDialog = true
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("Restock", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Tab Row (Stock Availability vs Purchase Entries)
            item {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = { Text("Stock Availability") },
                        icon = { Icon(Icons.Default.Inventory, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("Purchase Entries (${purchaseEntries.size})") },
                        icon = { Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }
            }

            // Tab 0: Stock Availability
            if (selectedTabIndex == 0) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (filterOnlyLowStock) "Showing Low-Stock Items Only" else "All Inventory Items (${displayedNotebooks.size})",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        FilterChip(
                            selected = filterOnlyLowStock,
                            onClick = { filterOnlyLowStock = !filterOnlyLowStock },
                            label = { Text("Low Stock Only", fontSize = 11.sp) }
                        )
                    }
                }

                items(displayedNotebooks, key = { it.id }) { notebook ->
                    StockItemCard(
                        notebook = notebook,
                        onRestock = {
                            notebookToRestock = notebook
                            showPurchaseDialog = true
                        }
                    )
                }
            } else {
                // Tab 1: Purchase Entries
                if (purchaseEntries.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No purchase entries recorded yet.\nTap + to record stock inward from mills.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    items(purchaseEntries, key = { it.id }) { entry ->
                        PurchaseEntryCard(entry = entry)
                    }
                }
            }
        }
    }

    if (showPurchaseDialog) {
        AddPurchaseEntryDialog(
            notebooks = allNotebooks,
            preselectedNotebook = notebookToRestock,
            onDismiss = {
                showPurchaseDialog = false
                notebookToRestock = null
            },
            onSavePurchase = { supplier, nb, qty, unitCost, batch, notes ->
                viewModel.recordPurchase(
                    supplierName = supplier,
                    notebook = nb,
                    quantity = qty,
                    unitCost = unitCost,
                    batchNumber = batch,
                    notes = notes
                )
                showPurchaseDialog = false
                notebookToRestock = null
            }
        )
    }
}

@Composable
private fun StockItemCard(
    notebook: NotebookEntity,
    onRestock: () -> Unit
) {
    val isLowStock = notebook.currentStock <= notebook.minStockThreshold
    val cartons = notebook.currentStock / notebook.unitsPerCarton
    val cartonRemainder = notebook.currentStock % notebook.unitsPerCarton
    val progress = (notebook.currentStock.toFloat() / (notebook.minStockThreshold * 3).coerceAtLeast(100)).coerceIn(0f, 1f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("stock_item_${notebook.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(
                    text = notebook.rulingType,
                    type = if (isLowStock) StatusBadgeType.ERROR else StatusBadgeType.INFO
                )
                Text(
                    text = "Threshold: ${notebook.minStockThreshold} units",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = notebook.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${notebook.size} • ${notebook.pageCount} Pages • SKU: ${notebook.sku}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Units & Cartons Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${notebook.currentStock}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLowStock) AlertRed else EduNavyPrimary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "units in stock",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "Equivalent to $cartons cartons (${notebook.unitsPerCarton}/ctn)${if (cartonRemainder > 0) " + $cartonRemainder units" else ""}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = onRestock,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLowStock) AlertRed else EduNavyPrimary
                    ),
                    modifier = Modifier.testTag("restock_btn_${notebook.id}")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Restock")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (isLowStock) AlertRed else SuccessGreen,
                trackColor = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}

@Composable
private fun PurchaseEntryCard(entry: PurchaseEntryEntity) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(entry.dateEpoch))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(text = entry.entryNumber, type = StatusBadgeType.INFO)
                Text(
                    text = formattedDate,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = entry.notebookName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Supplier: ${entry.supplierName} • Batch: ${entry.batchNumber}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Quantity Inward:",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "+${entry.quantity} units",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                }
                Column {
                    Text(
                        text = "Unit Cost:",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "₹${"%.2f".format(entry.unitCost)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Total Inward Cost:",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "₹${"%.2f".format(entry.totalCost)}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = EduNavyPrimary
                    )
                }
            }

            if (entry.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Notes: ${entry.notes}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
