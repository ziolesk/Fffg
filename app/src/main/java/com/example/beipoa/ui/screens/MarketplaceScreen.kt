package com.example.beipoa.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CompareArrows
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.beipoa.data.model.MarketItem
import com.example.beipoa.ui.components.ItemCard
import com.example.beipoa.ui.components.ProductComparisonDialog
import com.example.beipoa.ui.components.QuickViewDialog
import com.example.beipoa.ui.theme.*
import com.example.beipoa.ui.viewmodel.MarketplaceViewModel
import com.example.beipoa.ui.viewmodel.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    viewModel: MarketplaceViewModel,
    onNavigateToProduct: (String) -> Unit,
    onNavigateToSell: () -> Unit
) {
    val items by viewModel.filteredMarketItems.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedUniversity by viewModel.selectedUniversity.collectAsState()
    val maxPrice by viewModel.maxPriceFilter.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()
    val showFavoritesOnly by viewModel.showFavoritesOnly.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val savedSearches by viewModel.savedSearches.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val quickViewItem by viewModel.quickViewItem.collectAsState()
    val isCompareModalOpen by viewModel.isCompareModalOpen.collectAsState()
    val comparisonItems by viewModel.comparisonItems.collectAsState()

    var showPriceDialog by remember { mutableStateOf(false) }
    var tempMaxPrice by remember { mutableStateOf("") }
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var universityMenuExpanded by remember { mutableStateOf(false) }
    var showSavedSearchesBar by remember { mutableStateOf(false) }

    val categories = listOf("All", "Textbooks", "Electronics", "Furniture", "Notes / PDFs", "Other")
    val universities = listOf("All") + viewModel.getUniversities()

    Scaffold(
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (comparisonItems.isNotEmpty()) {
                    ExtendedFloatingActionButton(
                        onClick = { viewModel.openCompareModal() },
                        containerColor = Navy900,
                        contentColor = Color.White,
                        icon = { Icon(Icons.Outlined.CompareArrows, contentDescription = null) },
                        text = { Text("Compare (${comparisonItems.size})", fontWeight = FontWeight.Bold) }
                    )
                }

                ExtendedFloatingActionButton(
                    onClick = {
                        if (currentUser != null) onNavigateToSell()
                        else viewModel.openAuth("login")
                    },
                    containerColor = OrangePrimary,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Sell Item", fontWeight = FontWeight.Bold) },
                    modifier = Modifier.testTag("marketplace_sell_fab")
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .testTag("marketplace_screen")
        ) {
            // Top Modern Search Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.searchQuery.value = it },
                            placeholder = { Text("Search textbooks, laptops, hostel gear...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                            trailingIcon = {
                                if (searchQuery.isNotBlank()) {
                                    IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear")
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("marketplace_search_input")
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        IconButton(
                            onClick = { showSavedSearchesBar = !showSavedSearchesBar },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                        ) {
                            Icon(Icons.Outlined.BookmarkBorder, contentDescription = "Saved Searches", tint = OrangePrimary)
                        }
                    }

                    // Saved Searches Toggle Row
                    AnimatedVisibility(visible = showSavedSearchesBar) {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Saved Searches:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                if (searchQuery.isNotBlank()) {
                                    TextButton(onClick = { viewModel.saveCurrentSearch() }) {
                                        Text("+ Save \"$searchQuery\"", fontSize = 11.sp, color = OrangePrimary)
                                    }
                                }
                            }
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(savedSearches) { saved ->
                                    InputChip(
                                        selected = searchQuery == saved,
                                        onClick = { viewModel.applySavedSearch(saved) },
                                        label = { Text(saved, fontSize = 11.sp) },
                                        trailingIcon = {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Remove",
                                                modifier = Modifier.size(12.dp).clickable { viewModel.removeSavedSearch(saved) }
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Horizontal Category Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { cat ->
                            val isSelected = if (cat == "All") selectedCategory == null else selectedCategory == cat
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    viewModel.selectedCategory.value = if (cat == "All") null else cat
                                },
                                label = { Text(cat, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = OrangePrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Advanced Filter & Sort Ribbon
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            // University Filter Dropdown
                            Box {
                                OutlinedButton(
                                    onClick = { universityMenuExpanded = true },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(14.dp), tint = OrangePrimary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = selectedUniversity ?: "All Campuses",
                                        fontSize = 11.sp,
                                        maxLines = 1
                                    )
                                }
                                DropdownMenu(
                                    expanded = universityMenuExpanded,
                                    onDismissRequest = { universityMenuExpanded = false }
                                ) {
                                    universities.forEach { uni ->
                                        DropdownMenuItem(
                                            text = { Text(uni, fontSize = 12.sp) },
                                            onClick = {
                                                viewModel.selectedUniversity.value = if (uni == "All") null else uni
                                                universityMenuExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Price Filter Button
                            OutlinedButton(
                                onClick = { showPriceDialog = true },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (maxPrice != null) "<= KSh ${maxPrice?.toInt()}" else "Max Price",
                                    fontSize = 11.sp,
                                    color = if (maxPrice != null) OrangePrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Favorites Only toggle
                            IconButton(
                                onClick = { viewModel.showFavoritesOnly.value = !showFavoritesOnly },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (showFavoritesOnly) Icons.Default.Favorite else Icons.Outlined.Favorite,
                                    contentDescription = "Filter Favorites",
                                    tint = if (showFavoritesOnly) Color(0xFFEF4444) else TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Sorting Dropdown
                        Box {
                            TextButton(
                                onClick = { sortMenuExpanded = true },
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Outlined.FilterList, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(sortBy.label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                            DropdownMenu(
                                expanded = sortMenuExpanded,
                                onDismissRequest = { sortMenuExpanded = false }
                            ) {
                                SortOption.values().forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option.label, fontSize = 12.sp) },
                                        onClick = {
                                            viewModel.sortBy.value = option
                                            sortMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Results count and active filter tags
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${items.size} listings found",
                    fontSize = 12.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Medium
                )

                if (selectedCategory != null || selectedUniversity != null || maxPrice != null || searchQuery.isNotBlank() || showFavoritesOnly) {
                    Text(
                        text = "Reset Filters",
                        fontSize = 11.sp,
                        color = OrangePrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { viewModel.resetFilters() }
                            .padding(4.dp)
                    )
                }
            }

            // Product Grid
            if (items.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.SearchOff, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No verified items found", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Try adjusting your keyword, university filter, or price limit.", color = TextMuted, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { viewModel.resetFilters() },
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                        ) {
                            Text("Show All Listings", color = Color.White)
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 80.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(items, key = { it.id }) { item ->
                        ItemCard(
                            item = item,
                            onClick = { onNavigateToProduct(item.id) },
                            onFavoriteToggle = { viewModel.toggleFavorite(item.id) },
                            isFavorite = favorites.contains(item.id),
                            onQuickView = { viewModel.quickViewItem.value = item }
                        )
                    }
                }
            }
        }
    }

    // Price Dialog
    if (showPriceDialog) {
        AlertDialog(
            onDismissRequest = { showPriceDialog = false },
            title = { Text("Filter by Maximum Price", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column {
                    Text("Enter highest acceptable price in KSh:", fontSize = 12.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = tempMaxPrice,
                        onValueChange = { tempMaxPrice = it },
                        placeholder = { Text("e.g. 5000") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = tempMaxPrice.toDoubleOrNull()
                        viewModel.maxPriceFilter.value = parsed
                        showPriceDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text("Apply", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.maxPriceFilter.value = null
                    tempMaxPrice = ""
                    showPriceDialog = false
                }) {
                    Text("Clear Filter")
                }
            }
        )
    }

    // Quick View Dialog
    QuickViewDialog(
        item = quickViewItem,
        onDismiss = { viewModel.quickViewItem.value = null },
        onViewFullDetails = { itemId ->
            viewModel.quickViewItem.value = null
            onNavigateToProduct(itemId)
        },
        onChatNow = { item ->
            viewModel.quickViewItem.value = null
            viewModel.startChatWithSeller(item)
            onNavigateToProduct(item.id)
        }
    )

    // Product Comparison Dialog
    ProductComparisonDialog(
        items = comparisonItems,
        onDismiss = { viewModel.closeCompareModal() },
        onNavigateToProduct = onNavigateToProduct
    )
}
