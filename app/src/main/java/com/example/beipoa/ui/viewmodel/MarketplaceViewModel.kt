package com.example.beipoa.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beipoa.data.model.*
import com.example.beipoa.data.repository.MarketplaceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class SortOption(val label: String) {
    NEWEST("Newest First"),
    PRICE_LOW_TO_HIGH("Price: Low to High"),
    PRICE_HIGH_TO_LOW("Price: High to Low"),
    MOST_POPULAR("Most Popular")
}

class MarketplaceViewModel(private val repository: MarketplaceRepository) : ViewModel() {

    val currentUser: StateFlow<UserProfile?> = repository.currentUser
    val allItems: StateFlow<List<MarketItem>> = repository.items
    val allUsers: StateFlow<List<UserProfile>> = repository.users
    val allReports: StateFlow<List<MarketReport>> = repository.reports
    val conversations: StateFlow<List<CampusMessage>> = repository.conversations
    val chatMessages: StateFlow<Map<String, List<ChatMessage>>> = repository.chatMessages
    val notifications: StateFlow<List<NotificationItem>> = repository.notifications
    val favorites: StateFlow<Set<String>> = repository.favorites
    val savedSearches: StateFlow<List<String>> = repository.savedSearches
    val isDarkMode: StateFlow<Boolean> = repository.darkMode

    // Filter states
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow<String?>(null)
    val selectedUniversity = MutableStateFlow<String?>(null)
    val maxPriceFilter = MutableStateFlow<Double?>(null)
    val sortBy = MutableStateFlow(SortOption.NEWEST)
    val showFavoritesOnly = MutableStateFlow(false)

    // Quick view item modal state
    val quickViewItem = MutableStateFlow<MarketItem?>(null)

    // Product comparison items (up to 2 items)
    val comparisonItems = MutableStateFlow<List<MarketItem>>(emptyList())
    val isCompareModalOpen = MutableStateFlow(false)

    // SnackBar / Toast events
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Auth modal state
    val isAuthDialogOpen = MutableStateFlow(false)
    val authMode = MutableStateFlow("login") // "login" or "register"

    // Report modal state
    val reportingItem = MutableStateFlow<MarketItem?>(null)

    // Active Chat Conversation
    val activeConversationId = MutableStateFlow<String?>(null)

    // Filtered & Sorted marketplace items
    val filteredMarketItems: StateFlow<List<MarketItem>> = combine(
        allItems,
        searchQuery,
        selectedCategory,
        selectedUniversity,
        maxPriceFilter
    ) { items, query, category, university, maxPrice ->
        items.filter { item ->
            val matchesStatus = item.status == "approved" && !item.sold
            val matchesQuery = query.isBlank() ||
                    item.title.contains(query, ignoreCase = true) ||
                    item.description.contains(query, ignoreCase = true) ||
                    item.category.contains(query, ignoreCase = true)
            val matchesCategory = category.isNullOrBlank() || item.category.equals(category, ignoreCase = true)
            val matchesUniversity = university.isNullOrBlank() || item.university.equals(university, ignoreCase = true)
            val matchesPrice = maxPrice == null || item.price <= maxPrice

            matchesStatus && matchesQuery && matchesCategory && matchesUniversity && matchesPrice
        }
    }.combine(sortBy) { items, sort ->
        when (sort) {
            SortOption.NEWEST -> items.sortedByDescending { it.createdAt }
            SortOption.PRICE_LOW_TO_HIGH -> items.sortedBy { it.price }
            SortOption.PRICE_HIGH_TO_LOW -> items.sortedByDescending { it.price }
            SortOption.MOST_POPULAR -> items.sortedByDescending { it.views }
        }
    }.combine(showFavoritesOnly) { items, favOnly ->
        if (favOnly) items.filter { favorites.value.contains(it.id) } else items
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Featured items for Home screen (top 5 approved items)
    val featuredItems: StateFlow<List<MarketItem>> = allItems.map { items ->
        items.filter { it.status == "approved" && !it.sold }.take(5)
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // User's own listings for Dashboard
    val myListings: StateFlow<List<MarketItem>> = combine(allItems, currentUser) { items, user ->
        if (user == null) emptyList()
        else items.filter { it.sellerId == user.id }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val unreadNotificationsCount: StateFlow<Int> = notifications.map { list ->
        list.count { !it.isRead }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    fun toggleDarkMode() {
        repository.toggleDarkMode()
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    fun resetFilters() {
        searchQuery.value = ""
        selectedCategory.value = null
        selectedUniversity.value = null
        maxPriceFilter.value = null
        sortBy.value = SortOption.NEWEST
        showFavoritesOnly.value = false
    }

    fun setCategoryFilter(category: String?) {
        selectedCategory.value = if (selectedCategory.value == category) null else category
    }

    fun toggleFavorite(itemId: String) {
        repository.toggleFavorite(itemId)
    }

    fun isFavorite(itemId: String): Boolean {
        return favorites.value.contains(itemId)
    }

    fun saveCurrentSearch() {
        val q = searchQuery.value.trim()
        if (q.isNotBlank()) {
            repository.addSavedSearch(q)
            showMessage("Saved search '$q'")
        }
    }

    fun removeSavedSearch(query: String) {
        repository.removeSavedSearch(query)
    }

    fun applySavedSearch(query: String) {
        searchQuery.value = query
    }

    // Comparison system
    fun toggleComparison(item: MarketItem) {
        val current = comparisonItems.value
        if (current.any { it.id == item.id }) {
            comparisonItems.value = current.filterNot { it.id == item.id }
            showMessage("Removed from comparison")
        } else {
            if (current.size >= 2) {
                comparisonItems.value = listOf(current[1], item)
            } else {
                comparisonItems.value = current + item
            }
            showMessage("Added to compare (${comparisonItems.value.size}/2)")
        }
    }

    fun openCompareModal() {
        isCompareModalOpen.value = true
    }

    fun closeCompareModal() {
        isCompareModalOpen.value = false
    }

    fun getUniversities(): List<String> = repository.getUniversities()

    // Auth actions
    fun openAuth(mode: String = "login") {
        authMode.value = mode
        isAuthDialogOpen.value = true
    }

    fun closeAuth() {
        isAuthDialogOpen.value = false
    }

    fun login(email: String, name: String = "", university: String = "") {
        val success = repository.login(email, name, university)
        if (success) {
            isAuthDialogOpen.value = false
            showMessage("Welcome to Bei Poa!")
        } else {
            showMessage("Account banned or invalid credentials.")
        }
    }

    fun switchDemoUser(userId: String) {
        repository.switchUser(userId)
        val name = repository.currentUser.value?.name ?: "User"
        val role = repository.currentUser.value?.role ?: "user"
        showMessage("Switched to $name ($role)")
    }

    fun logout() {
        repository.logout()
        showMessage("Logged out")
    }

    fun updateProfile(name: String, university: String, phone: String, bio: String, avatarUrl: String? = null, coverUrl: String? = null) {
        repository.updateProfile(name, university, phone, bio, avatarUrl, coverUrl)
        showMessage("Profile updated successfully")
    }

    // Chat operations
    fun openConversation(conversationId: String) {
        activeConversationId.value = conversationId
    }

    fun startChatWithSeller(item: MarketItem): String {
        val convId = repository.startOrGetConversation(item)
        activeConversationId.value = convId
        return convId
    }

    fun sendChatMessage(conversationId: String, text: String, mediaUrl: String? = null, mediaType: String? = null) {
        repository.sendChatMessage(conversationId, text, mediaUrl, mediaType)
    }

    // Notifications
    fun markNotificationRead(id: String) {
        repository.markNotificationRead(id)
    }

    fun markAllNotificationsRead() {
        repository.markAllNotificationsRead()
    }

    // Item CRUD
    fun recordView(itemId: String) {
        repository.recordItemView(itemId)
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
    ) {
        repository.postItem(title, description, price, category, condition, university, contact, imageUrl, additionalImages)
        val user = currentUser.value
        if (user?.role == "admin" || user?.role == "moderator") {
            showMessage("Listing approved and published!")
        } else {
            showMessage("Listing submitted! Pending moderator review.")
        }
    }

    fun markItemSold(itemId: String) {
        repository.markItemSold(itemId)
        showMessage("Item marked as Sold")
    }

    fun deleteItem(itemId: String) {
        repository.deleteItem(itemId)
        showMessage("Item deleted")
    }

    // Moderation & Admin
    fun approveItem(itemId: String) {
        repository.moderateItem(itemId, "approved")
        showMessage("Listing approved")
    }

    fun rejectItem(itemId: String) {
        repository.moderateItem(itemId, "rejected")
        showMessage("Listing rejected")
    }

    fun approveAllPending() {
        repository.approveAllPending()
        showMessage("All pending items approved")
    }

    fun openReportDialog(item: MarketItem) {
        reportingItem.value = item
    }

    fun closeReportDialog() {
        reportingItem.value = null
    }

    fun submitReport(reason: String, details: String) {
        val item = reportingItem.value ?: return
        repository.submitReport(item.id, reason, details)
        reportingItem.value = null
        showMessage("Report submitted. Campus moderators will inspect.")
    }

    fun resolveReport(reportId: String, status: String) {
        repository.resolveReport(reportId, status)
        showMessage("Report marked as $status")
    }

    fun banUser(userId: String) {
        repository.banUser(userId)
        showMessage("User banned and listings removed")
    }

    fun unbanUser(userId: String) {
        repository.unbanUser(userId)
        showMessage("User unbanned")
    }

    fun setUserRole(userId: String, role: String) {
        repository.setUserRole(userId, role)
        showMessage("User role updated to $role")
    }
}
