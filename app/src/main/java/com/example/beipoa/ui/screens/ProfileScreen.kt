package com.example.beipoa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.beipoa.ui.components.VerificationBadge
import com.example.beipoa.ui.theme.*
import com.example.beipoa.ui.viewmodel.MarketplaceViewModel

@Composable
fun ProfileScreen(
    viewModel: MarketplaceViewModel,
    onNavigateToLegal: (String) -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    var name by remember(currentUser) { mutableStateOf(currentUser?.name ?: "") }
    var university by remember(currentUser) { mutableStateOf(currentUser?.university ?: "") }
    var phone by remember(currentUser) { mutableStateOf(currentUser?.phone ?: "") }
    var bio by remember(currentUser) { mutableStateOf(currentUser?.bio ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("profile_screen")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Campus Profile",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Dark Mode switch
            IconButton(onClick = { viewModel.toggleDarkMode() }) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                    contentDescription = "Toggle Dark Mode",
                    tint = OrangePrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (currentUser == null) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("You are currently browsing as a guest.", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.openAuth("login") },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Log In or Register", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
            return
        }

        // Profile Hero Header with Cover Image & Avatar
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Navy900)
                ) {
                    if (currentUser?.coverImageUrl != null) {
                        AsyncImage(
                            model = currentUser?.coverImageUrl,
                            contentDescription = "Cover",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = OrangePrimary,
                            border = androidx.compose.foundation.BorderStroke(3.dp, MaterialTheme.colorScheme.surface),
                            modifier = Modifier
                                .size(68.dp)
                                .offset(y = (-30).dp)
                        ) {
                            if (currentUser?.avatarUrl != null) {
                                AsyncImage(
                                    model = currentUser?.avatarUrl,
                                    contentDescription = "Avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = currentUser?.name?.take(1)?.uppercase() ?: "U",
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 26.sp
                                    )
                                }
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${currentUser?.followersCount ?: 0}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Followers", fontSize = 10.sp, color = TextMuted)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${currentUser?.followingCount ?: 0}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Following", fontSize = 10.sp, color = TextMuted)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Class '${currentUser?.joinedYear ?: "24"}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Campus Year", fontSize = 10.sp, color = TextMuted)
                            }
                        }
                    }

                    Text(
                        text = currentUser?.name ?: "Student",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = currentUser?.email ?: "",
                        fontSize = 12.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = Navy900,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = (currentUser?.role ?: "USER").uppercase(),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        VerificationBadge()
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = currentUser?.bio ?: "",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Fast Demo Role Switcher
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Quick Demo Account Switcher:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.switchDemoUser("user-1") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = if (currentUser?.id == "user-1") ButtonDefaults.outlinedButtonColors(containerColor = Navy900, contentColor = Color.White) else ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text("Admin", fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = { viewModel.switchDemoUser("user-2") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = if (currentUser?.id == "user-2") ButtonDefaults.outlinedButtonColors(containerColor = OrangePrimary, contentColor = Color.White) else ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text("Mod", fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = { viewModel.switchDemoUser("user-3") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = if (currentUser?.id == "user-3") ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF334155), contentColor = Color.White) else ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text("Student", fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Edit Profile Form
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Edit Campus Profile", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = university,
                    onValueChange = { university = it },
                    label = { Text("University / Campus") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("WhatsApp Contact Phone") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Campus Bio / Course / Hostels") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.updateProfile(name, university, phone, bio)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_save_button")
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Logout Button
        OutlinedButton(
            onClick = { viewModel.logout() },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = RedError),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("profile_logout_button")
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null, tint = RedError, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Log Out", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Legal Links
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(onClick = { onNavigateToLegal("terms") }) {
                Text("Terms & Conditions", fontSize = 12.sp, color = TextMuted)
            }
            Text("•", color = TextMuted, modifier = Modifier.align(Alignment.CenterVertically))
            TextButton(onClick = { onNavigateToLegal("privacy") }) {
                Text("Privacy Policy", fontSize = 12.sp, color = TextMuted)
            }
        }
    }
}
