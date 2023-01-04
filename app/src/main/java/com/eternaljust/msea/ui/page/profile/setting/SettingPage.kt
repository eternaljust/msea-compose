package com.eternaljust.msea.ui.page.profile.setting

import android.content.Context
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.eternaljust.msea.R
import com.eternaljust.msea.ui.widget.ListArrowForward
import com.eternaljust.msea.ui.widget.NormalTopAppBar
import com.eternaljust.msea.utils.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.marosseleng.compose.material3.datetimepickers.time.ui.dialog.TimePickerDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SettingPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: SettingViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val notificationPermissionState = rememberPermissionState(
        android.Manifest.permission.POST_NOTIFICATIONS
    )

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
            if (viewModel.viewStates.isContactUsShow) {
                ContactUsDialog(
                    scaffoldState = scaffoldState,
                    scope = scope,
                    context = context,
                    onDismiss = { viewModel.dispatch(SettingViewAction.UpdateContactUsShow(false)) }
                )
            }

            if (viewModel.viewStates.isTimePickerShow) {
                TimePickerDialog(
                    onDismissRequest = {
                        viewModel.dispatch(SettingViewAction.UpdateTimePickerShow(false))
                    },
                    initialTime = viewModel.viewStates.daysignTime,
                    onTimeChange = {
                        viewModel.dispatch(SettingViewAction.UpdateDaysginTime(it))
                        viewModel.dispatch(SettingViewAction.UpdateTimePickerShow(false))
                        RemindersManager.startReminder(context)
                    },
                    title = { Text(text = "选择签到提醒时间") }
                )
            }

            LazyColumn(contentPadding = paddingValues) {
                viewModel.itemGroups.forEach { items ->
                    items(items) { item ->
                        ListItem(
                            modifier = Modifier.clickable {
                                when (item) {
                                    SettingListItem.SHARE -> {
                                        openSystemShare(
                                            text = "https://github.com/eternaljust/msea-compose",
                                            title = "msea-compose github 源码",
                                            context = context
                                        )
                                    }
                                    SettingListItem.CONTACT_US -> {
                                        viewModel.dispatch(SettingViewAction.UpdateContactUsShow(true))
                                    }
                                    SettingListItem.FEEDBACK -> {
                                        try {
                                            sendEmail(context = context)
                                        } catch (e: Exception) {
                                            scope.launch {
                                                scaffoldState.showSnackbar(
                                                    message = "邮件服务打开失败"
                                                )
                                            }
                                        }
                                    }
                                    SettingListItem.TERMS_OF_SERVICE -> {
                                        navController.navigate(item.route)
                                    }
                                    else -> { }
                                }
                            },
                            headlineText = {
                                SettingListItemTitle(
                                    item = item,
                                    daysignTime = viewModel.viewStates.daysignTime,
                                    timePickerEnbled = viewModel.viewStates.daysignChecked,
                                    timePickerClick = {
                                        viewModel.dispatch(SettingViewAction.UpdateTimePickerShow(true))
                                    }
                                )
                            },
                            supportingText = {
                                if (item == SettingListItem.COLOR_SCHEME) {
                                    Text(text = "开启后将根据您的桌面壁纸颜色来生成动态调色板方案")
                                }
                            },
                            leadingContent = {
                                SettingListItemIcon(item = item)
                            },
                            trailingContent = {
                                SettingListItemAction(
                                    item = item,
                                    daysignChecked = viewModel.viewStates.daysignChecked,
                                    daysignCheckedChange = {
                                        if (it) {
                                            if (notificationPermissionState.status.isGranted) {
                                                viewModel.dispatch(
                                                    SettingViewAction.UpdateDaysignChecked(true)
                                                )
                                                RemindersManager.startReminder(context)
                                            } else {
                                                SettingViewAction.UpdateDaysignChecked(false)
                                                notificationPermissionState.launchPermissionRequest()
                                            }
                                        } else {
                                            viewModel.dispatch(
                                                SettingViewAction.UpdateDaysignChecked(false)
                                            )
                                            RemindersManager.stopReminder(context)
                                        }
                                    },
                                    colorSchemeChecked = viewModel.viewStates.colorSchemeChecked,
                                    colorSchemeCheckedChange = {
                                        viewModel.dispatch(
                                            SettingViewAction.UpdateColorSchemeChecked(it)
                                        )
                                        scope.launch {
                                            scaffoldState.showSnackbar(
                                                message = "重启 App 后生效"
                                            )
                                        }
                                    },
                                    themeStyleItems = viewModel.themeStyleItems,
                                    themeStyleIndex = viewModel.viewStates.themeStyleIndex,
                                    themeStyleTabClick = {
                                        viewModel.dispatch(
                                            SettingViewAction.UpdateThemeStyleIndex(it)
                                        )
                                        scope.launch {
                                            scaffoldState.showSnackbar(
                                                message = "重启 App 后生效"
                                            )
                                        }
                                    }
                                )
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
private fun ContactUsDialog(
    scaffoldState: SnackbarHostState,
    scope: CoroutineScope,
    context: Context,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "联系我们") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Divider()

                TextButton(onClick = {
                    if (isAppInstalled
                            (
                            packageName = "com.sina.weibo",
                            context = context
                        )
                    ) {
                        openApp(
                            url = "sinaweibo://userinfo?uid=3266569590",
                            context = context
                        )
                    } else {
                        openSystemBrowser(
                            url = "https://weibo.com/eternaljust",
                            context = context
                        )
                    }

                    onDismiss()
                }) {
                    Text(text = "微博：@远恒之义")
                }

                Divider()

                val weixin = "eternaljust"
                TextButton(onClick = {
                    textCopyThenPost(
                        textCopied = weixin,
                        context = context
                    )

                    openApp(
                        url = "weixin://",
                        context = context
                    )

                    scope.launch {
                        scaffoldState.showSnackbar(
                            message = "微信号已复制到剪贴板"
                        )
                    }

                    onDismiss()
                }) {
                    Text(text = "加微信：${weixin} 发送：安卓加群")
                }

                Divider()
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(text = "确定")
            }
        }
    )
}

@Composable
private fun SettingListItemIcon(item: SettingListItem) = when (item) {
    SettingListItem.DAY_SIGN -> GetIcon(painter = R.drawable.ic_baseline_alarm_24)
    SettingListItem.DARK_MODE -> GetIcon(painter = R.drawable.ic_baseline_dark_mode_24)
    SettingListItem.COLOR_SCHEME -> GetIcon(painter = R.drawable.ic_baseline_settings_brightness_24)
    SettingListItem.FEEDBACK -> GetIcon(painter = R.drawable.ic_baseline_feedback_24)
    SettingListItem.CONTACT_US -> GetIcon(painter = R.drawable.ic_baseline_contacts_24)
    SettingListItem.SHARE -> GetIcon(imageVector = Icons.Default.Share)
    SettingListItem.TERMS_OF_SERVICE -> GetIcon(painter = R.drawable.ic_baseline_view_list_24)
}

@Composable
private fun GetIcon(imageVector: ImageVector) = Icon(
    modifier = Modifier.size(30.dp),
    imageVector = imageVector,
    tint = MaterialTheme.colorScheme.primary,
    contentDescription = null
)

@Composable
private fun GetIcon(painter: Int) = Icon(
    modifier = Modifier.size(30.dp),
    painter = painterResource(id = painter),
    tint = MaterialTheme.colorScheme.primary,
    contentDescription = null
)

@Composable
fun SettingListItemTitle(
    item: SettingListItem,
    daysignTime: LocalTime,
    timePickerEnbled: Boolean,
    timePickerClick: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = item.title,
            maxLines = 2
        )

        if (item == SettingListItem.DAY_SIGN) {
            Spacer(modifier = Modifier.width(16.dp))

            OutlinedButton(
                enabled = timePickerEnbled,
                onClick = timePickerClick
            ) {
                Text(
                    text = daysignTime.format(
                        DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
                    )
                )
            }
        }
    }
}

@Composable
private fun SettingListItemAction(
    item: SettingListItem,
    daysignChecked: Boolean,
    daysignCheckedChange: (Boolean) -> Unit,
    colorSchemeChecked: Boolean,
    colorSchemeCheckedChange: (Boolean) -> Unit,
    themeStyleItems: List<String>,
    themeStyleIndex: Int,
    themeStyleTabClick: (Int) -> Unit,
) {
    when (item) {
        SettingListItem.DAY_SIGN -> {
            Switch(
                checked = daysignChecked,
                onCheckedChange = { daysignCheckedChange(it) }
            )
        }
        SettingListItem.COLOR_SCHEME -> {
            Switch(
                checked = colorSchemeChecked,
                onCheckedChange = { colorSchemeCheckedChange(it) }
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
                        onClick = { themeStyleTabClick(index) }
                    )
                }
            }
        }
        else -> {
            ListArrowForward()
        }
    }
}

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