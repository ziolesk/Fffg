package com.example.beipoa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.beipoa.data.model.MarketReport
import com.example.beipoa.data.model.UserProfile
import com.example.beipoa.ui.components.StatTile
import com.example.beipoa.ui.components.StatusBadge
import com.example.beipoa.ui.components.formatKes
import com.example.beipoa.ui.theme.*
import com.example.beipoa.ui.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(viewModel: MarketplaceViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allItems by viewModel.allItems.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val allReports by viewModel.allReports.collectAsState()

    val isStaff = currentUser?.role == "admin" || currentUser?.role == "moderator"
    val isAdmin = currentUser?.role == "admin"

    if (!isStaff) {
        Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = RedError, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Campus Staff Access Only", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("This panel is reserved for campus moderators and admins.", color = TextMuted, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.switchDemoUser("user-1") },
                        colors = ButtonDefaults.buttonColors(containerColor = Navy900)
                    ) {
                        Text("Switch to Admin Demo (Peter Yator)", color = Color.White)
                    }
                }
            }
        }
        return
    }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = if (isAdmin) listOf("Overview", "Products", "Reports", "Users") else listOf("Overview", "Products", "Reports")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .testTag("admin_screen")
    ) {
        // Top Header
        Surface(color = Navy900, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Campus Moderation & Admin", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                Text("Logged in as ${currentUser?.name} (${currentUser?.role?.uppercase()})", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
            }
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = SurfaceCard,
            contentColor = OrangePrimary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
                )
            }
        }

        when (selectedTab) {
            0 -> AdminOverviewTab(allUsers, allItems, allReports, viewModel)
            1 -> AdminProductsTab(allItems, viewModel)
            2 -> AdminReportsTab(allReports, viewModel)
            3 -> if (isAdmin) AdminUsersTab(allUsers, viewModel)
        }
    }
}

@Composable
private fun AdminOverviewTab(
    users: List<UserProfile>,
    items: List<MarketItem>,
    reports: List<MarketReport>,
    viewModel: MarketplaceViewModel
) {
    val pending = items.count { it.status == "pending" }
    val openReports = reports.count { it.status == "open" }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatTile("Total Students", "${users.size}", Navy900, modifier = Modifier.weight(1f))
                StatTile("Campus Listings", "${items.size}", OrangePrimary, modifier = Modifier.weight(1f))
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatTile("Pending Review", "$pending", AmberWarning, modifier = Modifier.weight(1f))
                StatTile("Open Reports", "$openReports", RedError, modifier = Modifier.weight(1f))
            }
        }
        if (pending > 0) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AmberWarningBg),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Pending Moderation Queue", fontWeight = FontWeight.Bold, color = AmberWarningText)
                            Text("$pending student listings await approval.", fontSize = 12.sp, color = AmberWarningText)
                        }
                        Button(
                            onClick = { viewModel.approveAllPending() },
                            colors = ButtonDefaults.buttonColors(containerColor = AmberWarningText)
                        ) {
                            Text("Approve All", color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminProductsTab(
    items: List<MarketItem>,
    viewModel: MarketplaceViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items, key = { it.id }) { item ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))
                        StatusBadge(status = item.status, isSold = item.sold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("${formatKes(item.price)} • ${item.university} • Seller: ${item.sellerName}", fontSize = 12.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (item.status != "approved") {
                            Button(
                                onClick = { viewModel.approveItem(item.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Approve", fontSize = 11.sp, color = Color.White)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        if (item.status != "rejected") {
                            OutlinedButton(
                                onClick = { viewModel.rejectItem(item.id) },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberWarning)
                            ) {
                                Text("Reject", fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        IconButton(onClick = { viewModel.deleteItem(item.id) }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedError, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminReportsTab(
    reports: List<MarketReport>,
    viewModel: MarketplaceViewModel
) {
    if (reports.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No reports filed yet", color = TextMuted)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(reports, key = { it.id }) { report ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(report.itemTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))
                        Surface(
                            color = if (report.status == "open") RedErrorBg else GreenSuccessBg,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = report.status.uppercase(),
                                color = if (report.status == "open") RedErrorText else GreenSuccessText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Reason: ${report.reason.uppercase()} | Reporter: ${report.reporterName}", fontSize = 12.sp, color = RedError)
                    if (report.details.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Details: ${report.details}", fontSize = 12.sp, color = TextPrimary)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Reported Seller: ${report.sellerName} (${report.sellerEmail})", fontSize = 11.sp, color = TextMuted)

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        if (report.status == "open") {
                            Button(
                                onClick = { viewModel.resolveReport(report.id, "resolved") },
                                colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Resolve", fontSize = 11.sp, color = Color.White)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            OutlinedButton(
                                onClick = { viewModel.resolveReport(report.id, "dismissed") },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Dismiss", fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = { viewModel.banUser(report.sellerId) },
                                colors = ButtonDefaults.buttonColors(containerColor = RedError),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Ban Seller", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminUsersTab(
    users: List<UserProfile>,
    viewModel: MarketplaceViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(users, key = { it.id }) { user ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(user.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Surface(
                                color = Navy900,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = user.role.uppercase(),
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Surface(
                                color = if (user.status == "banned") RedErrorBg else GreenSuccessBg,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = user.status.uppercase(),
                                    color = if (user.status == "banned") RedErrorText else GreenSuccessText,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("${user.email} • ${user.university}", fontSize = 12.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        if (user.role == "user") {
                            OutlinedButton(
                                onClick = { viewModel.setUserRole(user.id, "moderator") },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Make Mod", fontSize = 11.sp)
                            }
                        } else if (user.role == "moderator") {
                            OutlinedButton(
                                onClick = { viewModel.setUserRole(user.id, "user") },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Demote", fontSize = 11.sp)
                            }
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        if (user.status == "active") {
                            Button(
                                onClick = { viewModel.banUser(user.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = RedError),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Ban", fontSize = 11.sp, color = Color.White)
                            }
                        } else {
                            Button(
                                onClick = { viewModel.unbanUser(user.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("Unban", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
