package com.eternaljust.msea.ui.page.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.eternaljust.msea.R
import com.eternaljust.msea.utils.RouteName

sealed class DrawerNavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Topic : DrawerNavigationItem(
        route = RouteName.Profile_Topic,
        title = "主题",
        icon = Icons.Default.Topic
    )

    object Friend : DrawerNavigationItem(
        route = RouteName.Profile_Friend,
        title = "好友",
        icon = Icons.Default.Group
    )

    object Favorite : DrawerNavigationItem(
        route = RouteName.Profile_Favorite,
        title = "收藏",
        icon = Icons.Default.Favorite
    )

    object Setting : DrawerNavigationItem(
        route = RouteName.Setting,
        title = "设置",
        icon = Icons.Default.Settings
    )

    object About : DrawerNavigationItem(
        route = RouteName.About,
        title = "关于",
        icon = Icons.Default.Info
    )
}

@Composable
fun DrawerPage(onClick: (DrawerNavigationItem) -> Unit) {
    val items1 = listOf(DrawerNavigationItem.Topic, DrawerNavigationItem.Friend, DrawerNavigationItem.Favorite)
    val items2 = listOf(DrawerNavigationItem.Setting, DrawerNavigationItem.About)

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        DrawerHeader()

        DrawerList(
            items = items1,
            onClick = { item ->
                onClick(item)
            }
        )

        Divider(modifier = Modifier.padding(horizontal = 16.dp))

        DrawerList(
            items = items2,
            onClick = { item ->
                onClick(item)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerList(items: List<DrawerNavigationItem>, onClick: (DrawerNavigationItem) -> Unit) {
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
fun DrawerHeader() {
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
            Image(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(6)),
                painter = painterResource(id = R.drawable.icon),
                contentDescription = null
            )

            Text(text = "未登录")
        }
    }
}