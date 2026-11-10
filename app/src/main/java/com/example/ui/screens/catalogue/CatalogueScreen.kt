package com.example.ui.screens.catalogue

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.local.entity.NotebookEntity
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogueScreen(
    viewModel: MainViewModel,
    onNavigateToInventory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val notebooks by viewModel.filteredNotebooks.collectAsStateWithLifecycle()
    val totalSKUs by viewModel.allNotebooks.collectAsStateWithLifecycle()
    val totalUnits by viewModel.totalStockUnits.collectAsStateWithLifecycle()
    val lowStockCount by viewModel.lowStockCount.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedRuling by viewModel.selectedRulingFilter.collectAsStateWithLifecycle()
    val selectedSize by viewModel.selectedSizeFilter.collectAsStateWithLifecycle()
    val selectedPages by viewModel.selectedPageFilter.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var selectedNotebookForDetail by remember { mutableStateOf<NotebookEntity?>(null) }
    var notebookToEdit by remember { mutableStateOf<NotebookEntity?>(null) }

    val rulings = listOf("All", "Single-line", "Four-line", "Ruled", "Long Book", "Practical Book", "Plain")
    val sizes = listOf("All", "A4", "Crown Long", "Regular", "Small")
    val pageCounts = listOf(null, 80, 120, 172, 240, 300)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = EduNavyPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_notebook_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Notebook")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Hero Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.hero_notebooks_b2b),
                        contentDescription = "School Notebooks Wholesale",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        EduNavyPrimary.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "School Notebook Catalogue",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Single-line, Four-line, Ruled, Long & Practical books at direct wholesale",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Quick Stats Row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricStatCard(
                        title = "Catalog Items",
                        value = "${totalSKUs.size}",
                        subtitle = "Active Notebook SKUs",
                        icon = Icons.Default.MenuBook,
                        iconTint = EduNavyPrimary,
                        iconBackground = EduNavyContainer,
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        title = "Warehouse Units",
                        value = "${totalUnits / 1000}k",
                        subtitle = "$totalUnits books in stock",
                        icon = Icons.Default.Inventory,
                        iconTint = EduTealSecondary,
                        iconBackground = EduTealContainer,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToInventory
                    )
                    MetricStatCard(
                        title = "Low Stock Alert",
                        value = "$lowStockCount",
                        subtitle = "Requires restock",
                        icon = Icons.Default.Warning,
                        iconTint = if (lowStockCount > 0) AlertRed else SuccessGreen,
                        iconBackground = if (lowStockCount > 0) Color(0xFFFEE2E2) else Color(0xFFDCFCE7),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToInventory
                    )
                }
            }

            // Search Bar
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.searchQuery.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("catalogue_search_input"),
                        placeholder = { Text("Search by name, SKU, ruling or size...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }

            // Ruling Type Filter Chips
            item {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = "RULING TYPE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        rulings.forEach { ruling ->
                            FilterChip(
                                selected = selectedRuling == ruling,
                                onClick = { viewModel.selectedRulingFilter.value = ruling },
                                label = { Text(ruling, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EduNavyPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Size & Page Count Filters
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Size:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    sizes.forEach { size ->
                        FilterChip(
                            selected = selectedSize == size,
                            onClick = { viewModel.selectedSizeFilter.value = size },
                            label = { Text(size, fontSize = 11.sp) }
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Pages:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    pageCounts.forEach { pages ->
                        FilterChip(
                            selected = selectedPages == pages,
                            onClick = { viewModel.selectedPageFilter.value = pages },
                            label = { Text(if (pages == null) "All" else "${pages}p", fontSize = 11.sp) }
                        )
                    }
                }
            }

            // Items Count Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Showing ${notebooks.size} Products",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (selectedRuling != "All" || selectedSize != "All" || selectedPages != null || searchQuery.isNotEmpty()) {
                        Text(
                            text = "Reset Filters",
                            color = EduNavyPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable {
                                viewModel.searchQuery.value = ""
                                viewModel.selectedRulingFilter.value = "All"
                                viewModel.selectedSizeFilter.value = "All"
                                viewModel.selectedPageFilter.value = null
                            }
                        )
                    }
                }
            }

            // Notebooks List
            if (notebooks.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No notebooks match your filters",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Try adjusting ruling type, size or search keywords",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                items(notebooks, key = { it.id }) { notebook ->
                    NotebookCard(
                        notebook = notebook,
                        onClick = { selectedNotebookForDetail = notebook }
                    )
                }
            }
        }
    }

    // Detail Dialog
    selectedNotebookForDetail?.let { nb ->
        NotebookDetailDialog(
            notebook = nb,
            onDismiss = { selectedNotebookForDetail = null },
            onEdit = {
                notebookToEdit = nb
                selectedNotebookForDetail = null
            },
            onDelete = {
                viewModel.deleteNotebook(nb)
                selectedNotebookForDetail = null
            },
            onQuickRestock = {
                selectedNotebookForDetail = null
                onNavigateToInventory()
            }
        )
    }

    // Add / Edit Dialog
    if (showAddDialog || notebookToEdit != null) {
        AddEditNotebookDialog(
            initialNotebook = notebookToEdit,
            onDismiss = {
                showAddDialog = false
                notebookToEdit = null
            },
            onSave = { updated ->
                if (notebookToEdit != null) {
                    viewModel.updateNotebook(updated)
                } else {
                    viewModel.addNotebook(updated)
                }
                showAddDialog = false
                notebookToEdit = null
            }
        )
    }
}

@Composable
private fun NotebookCard(
    notebook: NotebookEntity,
    onClick: () -> Unit
) {
    val isLowStock = notebook.currentStock <= notebook.minStockThreshold
    val cartonWholesale = notebook.wholesalePrice * notebook.unitsPerCarton
    val stockProgress = (notebook.currentStock.toFloat() / (notebook.minStockThreshold * 3).coerceAtLeast(100)).coerceIn(0f, 1f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() }
            .testTag("notebook_card_${notebook.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Ruling & SKU row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(
                    text = notebook.rulingType,
                    type = when {
                        notebook.rulingType.contains("Single", ignoreCase = true) -> StatusBadgeType.INFO
                        notebook.rulingType.contains("Four", ignoreCase = true) -> StatusBadgeType.TEAL
                        notebook.rulingType.contains("Long", ignoreCase = true) -> StatusBadgeType.GOLD
                        notebook.rulingType.contains("Practical", ignoreCase = true) -> StatusBadgeType.SUCCESS
                        else -> StatusBadgeType.INFO
                    }
                )

                Text(
                    text = notebook.sku,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Notebook Title
            Text(
                text = notebook.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Size, Pages, GSM badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SpecPill(text = notebook.size)
                SpecPill(text = "${notebook.pageCount} Pages")
                SpecPill(text = "${notebook.paperGsm} GSM")
                SpecPill(text = notebook.bindingType)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Wholesale Price & Carton Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Wholesale Price:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "₹${"%.2f".format(notebook.wholesalePrice)}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = EduNavyPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "MRP ₹${"%.2f".format(notebook.mrpPrice)}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "Carton (${notebook.unitsPerCarton} pcs): ₹${"%.2f".format(cartonWholesale)}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Stock indicator
                Column(horizontalAlignment = Alignment.End) {
                    if (isLowStock) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = AlertRed,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "LOW STOCK",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AlertRed
                            )
                        }
                    }
                    Text(
                        text = "${notebook.currentStock} in stock",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isLowStock) AlertRed else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { stockProgress },
                        modifier = Modifier
                            .width(80.dp)
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (isLowStock) AlertRed else SuccessGreen,
                        trackColor = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SpecPill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}
