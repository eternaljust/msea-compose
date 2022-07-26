package com.eternaljust.msea

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.eternaljust.msea.ui.page.home.HomePage
import com.eternaljust.msea.ui.page.home.TopicDetailPage
import com.eternaljust.msea.ui.page.node.NodePage
import com.eternaljust.msea.ui.page.notice.NoticePage
import com.eternaljust.msea.ui.theme.MseaComposeTheme
import com.eternaljust.msea.utils.RouteName
import kotlinx.coroutines.launch

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomBarScreen(
        route = RouteName.HOME,
        title = "虫部落",
        icon = Icons.Default.Home
    )

    object Notice : BottomBarScreen(
        route = RouteName.NOTICE,
        title = "通知",
        icon = Icons.Default.Notifications
    )

    object Node : BottomBarScreen(
        route = RouteName.NODE,
        title = "节点",
        icon = Icons.Default.List
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MseaComposeTheme {
                MyApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp() {
    MaterialTheme {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val items = listOf(Icons.Default.Home, Icons.Default.Notifications, Icons.Default.List)
        val selectedDrawerItem = remember { mutableStateOf(items[0]) }
        val snackbarHostState = remember { SnackbarHostState() }
        val navController = rememberNavController()

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                Column(modifier = Modifier.padding(vertical = 16.dp)) {
                    Text(text = "Msea")

                    items.forEach { item ->
                        NavigationDrawerItem(
                            icon = { Icon(item, contentDescription = null) },
                            label = { Text(item.name) },
                            selected = item == selectedDrawerItem.value,
                            onClick = {
                                scope.launch { drawerState.close() }
                                selectedDrawerItem.value = item
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }

            },
            content = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                val screens = listOf(
                    BottomBarScreen.Home,
                    BottomBarScreen.Notice,
                    BottomBarScreen.Node,
                )
                val mainScreens = screens.map { it.route }

                Scaffold(
                    topBar = {
                        if (mainScreens.contains(currentDestination?.route)) {
                            SmallTopAppBar(
                                title = {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = "Msea / 虫部落",
                                            style = MaterialTheme.typography.titleLarge
                                        )

                                        Text(
                                            text = "Make search easier / 让搜索更简单",
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                    }
                                },
                                navigationIcon = {
                                    IconButton(onClick = {
                                        scope.launch { drawerState.open() }
                                    }) {
                                        Icon(
                                            imageVector = Icons.Filled.Menu,
                                            contentDescription = null
                                        )
                                    }
                                },
                                actions = {
                                    IconButton(onClick = { /* doSomething() */ }) {
                                        Icon(
                                            imageVector = Icons.Filled.Search,
                                            contentDescription = null
                                        )
                                    }
                                },
                                colors = TopAppBarDefaults.smallTopAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    titleContentColor = Color.White,
                                    actionIconContentColor = Color.White,
                                    navigationIconContentColor = Color.White
                                )
                            )
                        }
                    },
                    bottomBar = {
                        if (mainScreens.contains(currentDestination?.route)) {
                            NavigationBar(
                                containerColor = if(isSystemInDarkTheme()) Color.Black else Color.White
                            )
                            {
                                screens.forEach { screen ->
                                    NavigationBarItem(
                                        icon = { Icon(screen.icon, contentDescription = null) },
                                        label = { Text(screen.title) },
                                        selected = currentDestination?.hierarchy?.any { it.route == screen.route  } == true,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id)
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    },
                    snackbarHost = {
                        SnackbarHost(snackbarHostState) { data ->
                            Snackbar(
                                modifier = Modifier.padding(16.dp),
                                containerColor = MaterialTheme.colorScheme.primary
                            ) {
                                Text(
                                    text = data.visuals.message,
                                )
                            }
                        }
                    },
                    content = { paddingValues ->
                        NavHost(navController,
                                startDestination = RouteName.HOME,
                                Modifier.padding(paddingValues)) {
                            composable(RouteName.HOME) {
                                HomePage(scaffoldState = snackbarHostState, navController = navController)
                            }

                            composable(RouteName.NOTICE) {
                                NoticePage(scaffoldState = snackbarHostState)
                            }

                            composable(RouteName.NODE) {
                                NodePage(scaffoldState = snackbarHostState)
                            }

                            detailsNav(scaffoldState = snackbarHostState, navController = navController)
                        }
                    }
                )
            }
        )
    }
}

private fun NavGraphBuilder.detailsNav(
    scaffoldState: SnackbarHostState,
    navController: NavHostController
) {
    composable(RouteName.TOPIC_DETAIL) {
        TopicDetailPage(scaffoldState = scaffoldState, navController = navController)
    }
}
