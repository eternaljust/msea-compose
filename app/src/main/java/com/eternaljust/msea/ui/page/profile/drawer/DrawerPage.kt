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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.eternaljust.msea.R
import com.eternaljust.msea.utils.RouteName
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

    LaunchedEffect(Unit) {
        snapshotFlow { drawerState.currentValue }
            .collect {
                println("drawerState = $it")
                viewModel.dispatch(DrawerViewAction.Login)
                if (it == DrawerValue.Open && viewModel.viewStates.isLogin) {
                    viewModel.dispatch(DrawerViewAction.GetProfile)
                }
            }
    }
    
    if (viewModel.viewStates.showLogout) {
        AlertDialog(
            title = {
                Text(text = "提示")
            },
            text = {
                Text(text = "退出登录后，相关设置会被重置")
            },
            onDismissRequest = {
                viewModel.dispatch(DrawerViewAction.LogoutDialog(false))
            },
            dismissButton = {
                Button(
                    onClick = { viewModel.dispatch(DrawerViewAction.LogoutDialog(false)) }
                ) {
                    Text(text = "取消")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.dispatch(DrawerViewAction.LogoutDialog(false))
                        onClick(DrawerNavigationItem.Logout)
                    }
                ) {
                    Text(text = "确认退出")
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
                onClick = { item ->
                    onClick(item)
                }
            )

            if (viewModel.viewStates.isLogin) {
                Divider(modifier = Modifier.padding(horizontal = 16.dp))

                DrawerList(
                    items = viewModel.logoutItems,
                    onClick = {
                        viewModel.dispatch(DrawerViewAction.LogoutDialog(show = true))
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DrawerList(items: List<DrawerNavigationItem>, onClick: (DrawerNavigationItem) -> Unit) {
    items.forEach { item ->
        NavigationDrawerItem(
            icon = { Icon(item.icon, contentDescription = null) },
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
            .height(200.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isLogin) {
                AsyncImage(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(shape = RoundedCornerShape(6)),
                    model = userInfo.avatar,
                    placeholder = painterResource(id = R.drawable.icon),
                    contentDescription = null
                )

                Text(
                    modifier = Modifier
                        .offset(y = 10.dp),
                    text = "${userInfo.name} uid(${userInfo.uid})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                val level = if (userInfo.level.contains("(")) {
                    userInfo.level
                } else {
                    "用户组(${userInfo.level})"
                }
                Text(
                    modifier = Modifier
                        .offset(y = 10.dp),
                    text = level,
                    color = MaterialTheme.colorScheme.secondary
                )

                Row(
                    modifier = Modifier
                        .offset(y = 10.dp)
                ) {
                    Text(
                        text = "好友: "
                    )

                    Text(
                        text = userInfo.friend,
                        color = MaterialTheme.colorScheme.primary
                    )

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
                        .offset(y = 10.dp)
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
        }
    }
}