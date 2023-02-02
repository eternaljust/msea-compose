package com.eternaljust.msea.ui.page.profile.setting

import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.eternaljust.msea.R
import com.eternaljust.msea.ui.page.profile.login.InvitationRegisterButton
import com.eternaljust.msea.ui.widget.ListArrowForward
import com.eternaljust.msea.ui.widget.NormalTopAppBar
import com.eternaljust.msea.ui.widget.WebViewModel
import com.eternaljust.msea.ui.widget.mseaTopAppBarColors
import com.eternaljust.msea.utils.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AboutPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: AboutViewModel = viewModel()
) {
    viewModel.dispatch(AboutViewAction.GetVersion)

    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect {
            when (it) {
                is AboutViewEvent.PopBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("关于 Msea") },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.dispatch(AboutViewAction.PopBack) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    if (UserInfo.instance.auth.isEmpty()) {
                        InvitationRegisterButton()
                    }
                },
                colors = mseaTopAppBarColors()
            )
        },
        content = { paddingValues ->
            if (viewModel.viewStates.versionShowDialog) {
                AlertDialog(
                    title = {
                        Text(text = "${viewModel.viewStates.configVersion.versionName} 新版本更新")
                    },
                    text = {
                        Column {
                            Text(text = "发布时间：${viewModel.viewStates.configVersion.updateTime}\n")
                            Text(text = "更新内容：\n${viewModel.viewStates.configVersion.versionContent}")
                        }
                    },
                    onDismissRequest = {
                        viewModel.dispatch(AboutViewAction.VersionShowDialog(false))
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                viewModel.dispatch(AboutViewAction.VersionShowDialog(false))
                            }
                        ) {
                            Text(text = "取消")
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.dispatch(AboutViewAction.VersionShowDialog(false))
                                openSystemBrowser(
                                    url = HTMLURL.APP_RELEASE,
                                    context = context
                                )
                            }
                        ) {
                            Text(text = "更新")
                        }
                    }
                )
            }

            LazyColumn(contentPadding = paddingValues) {
                stickyHeader {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Image(
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(6)),
                            painter = painterResource(id = R.drawable.icon),
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.clickable {
                                if (viewModel.versionCode < viewModel.viewStates.configVersion.versionCode) {
                                    viewModel.dispatch(AboutViewAction.VersionShowDialog(true))
                                }
                            },
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(text = "${viewModel.versionName} (${viewModel.versionCode})")

                            if (viewModel.versionCode < viewModel.viewStates.configVersion.versionCode) {
                                Spacer(modifier = Modifier.width(5.dp))

                                Text(
                                    modifier = Modifier
                                        .background(
                                            color = Color.Red,
                                            shape = RoundedCornerShape(50)
                                        )
                                        .padding(horizontal = 5.dp),
                                    text = "New",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }

                items(viewModel.items) {
                    ListItem(
                        modifier = Modifier
                            .clickable {
                                if (it.route == RouteName.SOURCE_CODE) {
                                    val url = HTMLURL.APP_GITHUB
                                    val web = WebViewModel(url = url)
                                    val args = String.format("/%s", Uri.encode(web.toJson()))
                                    navController.navigate(RouteName.WEBVIEW + args)
                                } else {
                                    navController.navigate(it.route)
                                }
                            },
                        headlineText = { Text(text = it.title) },
                        trailingContent = { ListArrowForward() }
                    )

                    Divider()
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicensePage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController
) {
    val items: List<WebViewModel> = listOf(
        WebViewModel(title = "jsoup", url = "https://jsoup.org/"),
        WebViewModel(title = "okhttp", url = "https://square.github.io/okhttp/"),
        WebViewModel(title = "coil-compose", url = "https://coil-kt.github.io/coil/compose/"),
        WebViewModel(
            title = "compose-material3-datetime-pickers",
            url = "https://github.com/marosseleng/compose-material3-datetime-pickers"
        )
     )

    Scaffold(
        topBar = {
            NormalTopAppBar(
                title = "开源协议",
                onClick = { navController.popBackStack() }
            )
        },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentPadding = paddingValues
            ) {
                items(items) {
                    ListItem(
                        modifier = Modifier
                            .clickable {
                                val args = String.format("/%s", Uri.encode(it.toJson()))
                                navController.navigate(RouteName.WEBVIEW + args)
                            },
                        headlineText = { Text(text = it.title) },
                        trailingContent = { ListArrowForward() }
                    )

                    Divider()
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SDKListPage(
    navController: NavHostController
) {
    val text = """
友盟+（umeng）SDK

使用目的：统计分析、性能监控

数据类型：设备Mac地址、唯一设备识别码（IMEI/android ID/IDFA/OPENUDID/IP地址/GUID、SIM 卡 IMSI 信息）以提供统计分析服务，并通过地理位置校准报表数据准确性，提供基础反作弊能力。

隐私政策/官网链接：https://www.umeng.com/policy

公司全称：友盟同欣（北京）科技有限公司
"""

    Scaffold(
        topBar = {
            NormalTopAppBar(
                title = "SDK 目录",
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