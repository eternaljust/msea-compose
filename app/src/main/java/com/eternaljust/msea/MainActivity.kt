package com.eternaljust.msea

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.eternaljust.msea.ui.page.home.HomePage
import com.eternaljust.msea.ui.page.home.sign.SignPage
import com.eternaljust.msea.ui.page.node.NodePage
import com.eternaljust.msea.ui.page.node.list.NodeListPage
import com.eternaljust.msea.ui.page.node.tag.TagItemModel
import com.eternaljust.msea.ui.page.node.tag.TagListPage
import com.eternaljust.msea.ui.page.node.tag.TagPage
import com.eternaljust.msea.ui.page.notice.NoticePage
import com.eternaljust.msea.ui.page.profile.*
import com.eternaljust.msea.ui.page.profile.drawer.DrawerPage
import com.eternaljust.msea.ui.page.profile.login.LoginPage
import com.eternaljust.msea.ui.theme.ColorTheme
import com.eternaljust.msea.ui.theme.MseaComposeTheme
import com.eternaljust.msea.ui.widget.mseaTopAppBarColors
import com.eternaljust.msea.utils.DataStoreUtil
import com.eternaljust.msea.utils.RouteName
import com.eternaljust.msea.utils.fromJson
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
            MseaComposeTheme(isDynamicColor = false) {
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
                        TopAppBar(
                            title = {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Msea / 虫部落",
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    currentDestination?.route?.let {
                                        if (it != BottomBarScreen.Home.route) {
                                            Text(
                                                text = "Make search easier / 让搜索更简单",
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    }
                                }
                            },
                            navigationIcon = {
                                IconButton(onClick = {
                                    scope.launch { drawerState.open() }
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Menu,
                                        contentDescription = "我的个人中心"
                                    )
                                }
                            },
                            actions = {
                                currentDestination?.route?.let {
                                    TopAppBarAcitons(
                                        route = it,
                                        navController = navController
                                    )
                                }
                            },
                            colors = mseaTopAppBarColors()
                        )
                    }
                },
                bottomBar = {
                    if (mainScreens.contains(currentDestination?.route)) {
                        NavigationBar(
                            containerColor = ColorTheme(light = Color.White, dark = Color.Black)
                        ) {
                            screens.forEach { screen ->
                                NavigationBarItem(
                                    icon = { Icon(screen.icon, contentDescription = null) },
                                    label = { Text(screen.title) },
                                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                    onClick = {
                                        if (currentDestination?.route != screen.route) {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
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
                            NoticePage(
                                scaffoldState = snackbarHostState,
                                navController = navController
                            )
                        }

                        composable(RouteName.NODE) {
                            NodePage(
                                scaffoldState = snackbarHostState,
                                navController = navController
                            )
                        }

                        detailsNav(
                            scaffoldState = snackbarHostState,
                            navController = navController
                        )
                    }
                }
            )
        }
    )
}

@Composable
fun TopAppBarAcitons(
    route: String,
    navController: NavHostController
) {
    when (route) {
        BottomBarScreen.Home.route -> {
            IconButton(
                onClick = { navController.navigate(route = RouteName.SIGN) }
            ) {
                Icon(
                    imageVector = Icons.Default.EnergySavingsLeaf,
                    contentDescription = "签到"
                )
            }

            IconButton(onClick = { /* doSomething() */ }) {
                Icon(
                    imageVector = Icons.Default.FormatListNumbered,
                    contentDescription = "排行榜"
                )
            }

            IconButton(onClick = { /* doSomething() */ }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "搜索"
                )
            }
        }
        BottomBarScreen.Node.route -> {
            IconButton(onClick = { navController.navigate(route = RouteName.TAG) }) {
                Icon(
                    imageVector = Icons.Default.Tag,
                    contentDescription = "标签"
                )
            }
        }
        else -> {}
    }
}

private fun NavGraphBuilder.detailsNav(
    scaffoldState: SnackbarHostState,
    navController: NavHostController
) {
//    composable(RouteName.TOPIC_DETAIL) {
//        ProfileTopicPage(
//            scaffoldState = scaffoldState,
//            navController = navController
//        )
//    }

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

    composable(RouteName.PROFILE_CREDIT) {
        ProfileCreditPage(
            scaffoldState = scaffoldState,
            navController = navController
        )
    }

    composable(RouteName.PROFILE_GROUP) {
        ProfileGroupPage(
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

    composable(RouteName.SIGN) {
        SignPage(
            scaffoldState = scaffoldState,
            navController = navController
        )
    }

    composable(RouteName.TAG) {
        TagPage(
            scaffoldState = scaffoldState,
            navController = navController
        )
    }

    composable(
        RouteName.TAG_LIST + "/{tagItem}",
        arguments = listOf(navArgument("tagItem") { type = NavType.StringType})
    ) {
        val tagItem = it.arguments?.getString("tagItem")?.fromJson<TagItemModel>()
        tagItem?.let {
            TagListPage(
                scaffoldState = scaffoldState,
                navController = navController,
                tagItem = it
            )
        }
    }

    composable(
        RouteName.NODE_LIST + "/{fid}",
        arguments = listOf(navArgument("fid") { type = NavType.StringType})
    ) {
        val fid = it.arguments?.getString("fid")
        fid?.let {
            NodeListPage(
                scaffoldState = scaffoldState,
                navController = navController,
                fid = it
            )
        }
    }
}
