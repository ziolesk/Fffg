package com.example.beipoa.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.beipoa.data.model.CampusMessage
import com.example.beipoa.data.model.ChatMessage
import com.example.beipoa.ui.components.formatKes
import com.example.beipoa.ui.theme.*
import com.example.beipoa.ui.viewmodel.MarketplaceViewModel
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    viewModel: MarketplaceViewModel,
    onNavigateToProduct: (String) -> Unit
) {
    val conversations by viewModel.conversations.collectAsState()
    val chatMessagesMap by viewModel.chatMessages.collectAsState()
    val activeConversationId by viewModel.activeConversationId.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val context = LocalContext.current

    var messageInputText by remember { mutableStateOf("") }

    val activeConv = conversations.find { it.conversationId == activeConversationId }
    val currentMessages = activeConversationId?.let { chatMessagesMap[it] } ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (activeConv != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { viewModel.activeConversationId.value = null },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(activeConv.otherUserName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Online • ${activeConv.itemTitle}", fontSize = 11.sp, color = GreenSuccessText)
                            }
                        }
                    } else {
                        Text("Campus Messages", fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    if (activeConv != null) {
                        IconButton(onClick = {
                            val cleanNumber = activeConv.otherUserPhone.replace(Regex("[^0-9+]"), "").removePrefix("+")
                            val textMsg = "Hello ${activeConv.otherUserName}, continuing our chat from Bei Poa regarding '${activeConv.itemTitle}'."
                            val uri = Uri.parse("https://wa.me/$cleanNumber?text=${URLEncoder.encode(textMsg, "UTF-8")}")
                            try {
                                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                            } catch (e: Exception) {
                                viewModel.showMessage("WhatsApp not found. Contact: ${activeConv.otherUserPhone}")
                            }
                        }) {
                            Icon(Icons.Default.Phone, contentDescription = "WhatsApp", tint = GreenSuccessText)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .testTag("messages_screen")
        ) {
            if (activeConv == null) {
                // Conversation list
                if (conversations.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = TextMuted, modifier = Modifier.size(52.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No campus inquiries yet", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("When students contact you about your items, messages appear here.", color = TextMuted, fontSize = 12.sp)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(conversations, key = { it.conversationId }) { conv ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.openConversation(conv.conversationId) },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box {
                                        Surface(
                                            shape = CircleShape,
                                            color = OrangeLight,
                                            modifier = Modifier.size(46.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = conv.otherUserName.take(1).uppercase(),
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 18.sp,
                                                    color = OrangePrimary
                                                )
                                            }
                                        }
                                        if (conv.isOnline) {
                                            Surface(
                                                shape = CircleShape,
                                                color = GreenSuccessText,
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .align(Alignment.BottomEnd)
                                            ) {}
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = conv.otherUserName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                                            Text(
                                                text = timeFormat.format(Date(conv.timestamp)),
                                                fontSize = 11.sp,
                                                color = TextMuted
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Text(
                                            text = conv.itemTitle,
                                            fontSize = 11.sp,
                                            color = OrangePrimary,
                                            fontWeight = FontWeight.SemiBold
                                        )

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Text(
                                            text = conv.lastMessage,
                                            fontSize = 12.sp,
                                            color = TextMuted,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Active Chat Window
                Column(modifier = Modifier.fillMaxSize()) {
                    // Item context card at top of chat
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                                .clickable { onNavigateToProduct(activeConv.itemId) },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (activeConv.itemImageUrl != null) {
                                    AsyncImage(
                                        model = activeConv.itemImageUrl,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Column {
                                    Text(activeConv.itemTitle, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(formatKes(activeConv.itemPrice), color = OrangePrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                            Text("View Item >", fontSize = 11.sp, color = OrangePrimary, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Chat message bubbles
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(currentMessages, key = { it.id }) { msg ->
                            val isMe = msg.isFromMe
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(
                                        topStart = 14.dp,
                                        topEnd = 14.dp,
                                        bottomStart = if (isMe) 14.dp else 2.dp,
                                        bottomEnd = if (isMe) 2.dp else 14.dp
                                    ),
                                    color = if (isMe) OrangePrimary else MaterialTheme.colorScheme.surface,
                                    shadowElevation = 1.dp,
                                    modifier = Modifier.widthIn(max = 280.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = msg.text,
                                            color = if (isMe) Color.White else MaterialTheme.colorScheme.onSurface,
                                            fontSize = 13.sp,
                                            lineHeight = 18.sp
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(msg.timestamp))
                                        Text(
                                            text = timeStr,
                                            color = if (isMe) Color.White.copy(alpha = 0.75f) else TextMuted,
                                            fontSize = 10.sp,
                                            modifier = Modifier.align(Alignment.End)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Chat Input Bar
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 4.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = messageInputText,
                                onValueChange = { messageInputText = it },
                                placeholder = { Text("Type student message...") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(20.dp),
                                maxLines = 3
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    if (messageInputText.isNotBlank()) {
                                        viewModel.sendChatMessage(activeConv.conversationId, messageInputText)
                                        messageInputText = ""
                                    }
                                },
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(OrangePrimary, CircleShape)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
