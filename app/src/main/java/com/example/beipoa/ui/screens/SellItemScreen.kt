package com.example.beipoa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.beipoa.ui.theme.*
import com.example.beipoa.ui.viewmodel.MarketplaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellItemScreen(
    viewModel: MarketplaceViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Textbooks") }
    var condition by remember { mutableStateOf("Like New") }
    var university by remember { mutableStateOf(currentUser?.university ?: "University of Nairobi") }
    var contact by remember { mutableStateOf(currentUser?.phone ?: "+254712345678") }
    var imageUrl by remember { mutableStateOf("") }

    val categories = listOf("Textbooks", "Electronics", "Furniture", "Notes / PDFs", "Other")
    val conditions = listOf("Like New", "Good", "Fair", "Used")
    val sampleImages = listOf(
        "Laptop" to "https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=600",
        "Book" to "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600",
        "Calculator" to "https://images.unsplash.com/photo-1611125832047-1d7ad1e8e48f?w=600",
        "Chair" to "https://images.unsplash.com/photo-1580481077195-c9c0b13cf41f?w=600",
        "Notes" to "https://images.unsplash.com/photo-1517842645767-c639042777db?w=600"
    )

    var categoryMenuExpanded by remember { mutableStateOf(false) }
    var conditionMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post Item for Sale", fontWeight = FontWeight.Bold) },
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
                .testTag("sell_item_screen")
        ) {
            // Notice card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AmberWarningBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = AmberWarningText, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "New listings go to Pending until verified by campus moderators for student safety.",
                        color = AmberWarningText,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Form
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title *") },
                        placeholder = { Text("e.g. Engineering Mathematics Stroud 5th Ed") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sell_title_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        placeholder = { Text("Condition details, specs, included accessories...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .testTag("sell_desc_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = { priceStr = it },
                        label = { Text("Price in KES *") },
                        placeholder = { Text("e.g. 1500") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sell_price_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Category & Condition Pickers
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Category Dropdown
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedCard(
                                onClick = { categoryMenuExpanded = true },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Category *", fontSize = 11.sp, color = TextMuted)
                                    Text(category, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                            DropdownMenu(
                                expanded = categoryMenuExpanded,
                                onDismissRequest = { categoryMenuExpanded = false }
                            ) {
                                categories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat) },
                                        onClick = {
                                            category = cat
                                            categoryMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Condition Dropdown
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedCard(
                                onClick = { conditionMenuExpanded = true },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Condition", fontSize = 11.sp, color = TextMuted)
                                    Text(condition, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                            DropdownMenu(
                                expanded = conditionMenuExpanded,
                                onDismissRequest = { conditionMenuExpanded = false }
                            ) {
                                conditions.forEach { cond ->
                                    DropdownMenuItem(
                                        text = { Text(cond) },
                                        onClick = {
                                            condition = cond
                                            conditionMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = university,
                        onValueChange = { university = it },
                        label = { Text("Campus / University *") },
                        placeholder = { Text("e.g. Kenyatta University") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sell_university_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = contact,
                        onValueChange = { contact = it },
                        label = { Text("WhatsApp Phone Contact *") },
                        placeholder = { Text("+254712345678") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sell_contact_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        label = { Text("Image URL (optional)") },
                        placeholder = { Text("https://...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("sell_image_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Or choose sample photo preset:", fontSize = 11.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        sampleImages.forEach { (label, url) ->
                            AssistChip(
                                onClick = { imageUrl = url },
                                label = { Text(label, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            val price = priceStr.toDoubleOrNull() ?: 0.0
                            viewModel.postItem(
                                title = title,
                                description = description,
                                price = price,
                                category = category,
                                condition = condition,
                                university = university,
                                contact = contact,
                                imageUrl = imageUrl.ifBlank { "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600" }
                            )
                            onSuccess()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("sell_submit_button")
                    ) {
                        Text("Submit for Review", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}
