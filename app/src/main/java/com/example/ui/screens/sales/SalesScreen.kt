package com.example.ui.screens.sales

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.SalesEntryEntity
import com.example.data.local.entity.SchoolEntity
import com.example.ui.MainViewModel
import com.example.ui.components.MetricStatCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.StatusBadgeType
import com.example.ui.theme.AlertRed
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
fun SalesScreen(
    viewModel: MainViewModel,
    preselectedSchoolForOrder: SchoolEntity? = null,
    onClearPreselectedSchool: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val salesEntries by viewModel.allSalesEntries.collectAsStateWithLifecycle()
    val schools by viewModel.allSchools.collectAsStateWithLifecycle()
    val notebooks by viewModel.allNotebooks.collectAsStateWithLifecycle()
    val totalRevenue by viewModel.totalRevenue.collectAsStateWithLifecycle()
    val pendingCount by viewModel.pendingDispatchCount.collectAsStateWithLifecycle()

    var selectedStatusFilter by remember { mutableStateOf("All") }
    var showCreateOrderDialog by remember { mutableStateOf(preselectedSchoolForOrder != null) }
    var selectedOrderForInvoice by remember { mutableStateOf<SalesEntryEntity?>(null) }

    val statusFilters = listOf("All", "Pending Dispatch", "Dispatched", "Delivered", "Paid")

    val filteredEntries = if (selectedStatusFilter == "All") {
        salesEntries
    } else {
        salesEntries.filter { it.status.equals(selectedStatusFilter, ignoreCase = true) }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateOrderDialog = true },
                containerColor = EduNavyPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("new_school_order_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "New School Order")
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
                        text = "School Orders & Invoicing",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Track B2B wholesale consignments, payment terms, and school invoices",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Metrics row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricStatCard(
                        title = "Total Revenue",
                        value = "₹${(totalRevenue / 1000).toInt()}k",
                        subtitle = "B2B gross sales",
                        icon = Icons.Default.Payments,
                        iconTint = SuccessGreen,
                        iconBackground = Color(0xFFDCFCE7),
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        title = "Total Orders",
                        value = "${salesEntries.size}",
                        subtitle = "Institutional bills",
                        icon = Icons.Default.Receipt,
                        iconTint = EduNavyPrimary,
                        iconBackground = EduNavyContainer,
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        title = "Pending Dispatch",
                        value = "$pendingCount",
                        subtitle = "In packing bay",
                        icon = Icons.Default.LocalShipping,
                        iconTint = if (pendingCount > 0) EduGoldTertiary else SuccessGreen,
                        iconBackground = EduGoldContainer,
                        modifier = Modifier.weight(1f),
                        onClick = { selectedStatusFilter = "Pending Dispatch" }
                    )
                }
            }

            // Status Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    statusFilters.forEach { status ->
                        FilterChip(
                            selected = selectedStatusFilter == status,
                            onClick = { selectedStatusFilter = status },
                            label = { Text(status, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EduNavyPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Orders List
            if (filteredEntries.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No orders match this status filter.\nTap + to create a new school order.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                items(filteredEntries, key = { it.id }) { entry ->
                    val matchedSchool = schools.find { it.id == entry.schoolId }
                    SalesEntryCard(
                        entry = entry,
                        school = matchedSchool,
                        onClick = { selectedOrderForInvoice = entry }
                    )
                }
            }
        }
    }

    // Create Order Dialog
    if (showCreateOrderDialog) {
        CreateSalesEntryDialog(
            schools = schools,
            notebooks = notebooks,
            preselectedSchool = preselectedSchoolForOrder,
            viewModel = viewModel,
            onDismiss = {
                showCreateOrderDialog = false
                onClearPreselectedSchool()
            },
            onSaveOrder = { school, items, terms, notes ->
                viewModel.recordSalesOrder(school, items, terms, notes)
                showCreateOrderDialog = false
                onClearPreselectedSchool()
            }
        )
    }

    // Invoice Detail Dialog
    selectedOrderForInvoice?.let { order ->
        val matchedSchool = schools.find { it.id == order.schoolId }
        InvoiceDetailDialog(
            entry = order,
            school = matchedSchool,
            onDismiss = { selectedOrderForInvoice = null },
            onUpdateStatus = { newStatus ->
                viewModel.updateSalesOrderStatus(order, newStatus)
                selectedOrderForInvoice = order.copy(status = newStatus)
            },
            onDeleteOrder = {
                viewModel.deleteSalesEntry(order)
                selectedOrderForInvoice = null
            }
        )
    }
}

@Composable
private fun SalesEntryCard(
    entry: SalesEntryEntity,
    school: SchoolEntity?,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(entry.dateEpoch))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() }
            .testTag("sales_card_${entry.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Invoice & Status Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(
                    text = entry.status,
                    type = when (entry.status) {
                        "Paid" -> StatusBadgeType.SUCCESS
                        "Delivered" -> StatusBadgeType.TEAL
                        "Dispatched" -> StatusBadgeType.INFO
                        "Pending Dispatch" -> StatusBadgeType.WARNING
                        else -> StatusBadgeType.ERROR
                    }
                )

                Text(
                    text = "$formattedDate • ${entry.invoiceNumber}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // School Name
            Text(
                text = entry.schoolName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Items summary
            Text(
                text = entry.itemsSummary,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                modifier = Modifier.padding(top = 2.dp)
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Amount, Units, Terms row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${entry.totalUnits} Units • Terms: ${entry.paymentTerms}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Tap to view invoice & dispatch details",
                        fontSize = 10.sp,
                        color = EduNavyPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "₹${"%.2f".format(entry.totalAmount)}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = EduNavyPrimary
                )
            }
        }
    }
}
