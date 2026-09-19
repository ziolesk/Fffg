package com.example.beipoa.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.beipoa.ui.components.formatKes
import com.example.beipoa.ui.theme.*
import com.example.beipoa.ui.viewmodel.MarketplaceViewModel
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    itemId: String,
    viewModel: MarketplaceViewModel,
    onBack: () -> Unit
) {
    val items by viewModel.allItems.collectAsState()
    val item = items.find { it.id == itemId }
    val context = LocalContext.current

    if (item == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Listing not found")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Complete Your Deal", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceCard)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .testTag("checkout_screen")
        ) {
            // 3-Step Campus Deal Indicator
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StepItem(number = "1", title = "Contact", isActive = true)
                    Text("—", color = BorderLight, fontWeight = FontWeight.Bold)
                    StepItem(number = "2", title = "Meet", isActive = false)
                    Text("—", color = BorderLight, fontWeight = FontWeight.Bold)
                    StepItem(number = "3", title = "Pay & Collect", isActive = false)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Order Summary Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Deal Summary",
                        style = MaterialTheme.typography.titleMedium,
                        color = Navy900
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = item.imageUrl ?: "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600",
                            contentDescription = item.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formatKes(item.price),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = OrangePrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = BorderLight)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Item Price", color = TextMuted, fontSize = 13.sp)
                        Text(formatKes(item.price), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Platform Service Fee", color = TextMuted, fontSize = 13.sp)
                        Text("FREE (KSh 0)", color = GreenSuccess, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = BorderLight)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Payable to Seller", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Navy900)
                        Text(formatKes(item.price), fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, color = OrangePrimary)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Notice: Payment happens directly in person between you and the seller upon physical item inspection. Bei Poa does not hold or process escrow payments.",
                        fontSize = 11.sp,
                        color = TextMuted,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // How it works card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF7ED)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Next Steps to Complete Deal:",
                        fontWeight = FontWeight.Bold,
                        color = OrangeDark,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val steps = listOf(
                        "1. Send pre-composed greeting to seller on WhatsApp",
                        "2. Agree on campus meeting location (e.g. Hostels or Student Centre)",
                        "3. Inspect item quality and test thoroughly",
                        "4. Send payment via M-Pesa / Cash directly"
                    )
                    steps.forEach { step ->
                        Text(step, fontSize = 12.sp, color = Color(0xFF7C2D12), modifier = Modifier.padding(vertical = 2.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Direct WhatsApp Button
            Button(
                onClick = {
                    val cleanPhone = item.contact.replace(Regex("[^0-9]"), "")
                    val msg = "Hi ${item.sellerName}, I saw your '${item.title}' on Bei Poa for ${formatKes(item.price)}. Is it available to meet on campus?"
                    val encoded = try { URLEncoder.encode(msg, "UTF-8") } catch (e: Exception) { "" }
                    val waUrl = "https://wa.me/$cleanPhone?text=$encoded"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(waUrl))
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        viewModel.showMessage("Could not launch WhatsApp. Seller contact: ${item.contact}")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("checkout_whatsapp_button")
            ) {
                Text("Message Seller on WhatsApp", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onBack,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Back to Listing", color = TextMuted)
            }
        }
    }
}

@Composable
private fun StepItem(number: String, title: String, isActive: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(if (isActive) OrangePrimary else Color(0xFFE2E8F0)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = if (isActive) Color.White else TextMuted,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            color = if (isActive) OrangePrimary else TextMuted
        )
    }
}
