package com.eternaljust.msea

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.EnergySavingsLeaf
import androidx.compose.material.icons.outlined.FormatListNumbered
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
import com.eternaljust.msea.ui.page.home.sign.SignPage
import com.eternaljust.msea.ui.page.node.NodePage
import com.eternaljust.msea.ui.page.notice.NoticePage
import com.eternaljust.msea.ui.page.profile.*
import com.eternaljust.msea.ui.page.profile.drawer.DrawerPage
import com.eternaljust.msea.ui.theme.MseaComposeTheme
import com.eternaljust.msea.ui.widget.mseaSmallTopAppBarColors
import com.eternaljust.msea.utils.DataStoreUtil
import com.eternaljust.msea.utils.RouteName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
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
        icon = Icons.Default.GridView
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Mseacompose)
        super.onCreate(savedInstanceState)
        setContent {
            MseaComposeTheme(isDynamicColor = true) {
                MyApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerPage(
                navController = navController,
                drawerState = drawerState,
                onClick = { item ->
                    scope.launch { drawerState.close() }
                    if (item.route == RouteName.LOGOUT) {
                        GlobalScope.launch { DataStoreUtil.clear() }
                        scope.launch {
                            snackbarHostState.showSnackbar(message = "已退出登录")
                        }
                    } else {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id)
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
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
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    Text(
                                        text = "Make search easier / 让搜索更简单",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            },
                            navigationIcon = {
                                IconButton(onClick = {
                                    scope.launch { drawerState.open() }
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Menu,
                                        contentDescription = "我的"
                                    )
                                }
                            },
                            actions = {
                                IconButton(
                                    onClick = { navController.navigate(route = RouteName.Sign) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.EnergySavingsLeaf,
                                        contentDescription = "签到"
                                    )
                                }

                                IconButton(onClick = { /* doSomething() */ }) {
                                    Icon(
                                        imageVector = Icons.Outlined.FormatListNumbered,
                                        contentDescription = "排行榜"
                                    )
                                }

                                IconButton(onClick = { /* doSomething() */ }) {
                                    Icon(
                                        imageVector = Icons.Filled.Search,
                                        contentDescription = "搜索"
                                    )
                                }
                            },
                            colors = mseaSmallTopAppBarColors()
                        )
                    }
                },
                bottomBar = {
                    if (mainScreens.contains(currentDestination?.route)) {
                        NavigationBar(
                            containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White
                        )
                        {
                            screens.forEach { screen ->
                                NavigationBarItem(
                                    icon = { Icon(screen.icon, contentDescription = null) },
                                    label = { Text(screen.title) },
                                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id)
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary
                                    )
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
                    NavHost(
                        navController,
                        startDestination = RouteName.HOME,
                        Modifier.padding(paddingValues)
                    ) {
                        composable(RouteName.HOME) {
                            HomePage(
                                scaffoldState = snackbarHostState,
                                navController = navController
                            )
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

private fun NavGraphBuilder.detailsNav(
    scaffoldState: SnackbarHostState,
    navController: NavHostController
) {
    composable(RouteName.TOPIC_DETAIL) {
        ProfileTopicPage(
            scaffoldState = scaffoldState,
            navController = navController
        )
    }

    composable(RouteName.PROFILE_TOPIC) {
        ProfileTopicPage(
            scaffoldState = scaffoldState,
            navController = navController
        )
    }

    composable(RouteName.PROFILE_FRIEND) {
        ProfileFriendPage(
            scaffoldState = scaffoldState,
            navController = navController
        )
    }

    composable(RouteName.PROFILE_FAVORITE) {
        ProfileFavoritePage(
            scaffoldState = scaffoldState,
            navController = navController
        )
    }

    composable(RouteName.SETTING) {
        SettingPage(
            scaffoldState = scaffoldState,
            navController = navController
        )
    }

    composable(RouteName.ABOUT) {
        AboutPage(
            scaffoldState = scaffoldState,
            navController = navController
        )
    }

    composable(RouteName.LOGIN) {
        LoginPage(
            scaffoldState = scaffoldState,
            navController = navController,
            loginMessage = {
                GlobalScope.launch(Dispatchers.Main) {
                    scaffoldState.showSnackbar(message = it)
                }
            })
    }

    composable(RouteName.Sign) {
        SignPage(
            scaffoldState = scaffoldState,
            navController = navController
        )
    }
}
