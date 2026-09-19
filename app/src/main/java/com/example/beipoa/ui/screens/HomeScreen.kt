package com.example.beipoa.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.beipoa.ui.components.ItemCard
import com.example.beipoa.ui.components.ProductComparisonDialog
import com.example.beipoa.ui.components.QuickViewDialog
import com.example.beipoa.ui.theme.*
import com.example.beipoa.ui.viewmodel.MarketplaceViewModel

data class CategoryItem(val name: String, val categoryFilter: String, val icon: ImageVector)

@Composable
fun HomeScreen(
    viewModel: MarketplaceViewModel,
    onNavigateToMarketplace: () -> Unit,
    onNavigateToSell: () -> Unit,
    onNavigateToProduct: (String) -> Unit,
    onNavigateToLegal: (String) -> Unit
) {
    val featuredItems by viewModel.featuredItems.collectAsState()
    val allItems by viewModel.allItems.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val quickViewItem by viewModel.quickViewItem.collectAsState()
    val isCompareModalOpen by viewModel.isCompareModalOpen.collectAsState()
    val comparisonItems by viewModel.comparisonItems.collectAsState()

    var expandedFaqIndex by remember { mutableStateOf<Int?>(null) }

    val categories = listOf(
        CategoryItem("Textbooks", "Textbooks", Icons.Default.MenuBook),
        CategoryItem("Notes/PDFs", "Notes / PDFs", Icons.Default.Description),
        CategoryItem("Laptops", "Electronics", Icons.Default.Laptop),
        CategoryItem("Phones", "Electronics", Icons.Default.PhoneAndroid),
        CategoryItem("Furniture", "Furniture", Icons.Default.Weekend),
        CategoryItem("Gadgets", "Electronics", Icons.Default.Headphones),
        CategoryItem("Lab Gear", "Other", Icons.Default.Science),
        CategoryItem("Other", "Other", Icons.Default.Category)
    )

    val universitiesSpotlight = listOf(
        "University of Nairobi" to "Main, Chiromo, Lower Kabete, Kikuyu",
        "Kenyatta University" to "Main Campus Thika Rd, Parklands",
        "JKUAT" to "Juja Main Campus, Karen",
        "Strathmore University" to "Madaraka Campus Ole Sangale Rd",
        "Moi University" to "Main Campus Eldoret, Annex"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 28.dp)
    ) {
        // Hero Section - Startup Gradient with Glassmorphism highlights
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Navy900, Navy800, Color(0xFF1E293B))
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 28.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.12f))
                            .padding(horizontal = 12.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = OrangePrimary,
                            modifier = Modifier.size(8.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "OFFICIAL CAMPUS PEER MARKETPLACE",
                            color = OrangeBadge,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Give Good Products a\nSecond Life on Campus",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        lineHeight = 36.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Connect directly with verified students across Kenyan universities. Buy textbooks, laptops, hostel essentials and calculators at fair student prices.",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onNavigateToMarketplace,
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                            shape = RoundedCornerShape(12.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                            modifier = Modifier.testTag("hero_browse_button")
                        ) {
                            Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Explore Market", fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        OutlinedButton(
                            onClick = {
                                if (currentUser != null) onNavigateToSell()
                                else viewModel.openAuth("register")
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.6f)),
                            modifier = Modifier.testTag("hero_sell_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Post an Item", fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Live Statistics Bar
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "${allUsers.size}+", color = Color.White, fontWeight = FontWeight.Black, fontSize = 17.sp)
                                Text(text = "Verified Students", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                            }
                            Divider(modifier = Modifier.height(24.dp).width(1.dp), color = Color.White.copy(alpha = 0.15f))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "${allItems.filter { it.status == "approved" }.size}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 17.sp)
                                Text(text = "Active Listings", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                            }
                            Divider(modifier = Modifier.height(24.dp).width(1.dp), color = Color.White.copy(alpha = 0.15f))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "100%", color = OrangePrimary, fontWeight = FontWeight.Black, fontSize = 17.sp)
                                Text(text = "Staff Moderated", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }

        // Shop by Category Section
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)) {
                Text(
                    text = "Shop by Category",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                for (chunk in categories.chunked(4)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        chunk.forEach { cat ->
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        viewModel.setCategoryFilter(cat.categoryFilter)
                                        onNavigateToMarketplace()
                                    },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(OrangeLight),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = cat.icon,
                                            contentDescription = cat.name,
                                            tint = OrangePrimary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = cat.name,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Featured Campus Deals with Quick View and Favorites
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Trending on Campus",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    TextButton(onClick = onNavigateToMarketplace) {
                        Text("View All", color = OrangePrimary, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(featuredItems, key = { it.id }) { item ->
                    Box(modifier = Modifier.width(190.dp)) {
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

        // University Spotlight Section
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 22.dp)) {
                Text(
                    text = "University Campus Hubs",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Find deals right at your faculty gates and campus hostels.",
                    color = TextMuted,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                universitiesSpotlight.forEach { (name, branches) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clickable {
                                viewModel.selectedUniversity.value = name
                                onNavigateToMarketplace()
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = OrangeLight,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.School, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(20.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                                    Text(branches, fontSize = 10.sp, color = TextMuted)
                                }
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
                        }
                    }
                }
            }
        }

        // Campus Trust & Safety Walkthrough
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Safe Student Commerce",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                val steps = listOf(
                    Triple("1. Verified Profiles", "Students sign up with campus details and real WhatsApp contacts.", Icons.Default.VerifiedUser),
                    Triple("2. Moderated Listings", "All items are inspected by campus moderators before going live.", Icons.Default.Shield),
                    Triple("3. Daylight Inspection", "Meet at campus student centres, inspect condition, pay on delivery.", Icons.Default.Handshake)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    steps.forEach { (title, desc, icon) ->
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(OrangeLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = icon, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = desc, fontSize = 10.sp, color = TextMuted, lineHeight = 14.sp)
                            }
                        }
                    }
                }
            }
        }

        // Campus Testimonials
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 22.dp)) {
                Text(
                    text = "Student Stories",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                val testimonials = listOf(
                    Triple("Kevin M.", "UoN Civil Engineering", "Sold my Stroud Maths book within 2 hours of posting. Handed over at the library gate smoothly!"),
                    Triple("Sharon A.", "JKUAT Science", "Saved over KSh 10,000 getting an HP EliteBook for programming from a 4th year senior.")
                )

                testimonials.forEach { (student, university, quote) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "“$quote”",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 17.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(student, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = OrangePrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("• $university", fontSize = 11.sp, color = TextMuted)
                            }
                        }
                    }
                }
            }
        }

        // Frequently Asked Questions
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Frequently Asked Questions",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                val faqs = listOf(
                    "Is Bei Poa free for campus students?" to "Yes! Listing and discovering campus items is 100% free for all students.",
                    "How do I pay the seller?" to "You meet in a safe public spot on campus, inspect the item in person, and pay directly via M-Pesa or cash.",
                    "What happens if a listing has false details?" to "You can report any item directly through the product page. Appointed student moderators review and remove fraudulent posts."
                )

                faqs.forEachIndexed { index, (q, a) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                            .clickable {
                                expandedFaqIndex = if (expandedFaqIndex == index) null else index
                            },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(q, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                                Icon(
                                    imageVector = if (expandedFaqIndex == index) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = TextMuted
                                )
                            }
                            if (expandedFaqIndex == index) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(a, fontSize = 11.sp, color = TextMuted, lineHeight = 16.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Footer Links
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { onNavigateToLegal("terms") }) {
                        Text("Terms & Conditions", fontSize = 12.sp, color = TextMuted)
                    }
                    Text("•", color = TextMuted, fontSize = 12.sp)
                    TextButton(onClick = { onNavigateToLegal("privacy") }) {
                        Text("Privacy Policy", fontSize = 12.sp, color = TextMuted)
                    }
                }
            }
        }
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
