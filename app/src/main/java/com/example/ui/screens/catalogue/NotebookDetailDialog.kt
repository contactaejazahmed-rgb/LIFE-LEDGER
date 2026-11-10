package com.example.ui.screens.catalogue

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.NotebookEntity
import com.example.ui.components.StatusBadge
import com.example.ui.components.StatusBadgeType
import com.example.ui.theme.AlertRed
import com.example.ui.theme.EduGoldTertiary
import com.example.ui.theme.EduNavyPrimary
import com.example.ui.theme.SuccessGreen

@Composable
fun NotebookDetailDialog(
    notebook: NotebookEntity,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onQuickRestock: () -> Unit
) {
    val isLowStock = notebook.currentStock <= notebook.minStockThreshold
    val cartons = notebook.currentStock / notebook.unitsPerCarton
    val cartonRemainder = notebook.currentStock % notebook.unitsPerCarton
    val cartonWholesale = notebook.wholesalePrice * notebook.unitsPerCarton
    val marginPercent = if (notebook.mrpPrice > 0) {
        ((notebook.mrpPrice - notebook.wholesalePrice) / notebook.mrpPrice * 100).toInt()
    } else 0

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("notebook_detail_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header with Ruling and Close
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
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_detail_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = notebook.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "SKU: ${notebook.sku}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Wholesale Pricing Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Wholesale Price / Unit",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "₹${"%.2f".format(notebook.wholesalePrice)}",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EduNavyPrimary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "MRP: ₹${"%.2f".format(notebook.mrpPrice)}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                StatusBadge(
                                    text = "$marginPercent% School Margin",
                                    type = StatusBadgeType.SUCCESS
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Carton Pack (${notebook.unitsPerCarton} units):",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "₹${"%.2f".format(cartonWholesale)} / carton",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Technical Specifications
                Text(
                    text = "SPECIFICATIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                SpecRow(label = "Size", value = notebook.size)
                SpecRow(label = "Page Count", value = "${notebook.pageCount} Pages")
                SpecRow(label = "Paper Quality", value = "${notebook.paperGsm} GSM Super White")
                SpecRow(label = "Binding Format", value = notebook.bindingType)
                SpecRow(label = "Units / Carton", value = "${notebook.unitsPerCarton} books")
                SpecRow(
                    label = "Stock Availability",
                    value = "${notebook.currentStock} units ($cartons cartons${if (cartonRemainder > 0) " + $cartonRemainder" else ""})",
                    highlight = isLowStock
                )
                SpecRow(label = "Reorder Threshold", value = "${notebook.minStockThreshold} units")

                if (notebook.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Description",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = notebook.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("edit_notebook_button")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit")
                    }

                    Button(
                        onClick = onQuickRestock,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("quick_restock_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isLowStock) AlertRed else EduNavyPrimary
                        )
                    ) {
                        Icon(Icons.Default.Inventory, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Restock")
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.testTag("delete_notebook_button")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AlertRed)
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecRow(
    label: String,
    value: String,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (highlight) AlertRed else MaterialTheme.colorScheme.onSurface
        )
    }
}
