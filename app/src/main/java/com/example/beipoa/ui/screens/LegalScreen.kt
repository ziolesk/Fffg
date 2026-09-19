package com.example.beipoa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.beipoa.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalScreen(
    type: String, // "terms" or "privacy"
    onBack: () -> Unit
) {
    val isTerms = type == "terms"
    val title = if (isTerms) "Terms & Conditions" else "Privacy Policy"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
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
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (isTerms) "Bei Poa Campus Marketplace Terms" else "Bei Poa Privacy Policy",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Navy900
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Last updated: Campus Academic Year 2024/2025", fontSize = 11.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(14.dp))

                    if (isTerms) {
                        LegalSection("1. Campus Peer-to-Peer Platform", "Bei Poa provides a campus communication medium connecting university students. Bei Poa does not hold goods in custody or directly process payment escrows.")
                        LegalSection("2. Safety & Physical Meetups", "Students must conduct inspections in safe, public university locations (e.g., student centres, faculty gates, daylight hours).")
                        LegalSection("3. Prohibited Items", "Counterfeit currency, academic examination leaks, dangerous goods, and unlicensed substances are strictly forbidden and will result in immediate suspension.")
                        LegalSection("4. Listing Moderation", "All new product submissions are reviewed by appointed campus moderators to maintain trust and protect students from scams.")
                    } else {
                        LegalSection("1. Student Information Collected", "We collect your campus email, chosen display name, university affiliation, and WhatsApp contact phone number to facilitate buyer-seller connections.")
                        LegalSection("2. Contact Visibility", "Your WhatsApp contact is made available to fellow students who wish to purchase your listed items.")
                        LegalSection("3. Local Storage & Security", "Your session and profile settings are stored securely on your local device with industry standard safety practices.")
                        LegalSection("4. Account Deletion", "You can remove your listings or delete your student profile at any time through the profile dashboard.")
                    }
                }
            }
        }
    }
}

@Composable
private fun LegalSection(heading: String, body: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = heading, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Navy900)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = body, fontSize = 12.sp, color = Color(0xFF334155), lineHeight = 17.sp)
    }
}
