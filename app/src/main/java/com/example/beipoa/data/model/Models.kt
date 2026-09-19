package com.example.beipoa.data.model

import java.util.UUID

data class MarketItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val price: Double,
    val category: String,
    val condition: String, // "Like New", "Good", "Fair", "Used"
    val university: String,
    val imageUrl: String? = null,
    val additionalImages: List<String> = emptyList(),
    val contact: String, // WhatsApp phone e.g. +254712345678
    val sellerId: String,
    val sellerName: String,
    val sellerEmail: String,
    val sellerVerified: Boolean = true,
    val status: String = "approved", // "pending", "approved", "rejected"
    val sold: Boolean = false,
    val views: Int = 24,
    val favoritesCount: Int = 5,
    val createdAt: Long = System.currentTimeMillis()
)

data class UserProfile(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val email: String,
    val university: String = "University of Nairobi",
    val phone: String = "+254712345678",
    val bio: String = "Campus student & marketplace trader",
    val role: String = "user", // "user", "moderator", "admin"
    val status: String = "active", // "active", "banned"
    val isVerified: Boolean = true,
    val avatarUrl: String? = null,
    val coverImageUrl: String? = null,
    val followersCount: Int = 18,
    val followingCount: Int = 12,
    val joinedYear: String = "2024",
    val telegramHandle: String? = null,
    val twitterHandle: String? = null
)

data class MarketReport(
    val id: String = UUID.randomUUID().toString(),
    val itemId: String,
    val itemTitle: String,
    val sellerId: String,
    val sellerName: String,
    val sellerEmail: String,
    val reason: String, // "scam", "fake", "inappropriate", "wrong_info", "other"
    val details: String,
    val reporterId: String,
    val reporterName: String,
    val status: String = "open", // "open", "resolved", "dismissed"
    val createdAt: Long = System.currentTimeMillis()
)

data class CampusMessage(
    val id: String = UUID.randomUUID().toString(),
    val conversationId: String = UUID.randomUUID().toString(),
    val itemId: String,
    val itemTitle: String,
    val itemPrice: Double = 0.0,
    val itemImageUrl: String? = null,
    val senderId: String = "",
    val senderName: String = "",
    val recipientId: String = "",
    val otherUserName: String,
    val otherUserPhone: String,
    val lastMessage: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFromMe: Boolean = false,
    val status: MessageStatus = MessageStatus.READ, // SENT, DELIVERED, READ
    val isOnline: Boolean = true,
    val typingText: String? = null,
    val attachmentType: String? = null // "image", "file", "location"
)

enum class MessageStatus {
    SENT, DELIVERED, READ
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val senderId: String,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFromMe: Boolean,
    val status: MessageStatus = MessageStatus.READ,
    val mediaUrl: String? = null,
    val mediaType: String? = null // "image", "file"
)

data class NotificationItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val type: NotificationType,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val targetId: String? = null
)

enum class NotificationType {
    LISTING_APPROVED,
    NEW_MESSAGE,
    PRICE_DROP,
    SECURITY_ALERT,
    SYSTEM
}

enum class ProductCategory(val label: String, val iconName: String) {
    TEXTBOOKS("Textbooks", "book"),
    NOTES("Notes / PDFs", "file"),
    ELECTRONICS("Electronics", "laptop"),
    FURNITURE("Furniture", "couch"),
    OTHER("Other", "box")
}
