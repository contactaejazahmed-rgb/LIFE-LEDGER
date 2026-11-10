package com.example.ui.screens.schools

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

@Composable
fun SchoolsScreen(
    viewModel: MainViewModel,
    onNavigateToCreateOrder: (SchoolEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val schools by viewModel.filteredSchools.collectAsStateWithLifecycle()
    val totalSchools by viewModel.allSchools.collectAsStateWithLifecycle()
    val verifiedCount by viewModel.verifiedSchoolCount.collectAsStateWithLifecycle()
    val searchQuery by viewModel.schoolSearchQuery.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var schoolToEdit by remember { mutableStateOf<SchoolEntity?>(null) }
    var schoolForPriceList by remember { mutableStateOf<SchoolEntity?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = EduNavyPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("register_school_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Register School")
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
                        text = "School Accounts & Partnerships",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Verified educational institutions, purchase managers & custom price lists",
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
                        title = "Registered Schools",
                        value = "${totalSchools.size}",
                        subtitle = "Institutional buyers",
                        icon = Icons.Default.School,
                        iconTint = EduNavyPrimary,
                        iconBackground = EduNavyContainer,
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        title = "Verified Status",
                        value = "$verifiedCount",
                        subtitle = "Approved for credit",
                        icon = Icons.Default.Verified,
                        iconTint = SuccessGreen,
                        iconBackground = Color(0xFFDCFCE7),
                        modifier = Modifier.weight(1f)
                    )
                    val pendingCount = totalSchools.size - verifiedCount
                    MetricStatCard(
                        title = "Pending Review",
                        value = "$pendingCount",
                        subtitle = "Awaiting verification",
                        icon = Icons.Default.Person,
                        iconTint = EduGoldTertiary,
                        iconBackground = EduGoldContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Search Bar
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.schoolSearchQuery.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("school_search_input"),
                        placeholder = { Text("Search by school, city, principal or board...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.schoolSearchQuery.value = "" }) {
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

            // Schools List
            if (schools.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No schools found",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Register a new school or modify search query",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                items(schools, key = { it.id }) { school ->
                    SchoolCard(
                        school = school,
                        onOpenPriceList = { schoolForPriceList = school },
                        onEdit = { schoolToEdit = school },
                        onDelete = { viewModel.deleteSchool(school) },
                        onCreateOrder = { onNavigateToCreateOrder(school) }
                    )
                }
            }
        }
    }

    // Add / Edit Dialog
    if (showAddDialog || schoolToEdit != null) {
        AddEditSchoolDialog(
            initialSchool = schoolToEdit,
            onDismiss = {
                showAddDialog = false
                schoolToEdit = null
            },
            onSave = { entity ->
                if (schoolToEdit != null) {
                    viewModel.updateSchool(entity)
                } else {
                    viewModel.addSchool(entity)
                }
                showAddDialog = false
                schoolToEdit = null
            }
        )
    }

    // Price List Dialog
    schoolForPriceList?.let { school ->
        SchoolPriceListDialog(
            school = school,
            viewModel = viewModel,
            onDismiss = { schoolForPriceList = null }
        )
    }
}

@Composable
private fun SchoolCard(
    school: SchoolEntity,
    onOpenPriceList: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCreateOrder: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("school_card_${school.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Status and Board Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(
                        text = school.verificationStatus,
                        type = when (school.verificationStatus) {
                            "Premium Partner" -> StatusBadgeType.GOLD
                            "Verified" -> StatusBadgeType.SUCCESS
                            else -> StatusBadgeType.WARNING
                        }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    StatusBadge(
                        text = school.affiliation,
                        type = StatusBadgeType.INFO
                    )
                }
                Text(
                    text = school.schoolCode,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // School Name
            Text(
                text = school.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Address Row
            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${school.address}, ${school.city}, ${school.state} - ${school.pincode}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Principal & Purchase Manager Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Principal:",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = school.principalName.ifBlank { "Not specified" },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Purchase Manager:",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = school.purchaseManagerName.ifBlank { "Not specified" },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Phone & Email with direct action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (school.contactPhone.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${school.contactPhone}"))
                                    context.startActivity(intent)
                                }
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "Call", tint = EduTealSecondary, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(school.contactPhone, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                    if (school.contactEmail.isNotBlank()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${school.contactEmail}"))
                                    context.startActivity(intent)
                                }
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Icon(Icons.Default.Email, contentDescription = "Email", tint = EduNavyPrimary, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(school.contactEmail.take(16) + "...", fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                // Discount Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(EduGoldContainer)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${school.contractedDiscountPercent}% Disc",
                        color = EduGoldTertiary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Actions row: Custom Price List, Create Order, Edit, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onOpenPriceList,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("school_price_list_btn_${school.id}")
                ) {
                    Icon(Icons.Default.PriceChange, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Price List", fontSize = 12.sp)
                }

                Button(
                    onClick = onCreateOrder,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("school_new_order_btn_${school.id}")
                ) {
                    Text("New Order", fontSize = 12.sp)
                }

                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit School", modifier = Modifier.size(18.dp))
                }

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete School", tint = AlertRed, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
