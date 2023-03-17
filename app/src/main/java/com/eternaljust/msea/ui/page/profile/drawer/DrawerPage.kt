package com.eternaljust.msea.ui.page.profile.drawer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.eternaljust.msea.R
import com.eternaljust.msea.utils.RouteName
import com.eternaljust.msea.utils.SettingInfo
import com.eternaljust.msea.utils.StatisticsTool
import com.eternaljust.msea.utils.UserInfo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerPage(
    navController: NavHostController,
    drawerState: DrawerState,
    onClick: (DrawerNavigationItem) -> Unit,
    viewModel: DrawerViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        snapshotFlow { drawerState.currentValue }
            .collect {
                println("drawerState = $it")
                viewModel.dispatch(DrawerViewAction.Login)
                if (it == DrawerValue.Open) {
                    StatisticsTool.instance.eventObject(
                        context = context,
                        resId = R.string.event_page_home,
                        keyAndValue = mapOf(
                            R.string.key_category to "菜单"
                        )
                    )

                    if (viewModel.viewStates.isLogin) {
                        viewModel.dispatch(DrawerViewAction.GetProfile)
                    }
                    viewModel.dispatch(DrawerViewAction.GetVersion)
                    println("currentCycleCount---${SettingInfo.instance.cycleCount}")
                    println("configCycleCount---${viewModel.viewStates.version.cycleCount}")
                    if (SettingInfo.instance.cycleCount == viewModel.viewStates.version.cycleCount) {
                        // 获取版本更新配置
                        viewModel.dispatch(DrawerViewAction.LoadVersion)
                    } else {
                        SettingInfo.instance.cycleCount += 1
                    }
                }
            }
    }
    
    if (viewModel.viewStates.showLogout) {
        AlertDialog(
            title = {
                Text(text = "提示")
            },
            text = {
                Text(text = "是否确认退出登录？")
            },
            onDismissRequest = {
                viewModel.dispatch(DrawerViewAction.LogoutDialog(false))
                StatisticsTool.instance.eventObject(
                    context = context,
                    resId = R.string.event_list_drawer,
                    keyAndValue = mapOf(
                        R.string.key_logout to "取消"
                    )
                )
            },
            dismissButton = {
                Button(
                    onClick = {
                        StatisticsTool.instance.eventObject(
                            context = context,
                            resId = R.string.event_list_drawer,
                            keyAndValue = mapOf(
                                R.string.key_logout to "取消"
                            )
                        )
                        viewModel.dispatch(DrawerViewAction.LogoutDialog(false))
                    }
                ) {
                    Text(text = "取消")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        StatisticsTool.instance.eventObject(
                            context = context,
                            resId = R.string.event_list_drawer,
                            keyAndValue = mapOf(
                                R.string.key_logout to "确认"
                            )
                        )
                        viewModel.dispatch(DrawerViewAction.LogoutDialog(false))
                        onClick(DrawerNavigationItem.Logout)
                    }
                ) {
                    Text(text = "确认")
                }
            }
        )
    }

    ModalDrawerSheet {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            DrawerHeader(
                isLogin = viewModel.viewStates.isLogin,
                userInfo = viewModel.viewStates.userInfo,
                onClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate(route = RouteName.LOGIN) {
                        popUpTo(navController.graph.findStartDestination().id)
                    }
                    StatisticsTool.instance.eventObject(
                        context = context,
                        resId = R.string.event_page_login,
                        keyAndValue = mapOf(
                            R.string.key_source to "抽屉菜单",
                            R.string.key_location to "抽屉菜单-未登录"
                        )
                    )
                },
                levelClick = {}
            )

            Divider()

            DrawerList(
                items = viewModel.profileItems,
                onClick = { item ->
                    onClick(item)
                }
            )

            Divider(modifier = Modifier.padding(horizontal = 16.dp))

            DrawerList(
                items = viewModel.settingItems,
                isNewVersion = viewModel.viewStates.isNewVersion,
                onClick = { item ->
                    onClick(item)
                }
            )

            if (viewModel.viewStates.isLogin) {
                Divider(modifier = Modifier.padding(horizontal = 16.dp))

                DrawerList(
                    items = viewModel.logoutItems,
                    onClick = {
                        StatisticsTool.instance.eventObject(
                            context = context,
                            resId = R.string.event_list_drawer,
                            keyAndValue = mapOf(
                                R.string.key_item to DrawerNavigationItem.Logout.title
                            )
                        )
                        viewModel.dispatch(DrawerViewAction.LogoutDialog(show = true))
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DrawerList(
    items: List<DrawerNavigationItem>,
    isNewVersion: Boolean = false,
    onClick: (DrawerNavigationItem) -> Unit
) {
    items.forEach { item ->
        NavigationDrawerItem(
            icon = {
                if (item.imageVector != null) {
                    if (item == DrawerNavigationItem.About) {
                        Icon(
                            imageVector = item.imageVector,
                            contentDescription = null,
                            tint = if (isNewVersion) Color.Red else LocalContentColor.current
                        )
                    } else {
                        Icon(imageVector = item.imageVector, contentDescription = null)
                    }
                } else if (item.painter != null) {
                    Icon(painter = painterResource(id = item.painter), contentDescription = null)
                }
            },
            label = { Text(item.title) },
            selected = false,
            onClick = { onClick(item) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}

@Composable
private fun DrawerHeader(
    isLogin: Boolean,
    userInfo: UserInfo,
    onClick: () -> Unit,
    levelClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            if (isLogin) {
                AsyncImage(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(shape = RoundedCornerShape(6)),
                    model = userInfo.avatar,
                    placeholder = painterResource(id = R.drawable.icon),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    modifier = Modifier,
                    text = "${userInfo.name} uid(${userInfo.uid})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(5.dp))

                val level = if (userInfo.level.contains("(")) {
                    userInfo.level
                } else {
                    "用户组(${userInfo.level})"
                }
                Text(
                    modifier = Modifier,
                    text = level,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(5.dp))

                Row(
                    modifier = Modifier
                ) {
                    Text(
                        text = " 回帖: "
                    )

                    Text(
                        text = userInfo.reply,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = " 主题: "
                    )

                    Text(
                        text = userInfo.topic,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(
                    modifier = Modifier
                ) {
                    Text(
                        text = "积分: "
                    )

                    Text(
                        text = userInfo.integral,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = " Bit: "
                    )

                    Text(
                        text = userInfo.bits,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = " 违规: "
                    )

                    Text(
                        text = userInfo.violation,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                Image(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(6)),
                    painter = painterResource(id = R.drawable.icon),
                    contentDescription = null
                )

                Button(onClick = onClick) {
                    Text(text = "未登录")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}