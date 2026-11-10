package com.example.ui.screens.inventory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.NotebookEntity
import com.example.ui.theme.EduNavyPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPurchaseEntryDialog(
    notebooks: List<NotebookEntity>,
    preselectedNotebook: NotebookEntity? = null,
    onDismiss: () -> Unit,
    onSavePurchase: (supplierName: String, notebook: NotebookEntity, quantity: Int, unitCost: Double, batchNumber: String, notes: String) -> Unit
) {
    var selectedNotebook by remember { mutableStateOf(preselectedNotebook ?: notebooks.firstOrNull()) }
    var supplierName by remember { mutableStateOf("JK Paper & Stationery Mills Ltd.") }
    var batchNumber by remember { mutableStateOf("BATCH-${System.currentTimeMillis() % 10000}") }
    var quantityText by remember { mutableStateOf("500") }
    var unitCostText by remember { mutableStateOf((selectedNotebook?.wholesalePrice?.times(0.75)?.toInt() ?: 20).toString()) }
    var notes by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }

    val quantity = quantityText.toIntOrNull() ?: 0
    val unitCost = unitCostText.toDoubleOrNull() ?: 0.0
    val totalCost = quantity * unitCost

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("add_purchase_entry_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Text(
                    text = "Record Inward Purchase / Restock",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Add arrival of printed notebooks to replenish warehouse inventory",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Notebook Selection Dropdown
                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedNotebook?.name ?: "Select Notebook",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Notebook to Restock") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("purchase_notebook_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        notebooks.forEach { nb ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(nb.name, fontWeight = FontWeight.SemiBold)
                                        Text("Current Stock: ${nb.currentStock} units • ${nb.rulingType}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                },
                                onClick = {
                                    selectedNotebook = nb
                                    unitCostText = (nb.wholesalePrice * 0.72).toInt().toString()
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = supplierName,
                    onValueChange = { supplierName = it },
                    label = { Text("Paper Mill / Manufacturer Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = batchNumber,
                    onValueChange = { batchNumber = it },
                    label = { Text("Production Batch / Lot Number") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it },
                        label = { Text("Quantity (Units)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("purchase_qty_input")
                    )
                    OutlinedTextField(
                        value = unitCostText,
                        onValueChange = { unitCostText = it },
                        label = { Text("Purchase Cost ₹/Unit") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("purchase_unit_cost_input")
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Total Cost Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Total Inward Invoice Value:",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "₹${"%.2f".format(totalCost)}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = EduNavyPrimary
                            )
                        }
                        selectedNotebook?.let { nb ->
                            val newStock = nb.currentStock + quantity
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Updated Stock Level:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "$newStock units",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Inspection Notes & Gate Pass Ref") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val nb = selectedNotebook
                            if (nb != null && quantity > 0 && unitCost > 0) {
                                onSavePurchase(
                                    supplierName,
                                    nb,
                                    quantity,
                                    unitCost,
                                    batchNumber,
                                    notes
                                )
                            }
                        },
                        modifier = Modifier.testTag("confirm_purchase_button")
                    ) {
                        Text("Confirm Restock")
                    }
                }
            }
        }
    }
}
