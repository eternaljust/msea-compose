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
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.rememberCoroutineScope
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
import com.eternaljust.msea.ui.widget.WebURL
import com.eternaljust.msea.utils.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SettingPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: SettingViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

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

            LazyColumn(contentPadding = paddingValues) {
                viewModel.itemGroups.forEach { items ->
                    items(items) { item ->
                        ListItem(
                            modifier = Modifier.clickable {
                                if (item != SettingListItem.DARK_MODE &&
                                    item != SettingListItem.COLOR_SCHEME) {
                                    StatisticsTool.instance.eventObject(
                                        context = context,
                                        resId = R.string.event_list_drawer,
                                        keyAndValue = mapOf(
                                            R.string.key_setting to item.title
                                        )
                                    )
                                }
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
                                    SettingListItem.TERMS_OF_SERVICE,
                                    SettingListItem.PRIVACY_POLICY -> {
                                        navController.navigate(item.route)
                                    }
                                    else -> { }
                                }
                            },
                            headlineContent = {
                                SettingListItemTitle(
                                    item = item
                                )
                            },
                            supportingContent = {
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
                                    colorSchemeChecked = viewModel.viewStates.colorSchemeChecked,
                                    colorSchemeCheckedChange = {
                                        StatisticsTool.instance.eventObject(
                                            context = context,
                                            resId = R.string.event_list_drawer,
                                            keyAndValue = mapOf(
                                                R.string.key_setting to item.title
                                            )
                                        )
                                        StatisticsTool.instance.eventObject(
                                            context = context,
                                            resId = R.string.event_list_drawer,
                                            keyAndValue = mapOf(
                                                R.string.key_setting_color to if (it) "开启" else "关闭"
                                            )
                                        )

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
                                        StatisticsTool.instance.eventObject(
                                            context = context,
                                            resId = R.string.event_list_drawer,
                                            keyAndValue = mapOf(
                                                R.string.key_setting to item.title
                                            )
                                        )
                                        StatisticsTool.instance.eventObject(
                                            context = context,
                                            resId = R.string.event_list_drawer,
                                            keyAndValue = mapOf(
                                                R.string.key_setting_dark to viewModel.themeStyleItems[it]
                                            )
                                        )

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
    SettingListItem.DARK_MODE -> GetIcon(painter = R.drawable.ic_baseline_dark_mode_24)
    SettingListItem.COLOR_SCHEME -> GetIcon(painter = R.drawable.ic_baseline_settings_brightness_24)
    SettingListItem.FEEDBACK -> GetIcon(painter = R.drawable.ic_baseline_feedback_24)
    SettingListItem.CONTACT_US -> GetIcon(painter = R.drawable.ic_baseline_contacts_24)
    SettingListItem.SHARE -> GetIcon(imageVector = Icons.Default.Share)
    SettingListItem.TERMS_OF_SERVICE -> GetIcon(painter = R.drawable.ic_baseline_view_list_24)
    SettingListItem.PRIVACY_POLICY -> GetIcon(painter = R.drawable.ic_baseline_privacy_tip_24)
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
    item: SettingListItem
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = item.title,
            maxLines = 2
        )
    }
}

@Composable
private fun SettingListItemAction(
    item: SettingListItem,
    colorSchemeChecked: Boolean,
    colorSchemeCheckedChange: (Boolean) -> Unit,
    themeStyleItems: List<String>,
    themeStyleIndex: Int,
    themeStyleTabClick: (Int) -> Unit,
) {
    when (item) {
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
                Text(text = Constants.termsOfService)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyPage(
    navController: NavHostController
) {
    Scaffold(
        topBar = {
            NormalTopAppBar(
                title = "隐私政策",
                onClick = { navController.popBackStack() }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues = paddingValues)
                    .fillMaxWidth()
            ) {
                WebURL(
                    modifier = Modifier.fillMaxSize(),
                    url = Constants.privacyFileUrl
                )
            }
        }
    )
}