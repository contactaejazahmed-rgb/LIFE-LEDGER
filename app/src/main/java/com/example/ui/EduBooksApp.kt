package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.entity.SchoolEntity
import com.example.ui.components.EduBooksTopBar
import com.example.ui.screens.catalogue.CatalogueScreen
import com.example.ui.screens.inventory.InventoryScreen
import com.example.ui.screens.sales.SalesScreen
import com.example.ui.screens.schools.SchoolsScreen
import com.example.ui.theme.AlertRed
import com.example.ui.theme.EduGoldTertiary
import com.example.ui.theme.EduNavyPrimary

enum class AppTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    CATALOGUE("Catalogue", Icons.Default.MenuBook),
    SCHOOLS("Schools", Icons.Default.School),
    INVENTORY("Inventory", Icons.Default.Inventory),
    SALES("Orders", Icons.Default.Receipt)
}

@Composable
fun EduBooksApp(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(AppTab.CATALOGUE) }
    var preselectedSchoolForOrder by remember { mutableStateOf<SchoolEntity?>(null) }

    val lowStockCount by viewModel.lowStockCount.collectAsStateWithLifecycle()
    val pendingCount by viewModel.pendingDispatchCount.collectAsStateWithLifecycle()

    // BackHandler: Return to Catalogue if on another tab
    BackHandler(enabled = selectedTab != AppTab.CATALOGUE) {
        selectedTab = AppTab.CATALOGUE
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            EduBooksTopBar(
                lowStockCount = lowStockCount,
                onLowStockClick = { selectedTab = AppTab.INVENTORY }
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_bottom_nav"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                AppTab.values().forEach { tab ->
                    val isSelected = selectedTab == tab

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        icon = {
                            when (tab) {
                                AppTab.INVENTORY -> {
                                    if (lowStockCount > 0) {
                                        BadgedBox(
                                            badge = {
                                                Badge(containerColor = AlertRed) {
                                                    Text("$lowStockCount", color = Color.White, fontSize = 9.sp)
                                                }
                                            }
                                        ) {
                                            Icon(tab.icon, contentDescription = tab.title)
                                        }
                                    } else {
                                        Icon(tab.icon, contentDescription = tab.title)
                                    }
                                }
                                AppTab.SALES -> {
                                    if (pendingCount > 0) {
                                        BadgedBox(
                                            badge = {
                                                Badge(containerColor = EduGoldTertiary) {
                                                    Text("$pendingCount", color = Color.White, fontSize = 9.sp)
                                                }
                                            }
                                        ) {
                                            Icon(tab.icon, contentDescription = tab.title)
                                        }
                                    } else {
                                        Icon(tab.icon, contentDescription = tab.title)
                                    }
                                }
                                else -> Icon(tab.icon, contentDescription = tab.title)
                            }
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EduNavyPrimary,
                            selectedTextColor = EduNavyPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_item_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                AppTab.CATALOGUE -> {
                    CatalogueScreen(
                        viewModel = viewModel,
                        onNavigateToInventory = { selectedTab = AppTab.INVENTORY }
                    )
                }
                AppTab.SCHOOLS -> {
                    SchoolsScreen(
                        viewModel = viewModel,
                        onNavigateToCreateOrder = { school ->
                            preselectedSchoolForOrder = school
                            selectedTab = AppTab.SALES
                        }
                    )
                }
                AppTab.INVENTORY -> {
                    InventoryScreen(viewModel = viewModel)
                }
                AppTab.SALES -> {
                    SalesScreen(
                        viewModel = viewModel,
                        preselectedSchoolForOrder = preselectedSchoolForOrder,
                        onClearPreselectedSchool = { preselectedSchoolForOrder = null }
                    )
                }
            }
        }
    }
}
