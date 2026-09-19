package com.example.beipoa.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.beipoa.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class MarketplaceRepository(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("beipoa_prefs", Context.MODE_PRIVATE)

    private val _items = MutableStateFlow<List<MarketItem>>(emptyList())
    val items: StateFlow<List<MarketItem>> = _items.asStateFlow()

    private val _users = MutableStateFlow<List<UserProfile>>(emptyList())
    val users: StateFlow<List<UserProfile>> = _users.asStateFlow()

    private val _reports = MutableStateFlow<List<MarketReport>>(emptyList())
    val reports: StateFlow<List<MarketReport>> = _reports.asStateFlow()

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    private val _conversations = MutableStateFlow<List<CampusMessage>>(emptyList())
    val conversations: StateFlow<List<CampusMessage>> = _conversations.asStateFlow()

    private val _chatMessages = MutableStateFlow<Map<String, List<ChatMessage>>>(emptyMap())
    val chatMessages: StateFlow<Map<String, List<ChatMessage>>> = _chatMessages.asStateFlow()

    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    private val _savedSearches = MutableStateFlow<List<String>>(listOf("Engineering text", "Casio", "Hostel fridge"))
    val savedSearches: StateFlow<List<String>> = _savedSearches.asStateFlow()

    private val _darkMode = MutableStateFlow(prefs.getBoolean("dark_mode", false))
    val darkMode: StateFlow<Boolean> = _darkMode.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        // Default Users with verified credentials
        val defaultUsers = listOf(
            UserProfile(
                id = "user-1",
                name = "Peter Yator",
                email = "peteryator500@gmail.com",
                university = "University of Nairobi",
                phone = "+254712345678",
                bio = "Engineering student & campus marketplace administrator. Building safe student tech.",
                role = "admin",
                status = "active",
                isVerified = true,
                avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200",
                coverImageUrl = "https://images.unsplash.com/photo-1541339907198-e08756dedf3f?w=800",
                followersCount = 42,
                followingCount = 19,
                telegramHandle = "@peteryator",
                twitterHandle = "@yator_eng"
            ),
            UserProfile(
                id = "user-2",
                name = "Faith Mwangi",
                email = "faith.mwangi@students.ku.ac.ke",
                university = "Kenyatta University",
                phone = "+254722987654",
                bio = "Economics 3rd year. Hostels Nyayo 2. Selling verified course books & electronics.",
                role = "moderator",
                status = "active",
                isVerified = true,
                avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200",
                coverImageUrl = "https://images.unsplash.com/photo-1523050854058-8df90110c9f1?w=800",
                followersCount = 28,
                followingCount = 15,
                telegramHandle = "@faith_mwangi"
            ),
            UserProfile(
                id = "user-3",
                name = "Brian Ochieng",
                email = "brian.ochieng@jkuat.ac.ke",
                university = "JKUAT",
                phone = "+254733445566",
                bio = "Computer Science enthusiast. Hostels Hall 6. Fast responder.",
                role = "user",
                status = "active",
                isVerified = true,
                avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200",
                coverImageUrl = "https://images.unsplash.com/photo-1519452635265-7b1fbfd1e4e0?w=800",
                followersCount = 14,
                followingCount = 20
            )
        )
        _users.value = defaultUsers

        // Set logged-in user
        val savedUserId = prefs.getString("current_user_id", "user-1")
        _currentUser.value = defaultUsers.find { it.id == savedUserId } ?: defaultUsers.first()

        // Real verified campus listings with real details
        val initialItems = listOf(
            MarketItem(
                id = "item-1",
                title = "HP EliteBook 840 G5 (i5 8th Gen, 16GB RAM, 512GB SSD)",
                description = "Clean metallic body, backlit keyboard, genuine charger included, 5-hour battery health. Perfect for programming, CAD, and campus coursework. Tested and functional.",
                price = 32000.0,
                category = "Electronics",
                condition = "Like New",
                university = "University of Nairobi",
                imageUrl = "https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=800&auto=format&fit=crop&q=80",
                additionalImages = listOf(
                    "https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=800",
                    "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800",
                    "https://images.unsplash.com/photo-1611186871348-b1ce696e52c9?w=800"
                ),
                contact = "+254712345678",
                sellerId = "user-1",
                sellerName = "Peter Yator",
                sellerEmail = "peteryator500@gmail.com",
                sellerVerified = true,
                status = "approved",
                sold = false,
                views = 142,
                favoritesCount = 28
            ),
            MarketItem(
                id = "item-2",
                title = "Engineering Mathematics 5th Ed - K.A. Stroud",
                description = "Complete textbook in very good condition with no torn pages. Crucial for 1st & 2nd year engineering & physics students at UoN, KU, and JKUAT.",
                price = 1800.0,
                category = "Textbooks",
                condition = "Good",
                university = "Kenyatta University",
                imageUrl = "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=800&auto=format&fit=crop&q=80",
                additionalImages = listOf(
                    "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=800",
                    "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=800"
                ),
                contact = "+254722987654",
                sellerId = "user-2",
                sellerName = "Faith Mwangi",
                sellerEmail = "faith.mwangi@students.ku.ac.ke",
                sellerVerified = true,
                status = "approved",
                sold = false,
                views = 89,
                favoritesCount = 12
            ),
            MarketItem(
                id = "item-3",
                title = "Casio FX-991EX ClassWiz Scientific Calculator",
                description = "Original Casio calculator with natural textbook display and solar backup. Used for only one semester in engineering classes.",
                price = 2200.0,
                category = "Electronics",
                condition = "Like New",
                university = "JKUAT",
                imageUrl = "https://images.unsplash.com/photo-1611125832047-1d7ad1e8e48f?w=800&auto=format&fit=crop&q=80",
                additionalImages = listOf(
                    "https://images.unsplash.com/photo-1611125832047-1d7ad1e8e48f?w=800"
                ),
                contact = "+254733445566",
                sellerId = "user-3",
                sellerName = "Brian Ochieng",
                sellerEmail = "brian.ochieng@jkuat.ac.ke",
                sellerVerified = true,
                status = "approved",
                sold = false,
                views = 64,
                favoritesCount = 9
            ),
            MarketItem(
                id = "item-4",
                title = "Compact Hostel Mini Fridge (45 Litres)",
                description = "Low power consumption, perfect for hostel rooms. Keeps drinks ice cold and includes mini freezer compartment. Moving out sale!",
                price = 9500.0,
                category = "Furniture",
                condition = "Good",
                university = "Strathmore University",
                imageUrl = "https://images.unsplash.com/photo-1584992236310-6edddc08acff?w=800&auto=format&fit=crop&q=80",
                additionalImages = listOf(
                    "https://images.unsplash.com/photo-1584992236310-6edddc08acff?w=800"
                ),
                contact = "+254722987654",
                sellerId = "user-2",
                sellerName = "Faith Mwangi",
                sellerEmail = "faith.mwangi@students.ku.ac.ke",
                sellerVerified = true,
                status = "approved",
                sold = false,
                views = 210,
                favoritesCount = 43
            ),
            MarketItem(
                id = "item-5",
                title = "Medical Physiology & Anatomy Revision Summaries (Printed)",
                description = "Hand-curated colored notes and past paper solutions covering Anatomy, Physiology, and Pathology. Very neat and organized in binder.",
                price = 750.0,
                category = "Notes / PDFs",
                condition = "Like New",
                university = "University of Nairobi",
                imageUrl = "https://images.unsplash.com/photo-1517842645767-c639042777db?w=800&auto=format&fit=crop&q=80",
                contact = "+254712345678",
                sellerId = "user-1",
                sellerName = "Peter Yator",
                sellerEmail = "peteryator500@gmail.com",
                sellerVerified = true,
                status = "approved",
                sold = false,
                views = 76,
                favoritesCount = 15
            ),
            MarketItem(
                id = "item-6",
                title = "Ergonomic Mesh Study Chair with Lumbar Support",
                description = "Adjustable height, smooth rolling castors, breathable mesh back. Ideal for long study and exam prep nights in hostels.",
                price = 4500.0,
                category = "Furniture",
                condition = "Good",
                university = "JKUAT",
                imageUrl = "https://images.unsplash.com/photo-1580481077195-c9c0b13cf41f?w=800&auto=format&fit=crop&q=80",
                contact = "+254733445566",
                sellerId = "user-3",
                sellerName = "Brian Ochieng",
                sellerEmail = "brian.ochieng@jkuat.ac.ke",
                sellerVerified = true,
                status = "approved",
                sold = false,
                views = 115,
                favoritesCount = 18
            ),
            MarketItem(
                id = "item-7",
                title = "Laboratory Coat (Size Large) + Protective Safety Specs",
                description = "100% white cotton lab coat with embroidered campus patch space and clear anti-fog safety spectacles.",
                price = 1100.0,
                category = "Other",
                condition = "Like New",
                university = "Moi University",
                imageUrl = "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800&auto=format&fit=crop&q=80",
                contact = "+254712345678",
                sellerId = "user-1",
                sellerName = "Peter Yator",
                sellerEmail = "peteryator500@gmail.com",
                sellerVerified = true,
                status = "pending",
                sold = false,
                views = 31,
                favoritesCount = 4
            )
        )
        _items.value = initialItems

        // Initial Sample Reports for Moderation
        val initialReports = listOf(
            MarketReport(
                id = "report-1",
                itemId = "item-7",
                itemTitle = "Laboratory Coat (Size Large) + Protective Safety Specs",
                sellerId = "user-1",
                sellerName = "Peter Yator",
                sellerEmail = "peteryator500@gmail.com",
                reason = "wrong_info",
                details = "Seller WhatsApp number took a while to reply yesterday.",
                reporterId = "user-3",
                reporterName = "Brian Ochieng",
                status = "open"
            )
        )
        _reports.value = initialReports

        // Real-time Chat Conversations
        val initialConversations = listOf(
            CampusMessage(
                id = "conv-1",
                conversationId = "conv-1",
                itemId = "item-1",
                itemTitle = "HP EliteBook 840 G5",
                itemPrice = 32000.0,
                itemImageUrl = "https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=400",
                senderId = "user-2",
                senderName = "Faith Mwangi",
                recipientId = "user-1",
                otherUserName = "Faith Mwangi",
                otherUserPhone = "+254722987654",
                lastMessage = "Hi Peter! Can we meet at UoN Main Campus Library gate at 2 PM?",
                timestamp = System.currentTimeMillis() - 15 * 60 * 1000,
                isFromMe = false,
                status = MessageStatus.READ,
                isOnline = true
            ),
            CampusMessage(
                id = "conv-2",
                conversationId = "conv-2",
                itemId = "item-3",
                itemTitle = "Casio FX-991EX Calculator",
                itemPrice = 2200.0,
                itemImageUrl = "https://images.unsplash.com/photo-1611125832047-1d7ad1e8e48f?w=400",
                senderId = "user-3",
                senderName = "Brian Ochieng",
                recipientId = "user-1",
                otherUserName = "Brian Ochieng",
                otherUserPhone = "+254733445566",
                lastMessage = "Is the calculator still available for KSh 2,000?",
                timestamp = System.currentTimeMillis() - 2 * 3600 * 1000,
                isFromMe = false,
                status = MessageStatus.DELIVERED,
                isOnline = false
            ),
            CampusMessage(
                id = "conv-3",
                conversationId = "conv-3",
                itemId = "item-4",
                itemTitle = "Hostel Mini Fridge",
                itemPrice = 9500.0,
                itemImageUrl = "https://images.unsplash.com/photo-1584992236310-6edddc08acff?w=400",
                senderId = "user-1",
                senderName = "Peter Yator",
                recipientId = "user-2",
                otherUserName = "Faith Mwangi",
                otherUserPhone = "+254722987654",
                lastMessage = "Yes, fridge is in great shape! You can inspect it today.",
                timestamp = System.currentTimeMillis() - 24 * 3600 * 1000,
                isFromMe = true,
                status = MessageStatus.READ,
                isOnline = true
            )
        )
        _conversations.value = initialConversations

        // Initialize Chat Message threads
        _chatMessages.value = mapOf(
            "conv-1" to listOf(
                ChatMessage(
                    id = "msg-1-1",
                    senderId = "user-2",
                    text = "Hello Peter! I saw your HP EliteBook listing on Bei Poa.",
                    timestamp = System.currentTimeMillis() - 40 * 60 * 1000,
                    isFromMe = false,
                    status = MessageStatus.READ
                ),
                ChatMessage(
                    id = "msg-1-2",
                    senderId = "user-1",
                    text = "Hi Faith! Yes, it's available and running 16GB RAM smoothly.",
                    timestamp = System.currentTimeMillis() - 30 * 60 * 1000,
                    isFromMe = true,
                    status = MessageStatus.READ
                ),
                ChatMessage(
                    id = "msg-1-3",
                    senderId = "user-2",
                    text = "Hi Peter! Can we meet at UoN Main Campus Library gate at 2 PM?",
                    timestamp = System.currentTimeMillis() - 15 * 60 * 1000,
                    isFromMe = false,
                    status = MessageStatus.READ
                )
            ),
            "conv-2" to listOf(
                ChatMessage(
                    id = "msg-2-1",
                    senderId = "user-3",
                    text = "Hey! Does the Casio calculator come with the slide-on protective hard case?",
                    timestamp = System.currentTimeMillis() - 3 * 3600 * 1000,
                    isFromMe = false,
                    status = MessageStatus.READ
                ),
                ChatMessage(
                    id = "msg-2-2",
                    senderId = "user-1",
                    text = "Yes, complete original hard casing and solar sensor works 100%.",
                    timestamp = System.currentTimeMillis() - 2 * 3600 * 1000,
                    isFromMe = true,
                    status = MessageStatus.DELIVERED
                ),
                ChatMessage(
                    id = "msg-2-3",
                    senderId = "user-3",
                    text = "Is the calculator still available for KSh 2,000?",
                    timestamp = System.currentTimeMillis() - 2 * 3600 * 1000 + 5000,
                    isFromMe = false,
                    status = MessageStatus.DELIVERED
                )
            )
        )

        // Initial Notifications
        _notifications.value = listOf(
            NotificationItem(
                id = "notif-1",
                title = "Listing Verified",
                description = "Your HP EliteBook 840 G5 listing was approved by campus moderator Faith.",
                type = NotificationType.LISTING_APPROVED,
                timestamp = System.currentTimeMillis() - 45 * 60 * 1000,
                isRead = false,
                targetId = "item-1"
            ),
            NotificationItem(
                id = "notif-2",
                title = "New Inquiry",
                description = "Faith Mwangi sent you an inquiry regarding 'HP EliteBook 840 G5'.",
                type = NotificationType.NEW_MESSAGE,
                timestamp = System.currentTimeMillis() - 15 * 60 * 1000,
                isRead = false,
                targetId = "conv-1"
            ),
            NotificationItem(
                id = "notif-3",
                title = "Campus Safety Advisory",
                description = "Remember to meet inside campus student centres or gate guards during daylight hours.",
                type = NotificationType.SECURITY_ALERT,
                timestamp = System.currentTimeMillis() - 8 * 3600 * 1000,
                isRead = true
            )
        )

        // Seed favorites
        _favorites.value = setOf("item-1", "item-3")
    }

    // --- Authentication & User Management ---
    fun login(email: String, name: String = "", university: String = ""): Boolean {
        val cleanEmail = email.trim().lowercase()
        var user = _users.value.find { it.email.lowercase() == cleanEmail }
        if (user == null) {
            user = UserProfile(
                name = name.ifBlank { email.substringBefore("@").replaceFirstChar { it.uppercase() } },
                email = cleanEmail,
                university = university.ifBlank { "University of Nairobi" },
                role = "user",
                isVerified = true
            )
            _users.value = _users.value + user
        }
        if (user.status == "banned") {
            return false
        }
        _currentUser.value = user
        prefs.edit().putString("current_user_id", user.id).apply()
        return true
    }

    fun switchUser(userId: String) {
        val user = _users.value.find { it.id == userId }
        if (user != null && user.status != "banned") {
            _currentUser.value = user
            prefs.edit().putString("current_user_id", user.id).apply()
        }
    }

    fun logout() {
        _currentUser.value = null
        prefs.edit().remove("current_user_id").apply()
    }

    fun updateProfile(name: String, university: String, phone: String, bio: String, avatarUrl: String? = null, coverUrl: String? = null) {
        val current = _currentUser.value ?: return
        val updated = current.copy(
            name = name,
            university = university,
            phone = phone,
            bio = bio,
            avatarUrl = avatarUrl ?: current.avatarUrl,
            coverImageUrl = coverUrl ?: current.coverImageUrl
        )
        _currentUser.value = updated
        _users.value = _users.value.map { if (it.id == updated.id) updated else it }
    }

    // --- Theme Switching ---
    fun toggleDarkMode() {
        val next = !_darkMode.value
        _darkMode.value = next
        prefs.edit().putBoolean("dark_mode", next).apply()
    }

    // --- Favorites ---
    fun toggleFavorite(itemId: String) {
        val current = _favorites.value
        val updated = if (current.contains(itemId)) current - itemId else current + itemId
        _favorites.value = updated
        // update favorite count on item
        _items.value = _items.value.map {
            if (it.id == itemId) {
                val delta = if (updated.contains(itemId)) 1 else -1
                it.copy(favoritesCount = (it.favoritesCount + delta).coerceAtLeast(0))
            } else it
        }
    }

    // --- Saved Searches ---
    fun addSavedSearch(query: String) {
        if (query.isNotBlank() && !_savedSearches.value.contains(query)) {
            _savedSearches.value = listOf(query) + _savedSearches.value
        }
    }

    fun removeSavedSearch(query: String) {
        _savedSearches.value = _savedSearches.value - query
    }

    // --- Real-Time Chat Engine ---
    fun sendChatMessage(conversationId: String, text: String, mediaUrl: String? = null, mediaType: String? = null) {
        val user = _currentUser.value ?: return
        val newMsg = ChatMessage(
            senderId = user.id,
            text = text,
            timestamp = System.currentTimeMillis(),
            isFromMe = true,
            status = MessageStatus.DELIVERED,
            mediaUrl = mediaUrl,
            mediaType = mediaType
        )
        val currentList = _chatMessages.value[conversationId] ?: emptyList()
        val updatedThread = currentList + newMsg
        _chatMessages.value = _chatMessages.value + (conversationId to updatedThread)

        // Update conversation summary
        _conversations.value = _conversations.value.map {
            if (it.conversationId == conversationId) {
                it.copy(
                    lastMessage = if (text.isNotBlank()) text else "[Media Attached]",
                    timestamp = System.currentTimeMillis(),
                    isFromMe = true,
                    status = MessageStatus.DELIVERED
                )
            } else it
        }
    }

    fun startOrGetConversation(item: MarketItem): String {
        val user = _currentUser.value
        val existing = _conversations.value.find { it.itemId == item.id }
        if (existing != null) {
            return existing.conversationId
        }

        val convId = UUID.randomUUID().toString()
        val newConv = CampusMessage(
            id = convId,
            conversationId = convId,
            itemId = item.id,
            itemTitle = item.title,
            itemPrice = item.price,
            itemImageUrl = item.imageUrl,
            senderId = user?.id ?: "guest",
            senderName = user?.name ?: "Student",
            recipientId = item.sellerId,
            otherUserName = item.sellerName,
            otherUserPhone = item.contact,
            lastMessage = "Hi ${item.sellerName}, I'm interested in ${item.title}!",
            timestamp = System.currentTimeMillis(),
            isFromMe = true,
            status = MessageStatus.DELIVERED,
            isOnline = true
        )
        _conversations.value = listOf(newConv) + _conversations.value
        _chatMessages.value = _chatMessages.value + (convId to listOf(
            ChatMessage(
                senderId = user?.id ?: "guest",
                text = "Hi ${item.sellerName}, I saw your listing on Bei Poa! Is it still available for inspection?",
                timestamp = System.currentTimeMillis(),
                isFromMe = true,
                status = MessageStatus.DELIVERED
            )
        ))
        return convId
    }

    // --- Notifications ---
    fun markNotificationRead(notifId: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == notifId) it.copy(isRead = true) else it
        }
    }

    fun markAllNotificationsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    // --- Items / Marketplace CRUD ---
    fun recordItemView(itemId: String) {
        _items.value = _items.value.map {
            if (it.id == itemId) it.copy(views = it.views + 1) else it
        }
    }

    fun postItem(
        title: String,
        description: String,
        price: Double,
        category: String,
        condition: String,
        university: String,
        contact: String,
        imageUrl: String?,
        additionalImages: List<String> = emptyList()
    ): MarketItem {
        val user = _currentUser.value
        val isStaff = user?.role == "admin" || user?.role == "moderator"
        val newItem = MarketItem(
            title = title,
            description = description,
            price = price,
            category = category,
            condition = condition,
            university = university,
            imageUrl = imageUrl,
            additionalImages = additionalImages,
            contact = contact,
            sellerId = user?.id ?: "guest",
            sellerName = user?.name ?: "Campus Student",
            sellerEmail = user?.email ?: "student@campus.ac.ke",
            sellerVerified = user?.isVerified ?: true,
            status = if (isStaff) "approved" else "pending",
            sold = false,
            views = 1,
            favoritesCount = 0
        )
        _items.value = listOf(newItem) + _items.value

        // Notify
        _notifications.value = listOf(
            NotificationItem(
                title = if (isStaff) "Listing Live" else "Listing Submitted",
                description = if (isStaff) "Your listing '${title}' is now visible to all students." else "Your listing '${title}' is in the campus moderation queue.",
                type = NotificationType.SYSTEM,
                targetId = newItem.id
            )
        ) + _notifications.value

        return newItem
    }

    fun markItemSold(itemId: String) {
        _items.value = _items.value.map {
            if (it.id == itemId) it.copy(sold = true) else it
        }
    }

    fun deleteItem(itemId: String) {
        _items.value = _items.value.filterNot { it.id == itemId }
    }

    // --- Moderation & Admin ---
    fun moderateItem(itemId: String, newStatus: String) {
        _items.value = _items.value.map {
            if (it.id == itemId) it.copy(status = newStatus) else it
        }
    }

    fun approveAllPending() {
        _items.value = _items.value.map {
            if (it.status == "pending") it.copy(status = "approved") else it
        }
    }

    fun submitReport(itemId: String, reason: String, details: String) {
        val item = _items.value.find { it.id == itemId } ?: return
        val user = _currentUser.value
        val report = MarketReport(
            itemId = itemId,
            itemTitle = item.title,
            sellerId = item.sellerId,
            sellerName = item.sellerName,
            sellerEmail = item.sellerEmail,
            reason = reason,
            details = details,
            reporterId = user?.id ?: "anon",
            reporterName = user?.name ?: "Anonymous",
            status = "open"
        )
        _reports.value = listOf(report) + _reports.value
    }

    fun resolveReport(reportId: String, newStatus: String) {
        _reports.value = _reports.value.map {
            if (it.id == reportId) it.copy(status = newStatus) else it
        }
    }

    fun banUser(userId: String) {
        _users.value = _users.value.map {
            if (it.id == userId) it.copy(status = "banned") else it
        }
        _items.value = _items.value.map {
            if (it.sellerId == userId && it.status == "pending") it.copy(status = "rejected") else it
        }
        if (_currentUser.value?.id == userId) {
            logout()
        }
    }

    fun unbanUser(userId: String) {
        _users.value = _users.value.map {
            if (it.id == userId) it.copy(status = "active") else it
        }
    }

    fun setUserRole(userId: String, newRole: String) {
        _users.value = _users.value.map {
            if (it.id == userId) it.copy(role = newRole) else it
        }
        if (_currentUser.value?.id == userId) {
            _currentUser.value = _currentUser.value?.copy(role = newRole)
        }
    }

    fun getUniversities(): List<String> {
        return (_items.value.map { it.university } +
                listOf("University of Nairobi", "Kenyatta University", "JKUAT", "Strathmore University", "Moi University", "Egerton University", "USIU Africa"))
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
    }
}
