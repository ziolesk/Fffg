package com.example.beipoa.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.beipoa.ui.components.ConditionBadge
import com.example.beipoa.ui.components.SafetyTipsCard
import com.example.beipoa.ui.components.VerificationBadge
import com.example.beipoa.ui.components.formatKes
import com.example.beipoa.ui.theme.*
import com.example.beipoa.ui.viewmodel.MarketplaceViewModel
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    itemId: String,
    viewModel: MarketplaceViewModel,
    onBack: () -> Unit,
    onNavigateToCheckout: (String) -> Unit,
    onNavigateToMessages: () -> Unit
) {
    val items by viewModel.allItems.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val comparisonItems by viewModel.comparisonItems.collectAsState()
    val item = items.find { it.id == itemId }
    val context = LocalContext.current

    LaunchedEffect(itemId) {
        viewModel.recordView(itemId)
    }

    if (item == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Product listing not found", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onBack) { Text("Back to Marketplace") }
            }
        }
        return
    }

    val isFav = favorites.contains(item.id)
    val isInCompare = comparisonItems.any { it.id == item.id }

    val allGalleryImages = remember(item) {
        val list = mutableListOf<String>()
        item.imageUrl?.let { list.add(it) }
        list.addAll(item.additionalImages)
        if (list.isEmpty()) {
            list.add("https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=800")
        }
        list
    }
    var selectedImageIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite(item.id) }) {
                        Icon(
                            imageVector = if (isFav) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFav) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = { viewModel.toggleComparison(item) }) {
                        Icon(
                            imageVector = Icons.Outlined.CompareArrows,
                            contentDescription = "Compare",
                            tint = if (isInCompare) OrangePrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = { viewModel.openReportDialog(item) }) {
                        Icon(Icons.Outlined.Flag, contentDescription = "Report Listing", tint = RedErrorText)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Chat directly inside Bei Poa
                    OutlinedButton(
                        onClick = {
                            viewModel.startChatWithSeller(item)
                            onNavigateToMessages()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("In-App Chat", color = OrangePrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    // Direct WhatsApp deal button
                    Button(
                        onClick = {
                            val cleanNumber = item.contact.replace(Regex("[^0-9+]"), "").removePrefix("+")
                            val textMsg = "Hello ${item.sellerName}, I am contacting you from the Bei Poa campus marketplace regarding your listing: '${item.title}' (Price: ${formatKes(item.price)}). Is this still available for campus inspection?"
                            val encoded = URLEncoder.encode(textMsg, "UTF-8")
                            val uri = Uri.parse("https://wa.me/$cleanNumber?text=$encoded")
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                viewModel.showMessage("WhatsApp not installed. Contact number: ${item.contact}")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenSuccessText),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1.2f)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("WhatsApp", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .testTag("product_detail_screen")
        ) {
            // Main Gallery Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(Color(0xFFE2E8F0))
            ) {
                AsyncImage(
                    model = allGalleryImages.getOrElse(selectedImageIndex) { allGalleryImages.first() },
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                if (item.sold) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(color = OrangePrimary, shape = RoundedCornerShape(8.dp)) {
                            Text(
                                text = "ITEM SOLD",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Stats badge overlay
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${item.views} views", color = Color.White, fontSize = 11.sp)
                    }
                }
            }

            // Thumbnail Carousel if multiple images
            if (allGalleryImages.size > 1) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(allGalleryImages.indices.toList()) { index ->
                        val isSelected = selectedImageIndex == index
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) OrangePrimary else BorderLight,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedImageIndex = index }
                        ) {
                            AsyncImage(
                                model = allGalleryImages[index],
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Price and Condition row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatKes(item.price),
                        color = OrangePrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp
                    )
                    ConditionBadge(condition = item.condition)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Title
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // University Campus Badge
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.School, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = item.university,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Verified Student Seller Profile Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = OrangePrimary,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = item.sellerName.take(1).uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = item.sellerName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                VerificationBadge()
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "WhatsApp: ${item.contact}",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Product Description
                Text(
                    text = "Description & Item Condition",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Safety Tips Section
                SafetyTipsCard()

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
