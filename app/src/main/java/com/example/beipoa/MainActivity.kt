package com.example.beipoa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.beipoa.data.repository.MarketplaceRepository
import com.example.beipoa.ui.components.AuthDialog
import com.example.beipoa.ui.components.ReportDialog
import com.example.beipoa.ui.navigation.Screen
import com.example.beipoa.ui.screens.*
import com.example.beipoa.ui.theme.*
import com.example.beipoa.ui.viewmodel.MarketplaceViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = MarketplaceRepository(applicationContext)

        setContent {
            val viewModel: MarketplaceViewModel = remember {
                MarketplaceViewModel(repository)
            }
            val isDarkMode by viewModel.isDarkMode.collectAsState()

            BeiPoaTheme(darkTheme = isDarkMode) {
                BeiPoaApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeiPoaApp(viewModel: MarketplaceViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val currentUser by viewModel.currentUser.collectAsState()
    val isAuthOpen by viewModel.isAuthDialogOpen.collectAsState()
    val authMode by viewModel.authMode.collectAsState()
    val reportingItem by viewModel.reportingItem.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val unreadNotifsCount by viewModel.unreadNotificationsCount.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val isStaff = currentUser?.role == "admin" || currentUser?.role == "moderator"

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    val currentRoute = currentDestination?.route ?: Screen.Home.route

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = OrangePrimary,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "BP",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Bei Poa",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    // Notification Bell with Badge
                    IconButton(
                        onClick = {
                            navController.navigate(Screen.Notifications.route) {
                                launchSingleTop = true
                            }
                        }
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotifsCount > 0) {
                                    Badge(
                                        containerColor = OrangePrimary,
                                        contentColor = Color.White
                                    ) {
                                        Text("$unreadNotifsCount")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    if (currentUser == null) {
                        TextButton(
                            onClick = { viewModel.openAuth("login") },
                            modifier = Modifier.testTag("app_login_button")
                        ) {
                            Text("Sign In", color = OrangePrimary, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        IconButton(
                            onClick = {
                                navController.navigate(Screen.Profile.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                }
                            },
                            modifier = Modifier.testTag("app_profile_avatar_button")
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = OrangePrimary,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = currentUser?.name?.take(1)?.uppercase() ?: "U",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 10.sp) },
                    selected = currentRoute == Screen.Home.route,
                    onClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OrangePrimary,
                        selectedTextColor = OrangePrimary,
                        indicatorColor = OrangeLight
                    ),
                    modifier = Modifier.testTag("nav_home")
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Storefront, contentDescription = "Market") },
                    label = { Text("Market", fontSize = 10.sp) },
                    selected = currentRoute == Screen.Marketplace.route,
                    onClick = {
                        navController.navigate(Screen.Marketplace.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OrangePrimary,
                        selectedTextColor = OrangePrimary,
                        indicatorColor = OrangeLight
                    ),
                    modifier = Modifier.testTag("nav_marketplace")
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.AddCircleOutline, contentDescription = "Sell") },
                    label = { Text("Sell", fontSize = 10.sp) },
                    selected = currentRoute == Screen.Sell.route,
                    onClick = {
                        if (currentUser != null) {
                            navController.navigate(Screen.Sell.route) {
                                launchSingleTop = true
                            }
                        } else {
                            viewModel.openAuth("login")
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OrangePrimary,
                        selectedTextColor = OrangePrimary,
                        indicatorColor = OrangeLight
                    ),
                    modifier = Modifier.testTag("nav_sell")
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Dashboard", fontSize = 10.sp) },
                    selected = currentRoute == Screen.Dashboard.route,
                    onClick = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OrangePrimary,
                        selectedTextColor = OrangePrimary,
                        indicatorColor = OrangeLight
                    ),
                    modifier = Modifier.testTag("nav_dashboard")
                )

                NavigationBarItem(
                    icon = { Icon(Icons.Default.Chat, contentDescription = "Messages") },
                    label = { Text("Messages", fontSize = 10.sp) },
                    selected = currentRoute == Screen.Messages.route,
                    onClick = {
                        navController.navigate(Screen.Messages.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OrangePrimary,
                        selectedTextColor = OrangePrimary,
                        indicatorColor = OrangeLight
                    ),
                    modifier = Modifier.testTag("nav_messages")
                )

                if (isStaff) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin") },
                        label = { Text("Admin", fontSize = 10.sp) },
                        selected = currentRoute == Screen.Admin.route,
                        onClick = {
                            navController.navigate(Screen.Admin.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = OrangePrimary,
                            selectedTextColor = OrangePrimary,
                            indicatorColor = OrangeLight
                        ),
                        modifier = Modifier.testTag("nav_admin")
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToMarketplace = { navController.navigate(Screen.Marketplace.route) },
                    onNavigateToSell = { navController.navigate(Screen.Sell.route) },
                    onNavigateToProduct = { itemId -> navController.navigate(Screen.ProductDetail.createRoute(itemId)) },
                    onNavigateToLegal = { type -> navController.navigate(Screen.Legal.createRoute(type)) }
                )
            }

            composable(Screen.Marketplace.route) {
                MarketplaceScreen(
                    viewModel = viewModel,
                    onNavigateToProduct = { itemId -> navController.navigate(Screen.ProductDetail.createRoute(itemId)) },
                    onNavigateToSell = { navController.navigate(Screen.Sell.route) }
                )
            }

            composable(
                route = Screen.ProductDetail.route,
                arguments = listOf(navArgument("itemId") { type = NavType.StringType })
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
                ProductDetailScreen(
                    itemId = itemId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToCheckout = { id -> navController.navigate(Screen.Checkout.createRoute(id)) },
                    onNavigateToMessages = { navController.navigate(Screen.Messages.route) }
                )
            }

            composable(
                route = Screen.Checkout.route,
                arguments = listOf(navArgument("itemId") { type = NavType.StringType })
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
                CheckoutScreen(
                    itemId = itemId,
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Sell.route) {
                SellItemScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Home.route)
                        }
                    }
                )
            }

            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToSell = { navController.navigate(Screen.Sell.route) },
                    onNavigateToProduct = { itemId -> navController.navigate(Screen.ProductDetail.createRoute(itemId)) }
                )
            }

            composable(Screen.Messages.route) {
                MessagesScreen(
                    viewModel = viewModel,
                    onNavigateToProduct = { itemId -> navController.navigate(Screen.ProductDetail.createRoute(itemId)) }
                )
            }

            composable(Screen.Notifications.route) {
                NotificationsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToProduct = { itemId -> navController.navigate(Screen.ProductDetail.createRoute(itemId)) },
                    onNavigateToMessages = { navController.navigate(Screen.Messages.route) }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToLegal = { type -> navController.navigate(Screen.Legal.createRoute(type)) }
                )
            }

            composable(Screen.Admin.route) {
                AdminScreen(viewModel = viewModel)
            }

            composable(
                route = Screen.Legal.route,
                arguments = listOf(navArgument("type") { type = NavType.StringType })
            ) { backStackEntry ->
                val type = backStackEntry.arguments?.getString("type") ?: "terms"
                LegalScreen(
                    type = type,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }

    // Modals
    AuthDialog(
        isOpen = isAuthOpen,
        initialMode = authMode,
        onDismiss = { viewModel.closeAuth() },
        onLogin = { email, name, university -> viewModel.login(email, name, university) },
        onQuickSwitch = { userId -> viewModel.switchDemoUser(userId) }
    )

    ReportDialog(
        item = reportingItem,
        onDismiss = { viewModel.closeReportDialog() },
        onSubmit = { reason, details -> viewModel.submitReport(reason, details) }
    )
}
