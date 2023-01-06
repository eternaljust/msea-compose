package com.eternaljust.msea

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.eternaljust.msea.ui.page.home.HomePage
import com.eternaljust.msea.ui.page.home.topic.TopicDetailPage
import com.eternaljust.msea.ui.page.home.sign.SignPage
import com.eternaljust.msea.ui.page.home.topic.TopicDetailRouteModel
import com.eternaljust.msea.ui.page.node.NodeDetailPage
import com.eternaljust.msea.ui.page.node.NodePage
import com.eternaljust.msea.ui.page.node.list.NodeListPage
import com.eternaljust.msea.ui.page.node.tag.TagItemModel
import com.eternaljust.msea.ui.page.node.tag.TagListPage
import com.eternaljust.msea.ui.page.node.tag.TagPage
import com.eternaljust.msea.ui.page.notice.NoticePage
import com.eternaljust.msea.ui.page.profile.detail.*
import com.eternaljust.msea.ui.page.profile.drawer.DrawerPage
import com.eternaljust.msea.ui.page.profile.login.LoginPage
import com.eternaljust.msea.ui.page.profile.setting.*
import com.eternaljust.msea.ui.theme.colorTheme
import com.eternaljust.msea.ui.theme.MseaComposeTheme
import com.eternaljust.msea.ui.theme.themeStyleDark
import com.eternaljust.msea.ui.widget.WebURL
import com.eternaljust.msea.ui.widget.WebViewModel
import com.eternaljust.msea.ui.widget.WebViewPage
import com.eternaljust.msea.ui.widget.mseaTopAppBarColors
import com.eternaljust.msea.utils.*
import com.umeng.commonsdk.UMConfigure
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val imageVector: ImageVector?,
    val paint: Int?
) {
    object Home : BottomBarScreen(
        route = RouteName.HOME,
        title = "虫部落",
        imageVector = Icons.Default.Home,
        paint = null
    )

    object Notice : BottomBarScreen(
        route = RouteName.NOTICE,
        title = "通知",
        imageVector = Icons.Default.Notifications,
        paint = null
    )

    object Node : BottomBarScreen(
        route = RouteName.NODE,
        title = "节点",
        imageVector = null,
        paint = R.drawable.ic_baseline_grid_view_24
    )
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        println("onCreate-----")

        setTheme(R.style.Theme_Mseacompose)
        setContent {
            MseaComposeTheme(
                darkTheme = themeStyleDark(),
                isDynamicColor = SettingInfo.instance.colorScheme
            ) {
                var agreePrivacyPolicy by remember {
                    mutableStateOf(SettingInfo.instance.agreePrivacyPolicy)
                }

                if (agreePrivacyPolicy) {
                    MyApp()
                } else {
                    PrivacyPolicy(
                        cancelClick = {
                            exitProcess(0)
                        },
                        agreeClick = {
                            SettingInfo.instance.agreePrivacyPolicy = true
                            agreePrivacyPolicy = true
                            val channel = "GitHub"
                            // 友盟正式初始化
                            UMConfigure.init(
                                this,
                                Constants.umAppkey,
                                channel,
                                UMConfigure.DEVICE_TYPE_PHONE,
                                ""
                            )
                        }
                    )
                }
            }
        }

        if (SettingInfo.instance.daysignSwitch) {
            RemindersManager.startReminder(this)
        } else {
            RemindersManager.stopReminder(this)
        }
    }

    override fun onStart() {
        super.onStart()

        println("onStart-----")
    }

    override fun onRestart() {
        super.onRestart()

        println("onRestart-----")
    }

    override fun onResume() {
        super.onResume()

        println("onResume-----")
    }

    override fun onPause() {
        super.onPause()

        println("onPause-----")
    }

    override fun onStop() {
        super.onStop()

        println("onStop-----")
    }
}

@Composable
fun PrivacyPolicy(
    cancelClick: () -> Unit,
    agreeClick: () -> Unit
) {
    var isPrivacy by remember { mutableStateOf(false) }
    var isTermsOfService by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        AlertDialog(
            title = {
                Text(text = "隐私政策")
            },
            text = {
                val annotatedText = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = colorTheme(light = Color.Black, dark = Color.White)
                        )
                    ) {
                        append("Msea 尊重并保护所有用户的个人隐私权。为了给您提供更好的服务，Msea 会按照")
                    }

                    pushStringAnnotation(
                        tag = "privacy",
                        annotation = ""
                    )

                    withStyle(
                        style = SpanStyle(color = MaterialTheme.colorScheme.primary)
                    ) {
                        append("《隐私政策》")
                    }
                    pop()

                    withStyle(
                        style = SpanStyle(
                            color = colorTheme(light = Color.Black, dark = Color.White)
                        )
                    ) {
                        append("和")
                    }

                    pushStringAnnotation(
                        tag = "service",
                        annotation = ""
                    )
                    withStyle(
                        style = SpanStyle( color = MaterialTheme.colorScheme.primary )
                    ) {
                        append("《使用条款》")
                    }
                    pop()

                    withStyle(
                        style = SpanStyle(
                            color = colorTheme(light = Color.Black, dark = Color.White)
                        )
                    ) {
                        append(
                            "的规定使用和披露您的个人信息。\n" +
                                    "点击\"我同意\"即表示您已阅读并同意隐私政策与使用条款。"
                        )
                    }
                }

                ClickableText(
                    text = annotatedText,
                    onClick = { offset ->
                        annotatedText.getStringAnnotations(
                            tag = "privacy",
                            start = offset,
                            end = offset
                        ).firstOrNull()?.let {
                            isPrivacy = true
                        }

                        annotatedText.getStringAnnotations(
                            tag = "service",
                            start = offset,
                            end = offset
                        ).firstOrNull()?.let {
                            isTermsOfService = true
                        }
                    }
                )
            },
            onDismissRequest = {},
            dismissButton = {
                Button(
                    onClick = cancelClick
                ) {
                    Text(text = "不同意并退出")
                }
            },
            confirmButton = {
                Button(
                    onClick = agreeClick
                ) {
                    Text(text = "我同意")
                }
            }
        )
        
        if (isPrivacy) {
            AlertDialog(
                title = { Text(text = "隐私政策") },
                text = {
                    WebURL(
                        modifier = Modifier.fillMaxSize(),
                        url = Constants.privacyFileUrl
                    )
                },
                onDismissRequest = {},
                confirmButton = {
                    Button(
                        onClick = { isPrivacy = false }
                    ) {
                        Text(text = "确认")
                    }
                }
            )
        }

        if (isTermsOfService) {
            AlertDialog(
                title = { Text(text = "使用条款") },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(text = Constants.termsOfService)
                    }
                },
                onDismissRequest = {},
                confirmButton = {
                    Button(
                        onClick = { isTermsOfService = false }
                    ) {
                        Text(text = "确认")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun MyApp() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val navController = rememberNavController()
    var isDrawerGesturesEnabled by remember { mutableStateOf(true) }

    ModalNavigationDrawer(
        gesturesEnabled = isDrawerGesturesEnabled,
        drawerState = drawerState,
        drawerContent = {
            DrawerPage(
                navController = navController,
                drawerState = drawerState,
                onClick = { item ->
                    scope.launch { drawerState.close() }
                    if (UserInfo.instance.auth.isEmpty() && item.route != RouteName.SETTING &&
                                item.route != RouteName.ABOUT) {
                        navController.navigate((RouteName.LOGIN))
                    } else {
                        if (item.route == RouteName.LOGOUT) {
                            GlobalScope.launch { DataStoreUtil.clear() }
                            scope.launch {
                                snackbarHostState.showSnackbar(message = "已退出登录")
                            }
                        } else {
                            if (item.route == RouteName.PROFILE_TOPIC) {
                                navController.navigate(RouteName.PROFILE_TOPIC +
                                                               "/${UserInfo.instance.uid}")
                            } else {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id)
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
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
                        isDrawerGesturesEnabled = true

                        TopAppBar(
                            title = {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Msea / 虫部落",
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    currentDestination?.route?.let {
                                        if (it != BottomBarScreen.Home.route) {
                                            val text = if (it == BottomBarScreen.Notice.route)
                                                "Make search easier" else "让搜索更简单"
                                            Text(
                                                text = text,
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
                    } else {
                        isDrawerGesturesEnabled = false
                    }
                },
                bottomBar = {
                    if (mainScreens.contains(currentDestination?.route)) {
                        NavigationBar(
                            containerColor = colorTheme(light = Color.White, dark = Color.Black)
                        ) {
                            screens.forEach { screen ->
                                NavigationBarItem(
                                    icon = {
                                        if (screen.imageVector != null) {
                                            Icon(
                                                imageVector = screen.imageVector,
                                                contentDescription = null
                                            )
                                        } else if (screen.paint != null) {
                                            Icon(
                                                painter = painterResource(id = screen.paint),
                                                contentDescription = null
                                            )
                                        }

                                         },
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
                modifier = Modifier.size(50.dp),
                onClick = { navController.navigate(route = RouteName.SIGN) }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_energy_savings_leaf_24),
                        contentDescription = "签到"
                    )

                    Text(text = "签到")
                }
            }

//            IconButton(onClick = { /* doSomething() */ }) {
//                Icon(
//                    imageVector = Icons.Default.FormatListNumbered,
//                    contentDescription = "排行榜"
//                )
//            }
//
//            IconButton(onClick = { /* doSomething() */ }) {
//                Icon(
//                    imageVector = Icons.Default.Search,
//                    contentDescription = "搜索"
//                )
//            }
        }
        BottomBarScreen.Node.route -> {
            IconButton(onClick = { navController.navigate(route = RouteName.TAG) }) {
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_tag_24),
                    contentDescription = "标签"
                )
            }
        }
        else -> {}
    }
}

@OptIn(DelicateCoroutinesApi::class)
private fun NavGraphBuilder.detailsNav(
    scaffoldState: SnackbarHostState,
    navController: NavHostController
) {
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

    composable(
        route = RouteName.SIGN,
        deepLinks = listOf(
            navDeepLink { uriPattern = "msea://${RouteName.SIGN}" }
        )
    ) {
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
        route = RouteName.TAG_LIST + "/{tagItem}",
        arguments = listOf(navArgument("tagItem") { type = NavType.StringType })
    ) {
        val tagItem = it.arguments?.getString("tagItem")?.fromJson<TagItemModel>()
        tagItem?.let { item ->
            TagListPage(
                scaffoldState = scaffoldState,
                navController = navController,
                tagItem = item
            )
        }
    }

    composable(
        route = RouteName.NODE_LIST + "/{fid}",
        arguments = listOf(navArgument("fid") { type = NavType.StringType })
    ) {
        val fid = it.arguments?.getString("fid")
        fid?.let { id ->
            NodeListPage(
                scaffoldState = scaffoldState,
                navController = navController,
                fid = id
            )
        }
    }

    composable(RouteName.TERMS_OF_SERVICE) {
        TermsOfServicePage(
            navController = navController
        )
    }

    composable(RouteName.PRIVACY_POLICY) {
        PrivacyPolicyPage(
            navController = navController
        )
    }

    composable(RouteName.SDK_LIST) {
        SDKListPage(
            navController = navController
        )
    }

    composable(
        route = RouteName.WEBVIEW + "/{web}",
        arguments = listOf(navArgument("web") { type = NavType.StringType })
    ) {
        val web = it.arguments?.getString("web")?.fromJson<WebViewModel>()
        web?.let { model ->
            WebViewPage(
                web = model,
                navController = navController
            )
        }
    }

    composable(
        route = RouteName.TOPIC_DETAIL + "/{topic}",
        arguments = listOf(navArgument("topic") { type = NavType.StringType })
    ) {
        val topic = it.arguments?.getString("topic")?.fromJson<TopicDetailRouteModel>()
        topic?.let { model ->
            TopicDetailPage(
                scaffoldState = scaffoldState,
                navController = navController,
                tid = model.tid,
                isNodeFid125 = model.isNodeFid125
            )
        }
    }

    composable(RouteName.LICENSE) {
        LicensePage(
            scaffoldState = scaffoldState,
            navController = navController
        )
    }

    composable(
        route = RouteName.PROFILE_DETAIL + "/{uid}",
        arguments = listOf(navArgument("uid") { type = NavType.StringType })
    ) {
        val uid = it.arguments?.getString("uid")
        uid?.let { id ->
            ProfileDetailPage(
                scaffoldState = scaffoldState,
                navController = navController,
                uid = id
            )
        }
    }

    composable(
        route = RouteName.PROFILE_DETAIL_USERNAME + "/{username}",
        arguments = listOf(navArgument("username") { type = NavType.StringType })
    ) {
        val username = it.arguments?.getString("username")
        username?.let { id ->
            ProfileDetailPage(
                scaffoldState = scaffoldState,
                navController = navController,
                username = id
            )
        }
    }

    composable(
        route = RouteName.PROFILE_TOPIC + "/{uid}",
        arguments = listOf(navArgument("uid") { type = NavType.StringType })
    ) {
        val uid = it.arguments?.getString("uid")
        uid?.let { id ->
            ProfileTopicPage(
                scaffoldState = scaffoldState,
                navController = navController,
                uid = id,
                showTopBar = true
            )
        }
    }

    composable(
        route = RouteName.NODE_DETAIL + "/{gid}",
        arguments = listOf(navArgument("gid") { type = NavType.StringType })
    ) {
        val gid = it.arguments?.getString("gid")
        gid?.let { id ->
            NodeDetailPage(
                scaffoldState = scaffoldState,
                navController = navController,
                gid = id
            )
        }
    }
}
