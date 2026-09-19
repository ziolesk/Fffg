package com.example.beipoa.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Home")
    object Marketplace : Screen("marketplace", "Marketplace")
    object Sell : Screen("sell", "Sell Item")
    object Dashboard : Screen("dashboard", "Dashboard")
    object Messages : Screen("messages", "Messages")
    object Profile : Screen("profile", "Profile")
    object Admin : Screen("admin", "Admin")
    object Notifications : Screen("notifications", "Notifications")
    object ProductDetail : Screen("product_detail/{itemId}", "Product Detail") {
        fun createRoute(itemId: String) = "product_detail/$itemId"
    }
    object Checkout : Screen("checkout/{itemId}", "Deal Checkout") {
        fun createRoute(itemId: String) = "checkout/$itemId"
    }
    object Legal : Screen("legal/{type}", "Legal") {
        fun createRoute(type: String) = "legal/$type"
    }
}
