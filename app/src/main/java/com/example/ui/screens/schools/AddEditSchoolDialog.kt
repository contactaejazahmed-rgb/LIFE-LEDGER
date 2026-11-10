package com.example.ui.screens.schools

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
import com.example.data.local.entity.SchoolEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSchoolDialog(
    initialSchool: SchoolEntity? = null,
    onDismiss: () -> Unit,
    onSave: (SchoolEntity) -> Unit
) {
    var name by remember { mutableStateOf(initialSchool?.name ?: "") }
    var schoolCode by remember { mutableStateOf(initialSchool?.schoolCode ?: "SCH-${System.currentTimeMillis() % 10000}") }
    var affiliation by remember { mutableStateOf(initialSchool?.affiliation ?: "CBSE") }
    var address by remember { mutableStateOf(initialSchool?.address ?: "") }
    var city by remember { mutableStateOf(initialSchool?.city ?: "") }
    var state by remember { mutableStateOf(initialSchool?.state ?: "") }
    var pincode by remember { mutableStateOf(initialSchool?.pincode ?: "") }
    var principalName by remember { mutableStateOf(initialSchool?.principalName ?: "") }
    var purchaseManagerName by remember { mutableStateOf(initialSchool?.purchaseManagerName ?: "") }
    var phone by remember { mutableStateOf(initialSchool?.contactPhone ?: "") }
    var email by remember { mutableStateOf(initialSchool?.contactEmail ?: "") }
    var verificationStatus by remember { mutableStateOf(initialSchool?.verificationStatus ?: "Pending Verification") }
    var discountText by remember { mutableStateOf((initialSchool?.contractedDiscountPercent ?: 10.0).toString()) }
    var notes by remember { mutableStateOf(initialSchool?.notes ?: "") }

    val affiliations = listOf("CBSE", "ICSE", "State Board", "Cambridge / IB", "Montessori", "Matriculation")
    val statuses = listOf("Verified", "Pending Verification", "Premium Partner")

    var affiliationExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("add_edit_school_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Text(
                    text = if (initialSchool == null) "Register New School" else "Edit School Account",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("School Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("school_name_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = schoolCode,
                        onValueChange = { schoolCode = it },
                        label = { Text("School Code") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    // Affiliation Dropdown
                    ExposedDropdownMenuBox(
                        expanded = affiliationExpanded,
                        onExpandedChange = { affiliationExpanded = !affiliationExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = affiliation,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Affiliation") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = affiliationExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = affiliationExpanded,
                            onDismissRequest = { affiliationExpanded = false }
                        ) {
                            affiliations.forEach { aff ->
                                DropdownMenuItem(
                                    text = { Text(aff) },
                                    onClick = {
                                        affiliation = aff
                                        affiliationExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Verification Status
                ExposedDropdownMenuBox(
                    expanded = statusExpanded,
                    onExpandedChange = { statusExpanded = !statusExpanded }
                ) {
                    OutlinedTextField(
                        value = verificationStatus,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Verification Status") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = statusExpanded,
                        onDismissRequest = { statusExpanded = false }
                    ) {
                        statuses.forEach { st ->
                            DropdownMenuItem(
                                text = { Text(st) },
                                onClick = {
                                    verificationStatus = st
                                    statusExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Campus / Street Address") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = state,
                        onValueChange = { state = it },
                        label = { Text("State") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = pincode,
                        onValueChange = { pincode = it },
                        label = { Text("Pincode") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = principalName,
                    onValueChange = { principalName = it },
                    label = { Text("Principal / Head of Institution") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = purchaseManagerName,
                    onValueChange = { purchaseManagerName = it },
                    label = { Text("Purchase Manager / Contact Person") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Phone Number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = discountText,
                    onValueChange = { discountText = it },
                    label = { Text("Contracted Wholesale Discount %") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Internal Notes / Contract Details") },
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
                            val entity = (initialSchool ?: SchoolEntity(
                                schoolCode = schoolCode,
                                name = name,
                                affiliation = affiliation,
                                address = address,
                                city = city,
                                state = state,
                                pincode = pincode,
                                principalName = principalName,
                                purchaseManagerName = purchaseManagerName,
                                contactPhone = phone,
                                contactEmail = email,
                                verificationStatus = verificationStatus,
                                contractedDiscountPercent = discountText.toDoubleOrNull() ?: 10.0,
                                notes = notes
                            )).copy(
                                schoolCode = schoolCode.ifBlank { "SCH-${System.currentTimeMillis() % 10000}" },
                                name = name.ifBlank { "School Name" },
                                affiliation = affiliation,
                                address = address,
                                city = city,
                                state = state,
                                pincode = pincode,
                                principalName = principalName,
                                purchaseManagerName = purchaseManagerName,
                                contactPhone = phone,
                                contactEmail = email,
                                verificationStatus = verificationStatus,
                                contractedDiscountPercent = discountText.toDoubleOrNull() ?: 10.0,
                                notes = notes
                            )
                            onSave(entity)
                        },
                        modifier = Modifier.testTag("save_school_button")
                    ) {
                        Text("Save School")
                    }
                }
            }
        }
    }
}
