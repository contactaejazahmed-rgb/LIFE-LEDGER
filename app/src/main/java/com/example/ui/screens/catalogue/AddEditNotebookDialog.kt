package com.example.ui.screens.catalogue

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.NotebookEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNotebookDialog(
    initialNotebook: NotebookEntity? = null,
    onDismiss: () -> Unit,
    onSave: (NotebookEntity) -> Unit
) {
    var name by remember { mutableStateOf(initialNotebook?.name ?: "") }
    var sku by remember { mutableStateOf(initialNotebook?.sku ?: "NB-${System.currentTimeMillis() % 10000}") }
    var rulingType by remember { mutableStateOf(initialNotebook?.rulingType ?: "Single-line") }
    var size by remember { mutableStateOf(initialNotebook?.size ?: "Regular (24x18 cm)") }
    var pageCountText by remember { mutableStateOf((initialNotebook?.pageCount ?: 172).toString()) }
    var bindingType by remember { mutableStateOf(initialNotebook?.bindingType ?: "Center Stapled") }
    var gsmText by remember { mutableStateOf((initialNotebook?.paperGsm ?: 64).toString()) }
    var wholesalePriceText by remember { mutableStateOf((initialNotebook?.wholesalePrice ?: 28.0).toString()) }
    var mrpText by remember { mutableStateOf((initialNotebook?.mrpPrice ?: 45.0).toString()) }
    var unitsPerCartonText by remember { mutableStateOf((initialNotebook?.unitsPerCarton ?: 60).toString()) }
    var stockText by remember { mutableStateOf((initialNotebook?.currentStock ?: 500).toString()) }
    var thresholdText by remember { mutableStateOf((initialNotebook?.minStockThreshold ?: 100).toString()) }
    var description by remember { mutableStateOf(initialNotebook?.description ?: "") }

    val rulings = listOf(
        "Single-line",
        "Four-line",
        "Ruled (Square Grid)",
        "Ruled (Double-line)",
        "Long Book",
        "Practical Book",
        "Plain (Unruled)"
    )
    val sizes = listOf(
        "A4 (29.7x21 cm)",
        "Crown Long (28x17 cm)",
        "Regular (24x18 cm)",
        "Small (19x15 cm)"
    )
    val bindings = listOf(
        "Center Stapled",
        "Soft Bound",
        "Hard Bound",
        "Spiral"
    )

    var rulingExpanded by remember { mutableStateOf(false) }
    var sizeExpanded by remember { mutableStateOf(false) }
    var bindingExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("add_edit_notebook_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Text(
                    text = if (initialNotebook == null) "Add New Notebook" else "Edit Notebook",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Notebook Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("notebook_name_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = sku,
                    onValueChange = { sku = it },
                    label = { Text("SKU Code") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Ruling Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = rulingExpanded,
                    onExpandedChange = { rulingExpanded = !rulingExpanded }
                ) {
                    OutlinedTextField(
                        value = rulingType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Ruling Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = rulingExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = rulingExpanded,
                        onDismissRequest = { rulingExpanded = false }
                    ) {
                        rulings.forEach { ruling ->
                            DropdownMenuItem(
                                text = { Text(ruling) },
                                onClick = {
                                    rulingType = ruling
                                    rulingExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Size Dropdown
                ExposedDropdownMenuBox(
                    expanded = sizeExpanded,
                    onExpandedChange = { sizeExpanded = !sizeExpanded }
                ) {
                    OutlinedTextField(
                        value = size,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Size") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sizeExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = sizeExpanded,
                        onDismissRequest = { sizeExpanded = false }
                    ) {
                        sizes.forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s) },
                                onClick = {
                                    size = s
                                    sizeExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = pageCountText,
                        onValueChange = { pageCountText = it },
                        label = { Text("Pages") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = gsmText,
                        onValueChange = { gsmText = it },
                        label = { Text("Paper GSM") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Binding Dropdown
                ExposedDropdownMenuBox(
                    expanded = bindingExpanded,
                    onExpandedChange = { bindingExpanded = !bindingExpanded }
                ) {
                    OutlinedTextField(
                        value = bindingType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Binding Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bindingExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = bindingExpanded,
                        onDismissRequest = { bindingExpanded = false }
                    ) {
                        bindings.forEach { b ->
                            DropdownMenuItem(
                                text = { Text(b) },
                                onClick = {
                                    bindingType = b
                                    bindingExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = wholesalePriceText,
                        onValueChange = { wholesalePriceText = it },
                        label = { Text("Wholesale ₹") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("wholesale_price_input")
                    )
                    OutlinedTextField(
                        value = mrpText,
                        onValueChange = { mrpText = it },
                        label = { Text("MRP ₹") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = unitsPerCartonText,
                        onValueChange = { unitsPerCartonText = it },
                        label = { Text("Units / Carton") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = stockText,
                        onValueChange = { stockText = it },
                        label = { Text("Stock Units") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = thresholdText,
                    onValueChange = { thresholdText = it },
                    label = { Text("Min Stock Alert Threshold") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val entity = (initialNotebook ?: NotebookEntity(
                                sku = sku,
                                name = name,
                                rulingType = rulingType,
                                size = size,
                                pageCount = pageCountText.toIntOrNull() ?: 172,
                                bindingType = bindingType,
                                paperGsm = gsmText.toIntOrNull() ?: 64,
                                wholesalePrice = wholesalePriceText.toDoubleOrNull() ?: 25.0,
                                mrpPrice = mrpText.toDoubleOrNull() ?: 40.0,
                                unitsPerCarton = unitsPerCartonText.toIntOrNull() ?: 60,
                                currentStock = stockText.toIntOrNull() ?: 0,
                                minStockThreshold = thresholdText.toIntOrNull() ?: 100,
                                description = description
                            )).copy(
                                name = name.ifBlank { "Notebook" },
                                sku = sku.ifBlank { "NB-${System.currentTimeMillis() % 10000}" },
                                rulingType = rulingType,
                                size = size,
                                pageCount = pageCountText.toIntOrNull() ?: 172,
                                bindingType = bindingType,
                                paperGsm = gsmText.toIntOrNull() ?: 64,
                                wholesalePrice = wholesalePriceText.toDoubleOrNull() ?: 25.0,
                                mrpPrice = mrpText.toDoubleOrNull() ?: 40.0,
                                unitsPerCarton = unitsPerCartonText.toIntOrNull() ?: 60,
                                currentStock = stockText.toIntOrNull() ?: 0,
                                minStockThreshold = thresholdText.toIntOrNull() ?: 100,
                                description = description
                            )
                            onSave(entity)
                        },
                        modifier = Modifier.testTag("save_notebook_button")
                    ) {
                        Text("Save Notebook")
                    }
                }
            }
        }
    }
}
