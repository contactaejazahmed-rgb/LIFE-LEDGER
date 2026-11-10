package com.example.ui.screens.sales

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.NotebookEntity
import com.example.data.local.entity.SchoolEntity
import com.example.ui.MainViewModel
import com.example.ui.components.StatusBadge
import com.example.ui.components.StatusBadgeType
import com.example.ui.theme.AlertRed
import com.example.ui.theme.EduGoldTertiary
import com.example.ui.theme.EduNavyPrimary
import com.example.ui.theme.SuccessGreen

data class OrderItemDraft(
    val notebook: NotebookEntity,
    var quantity: Int,
    var pricePerUnit: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSalesEntryDialog(
    schools: List<SchoolEntity>,
    notebooks: List<NotebookEntity>,
    preselectedSchool: SchoolEntity? = null,
    viewModel: MainViewModel,
    onDismiss: () -> Unit,
    onSaveOrder: (school: SchoolEntity, items: List<Triple<NotebookEntity, Int, Double>>, paymentTerms: String, notes: String) -> Unit
) {
    var selectedSchool by remember { mutableStateOf(preselectedSchool ?: schools.firstOrNull()) }
    var paymentTerms by remember { mutableStateOf("Net 30 Days") }
    var notes by remember { mutableStateOf("") }
    var schoolDropdownExpanded by remember { mutableStateOf(false) }
    var termsDropdownExpanded by remember { mutableStateOf(false) }

    // Draft order items list
    val orderItems = remember { mutableStateListOf<OrderItemDraft>() }

    // Prepopulate 1 item
    LaunchedEffect(selectedSchool) {
        if (orderItems.isEmpty() && notebooks.isNotEmpty()) {
            val first = notebooks.first()
            val discount = selectedSchool?.contractedDiscountPercent ?: 10.0
            val price = first.wholesalePrice * (1.0 - (discount / 100.0))
            orderItems.add(OrderItemDraft(first, 200, price))
        }
    }

    val totalUnits = orderItems.sumOf { it.quantity }
    val totalAmount = orderItems.sumOf { it.quantity * it.pricePerUnit }

    val termsOptions = listOf("Net 30 Days", "Net 15 Days", "Advance Payment", "Cash on Delivery")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("create_sales_order_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Text(
                    text = "New School Sales Order & Invoice",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "B2B order entry with school-specific wholesale rate calculation",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // School Selector
                ExposedDropdownMenuBox(
                    expanded = schoolDropdownExpanded,
                    onExpandedChange = { schoolDropdownExpanded = !schoolDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedSchool?.name ?: "Select School",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("School Account") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = schoolDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("order_school_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = schoolDropdownExpanded,
                        onDismissRequest = { schoolDropdownExpanded = false }
                    ) {
                        schools.forEach { school ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(school.name, fontWeight = FontWeight.SemiBold)
                                        Text(
                                            "${school.affiliation} • ${school.contractedDiscountPercent}% Discount • ${school.city}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                onClick = {
                                    selectedSchool = school
                                    schoolDropdownExpanded = false
                                    // Recalculate prices for existing items
                                    val disc = school.contractedDiscountPercent
                                    orderItems.forEachIndexed { i, draft ->
                                        val newPrice = draft.notebook.wholesalePrice * (1.0 - (disc / 100.0))
                                        orderItems[i] = draft.copy(pricePerUnit = newPrice)
                                    }
                                }
                            )
                        }
                    }
                }

                selectedSchool?.let { sch ->
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Purchase Mgr: ${sch.purchaseManagerName}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        StatusBadge(
                            text = "${sch.contractedDiscountPercent}% School Discount",
                            type = StatusBadgeType.GOLD
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Order Items Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ORDER ITEMS (${orderItems.size})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )

                    Button(
                        onClick = {
                            val nextNb = notebooks.firstOrNull { nb -> orderItems.none { it.notebook.id == nb.id } } ?: notebooks.firstOrNull()
                            if (nextNb != null) {
                                val discount = selectedSchool?.contractedDiscountPercent ?: 10.0
                                val price = nextNb.wholesalePrice * (1.0 - (discount / 100.0))
                                orderItems.add(OrderItemDraft(nextNb, 100, price))
                            }
                        },
                        modifier = Modifier.testTag("add_order_item_line_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Notebook", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Items list
                orderItems.forEachIndexed { index, itemDraft ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = itemDraft.notebook.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { orderItems.removeAt(index) },
                                    enabled = orderItems.size > 1
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = AlertRed, modifier = Modifier.size(16.dp))
                                }
                            }

                            Text(
                                text = "${itemDraft.notebook.rulingType} • ${itemDraft.notebook.size} • Stock: ${itemDraft.notebook.currentStock} units",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = itemDraft.quantity.toString(),
                                    onValueChange = {
                                        val q = it.toIntOrNull() ?: 0
                                        orderItems[index] = itemDraft.copy(quantity = q)
                                    },
                                    label = { Text("Quantity") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = "%.2f".format(itemDraft.pricePerUnit),
                                    onValueChange = {
                                        val p = it.toDoubleOrNull() ?: itemDraft.pricePerUnit
                                        orderItems[index] = itemDraft.copy(pricePerUnit = p)
                                    },
                                    label = { Text("Rate ₹/unit") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    modifier = Modifier.weight(1f)
                                )

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Line Total:", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        "₹${"%.2f".format(itemDraft.quantity * itemDraft.pricePerUnit)}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EduNavyPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Payment Terms Dropdown
                ExposedDropdownMenuBox(
                    expanded = termsDropdownExpanded,
                    onExpandedChange = { termsDropdownExpanded = !termsDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = paymentTerms,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Payment Terms") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = termsDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = termsDropdownExpanded,
                        onDismissRequest = { termsDropdownExpanded = false }
                    ) {
                        termsOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt) },
                                onClick = {
                                    paymentTerms = opt
                                    termsDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Delivery Instructions / PO Ref") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Total Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Ordered Units:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$totalUnits units", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Invoice Amount:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(
                                "₹${"%.2f".format(totalAmount)}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = EduNavyPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val sch = selectedSchool
                            if (sch != null && orderItems.isNotEmpty() && totalAmount > 0) {
                                val triples = orderItems.map { Triple(it.notebook, it.quantity, it.pricePerUnit) }
                                onSaveOrder(sch, triples, paymentTerms, notes)
                            }
                        },
                        modifier = Modifier.testTag("submit_sales_order_btn")
                    ) {
                        Text("Generate Invoice & Order")
                    }
                }
            }
        }
    }
}
