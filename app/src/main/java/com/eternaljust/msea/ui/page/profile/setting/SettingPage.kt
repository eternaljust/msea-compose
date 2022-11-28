package com.eternaljust.msea.ui.page.profile.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.eternaljust.msea.ui.widget.ListArrowForward
import com.eternaljust.msea.ui.widget.NormalTopAppBar
import com.eternaljust.msea.utils.SettingInfo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: SettingViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    var colorSchemeChecked by remember {
        mutableStateOf(SettingInfo.instance.colorScheme)
    }
    val themeStyleItems = listOf("自动", "浅色", "深色")
    var themeStyleIndex by remember {
        mutableStateOf(SettingInfo.instance.themeStyle)
    }

    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect {
            when (it) {
                is SettingViewEvent.PopBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            NormalTopAppBar(
                title = "设置",
                onClick = { viewModel.dispatch(SettingViewAction.PopBack) }
            )
        },
        content = { paddingValues ->
            LazyColumn(contentPadding = paddingValues) {
                viewModel.itemGroups.forEach { items ->
                    items(items) { item ->
                        ListItem(
                            modifier = Modifier.clickable { navController.navigate(item.route) },
                            headlineText = { Text(text = item.title) },
                            supportingText = {
                                if (item == SettingListItem.COLOR_SCHEME) {
                                    Text(text = "开启后将根据您的桌面壁纸颜色来生成动态调色板方案")
                                }
                            },
                            leadingContent = { SettingListIcon(item = item) },
                            trailingContent = {
                                when (item) {
                                    SettingListItem.COLOR_SCHEME -> {
                                        Switch(
                                            checked = colorSchemeChecked,
                                            onCheckedChange = {
                                                SettingInfo.instance.colorScheme = it
                                                colorSchemeChecked = it
                                                scope.launch {
                                                    scaffoldState.showSnackbar(
                                                        message = "重启 App 后生效"
                                                    )
                                                }
                                            }
                                        )
                                    }
                                    SettingListItem.DARK_MODE -> {
                                        TabRow(
                                            modifier = Modifier.width(180.dp),
                                            selectedTabIndex = themeStyleIndex
                                        ) {
                                            themeStyleItems.forEachIndexed { index, text ->
                                                Tab(
                                                    text = { Text(text) },
                                                    selected = themeStyleIndex == index,
                                                    onClick = {
                                                        SettingInfo.instance.themeStyle = index
                                                        themeStyleIndex = index
                                                        scope.launch {
                                                            scaffoldState.showSnackbar(
                                                                message = "重启 App 后生效"
                                                            )
                                                        }
                                                    }
                                                )
                                            }
                                        }
                                    }
                                    else -> {
                                        ListArrowForward()
                                    }
                                }
                            }
                        )

                        if (item == items.last()) {
                            Divider(modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun SettingListIcon(item: SettingListItem) = when (item) {
    SettingListItem.DARK_MODE -> GetIcon(imageVector = Icons.Default.DarkMode)
    SettingListItem.COLOR_SCHEME -> GetIcon(imageVector = Icons.Default.SettingsBrightness)
    SettingListItem.FEEDBACK -> GetIcon(imageVector = Icons.Default.Feedback)
    SettingListItem.CONTACT_US -> GetIcon(imageVector = Icons.Default.Contacts)
    SettingListItem.SHARE -> GetIcon(imageVector = Icons.Default.Share)
    SettingListItem.CLEAN_CACHE -> GetIcon(imageVector = Icons.Default.CleaningServices)
    SettingListItem.TERMS_OF_SERVICE -> GetIcon(imageVector = Icons.Default.ViewList)
}

@Composable
private fun GetIcon(imageVector: ImageVector) = Icon(
    modifier = Modifier.size(30.dp),
    imageVector = imageVector,
    tint = MaterialTheme.colorScheme.primary,
    contentDescription = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsOfServicePage(
    navController: NavHostController
) {
    val text = """
        虫部落提醒您：在使用虫部落前，请您务必仔细阅读并透彻理解本声明。您可以选择不使用虫部落，但如果您使用虫部落，您的使用行为将被视为对本声明全部内容的认可。
        
        本站搜索聚合工具（快搜、学术搜索、搜书等）所收录等第三方搜索引擎的搜索算法、数据和搜索结果均属其个人或组织行为，不代表本站立场。
        
        鉴于本站搜索聚合工具未使用自建索引/数据存储/分析模式，无法确定您输入的条件进行是否合法，也无法实时监控第三方网站的搜索结果的合法性，所以本站对搜索聚合工具页面检索/分析出的结果不承担责任。如果因以本站的检索/分析结果作为任何商业行为或者学术研究的依据而产生不良后果，虫部落不承担任何法律责任。
        
        任何通过使用本站搜索聚合工具中的第三方搜索引擎而搜索链接到的其它第三方网页均系他人制作或提供，您可能从该第三方网页上获得资讯及享用服务，虫部落对其合法性概不负责，亦不承担任何法律责任。
        
        虫部落注册用户在社区发布的任何软件、插件和脚本等程序，仅用于测试和学习研究，禁止用于商业用途，不能保证其合法性，准确性，完整性和有效性，请根据情况自行判断，虫部落不承担任何法律责任。
        
        虫部落所有资源文件，禁止任何公众号、自媒体进行任何形式的转载、发布。
        
        虫部落对任何个人发布的软件、插件和脚本等程序问题概不负责，包括但不限于由任软件、插件和脚本等程序错误导致的任何损失或损害。
        
        请勿将虫部落的任何内容用于商业或非法目的，否则后果自负。如果任何单位或个人认为虫部落的相关内容可能涉嫌侵犯其权利，则应及时通知管理员并提供身份证明，所有权证明，管理员将在收到认证文件后删除相关脚本。
        
        再次重申：您访问、浏览、使用或者复制了虫部落的任何内容，则视为已接受此声明，请仔细阅读!
        """

    Scaffold(
        topBar = {
            NormalTopAppBar(
                title = "使用条款",
                onClick = { navController.popBackStack() }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues = paddingValues)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(text = text)
            }
        }
    )
}