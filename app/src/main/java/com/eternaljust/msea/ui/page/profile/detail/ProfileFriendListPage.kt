package com.eternaljust.msea.ui.page.profile.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.eternaljust.msea.R
import com.eternaljust.msea.ui.page.notice.NoticeTabItem
import com.eternaljust.msea.ui.widget.RefreshList
import com.eternaljust.msea.utils.RouteName
import com.eternaljust.msea.utils.StatisticsTool

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FriendListPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: FriendListViewModel = viewModel()
) {
    val viewStates = viewModel.viewStates
    val lazyPagingItems = viewStates.pagingData.collectAsLazyPagingItems()
    val context = LocalContext.current

    RefreshList(
        lazyPagingItems = lazyPagingItems
    ) {
        stickyHeader {
            FriendListListHeader(count = viewStates.count)
        }

        items(
            count = lazyPagingItems.itemCount,
            key = lazyPagingItems.itemKey { it.uuid },
        ) { index ->
            val item = lazyPagingItems[index]
            item?.let {
                FriendListListItemContent(
                    item = it,
                    avatarClick = {
                        navController.navigate(RouteName.PROFILE_DETAIL + "/${it.uid}")
                        StatisticsTool.instance.eventObject(
                            context = context,
                            resId = R.string.event_page_profile,
                            keyAndValue = mapOf(
                                R.string.key_source to ProfileFriendTabItem.FRIEND.title
                            )
                        )
                    }
                )
            }
        }
    }
}
@Composable
fun FriendListListHeader(
    count: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "按照好友热度排序")

            Text(text = "当前共有 $count 个好友")
        }
    }
}

@Composable
fun FriendListListItemContent(
    item: FriendListModel,
    avatarClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            modifier = Modifier
                .size(45.dp)
                .clip(shape = RoundedCornerShape(5))
                .clickable { avatarClick() },
            model = item.avatar,
            placeholder = painterResource(id = R.drawable.icon),
            contentDescription = null
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.clickable { avatarClick() },
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = item.hot,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.topic
            )
        }
    }

    Divider(modifier = Modifier)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FriendVisitorTraceListPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: FriendVisitorTraceListViewModel = viewModel()
) {
    val viewStates = viewModel.viewStates
    val lazyPagingItems = viewStates.pagingData.collectAsLazyPagingItems()
    val context = LocalContext.current

    RefreshList(
        lazyPagingItems = lazyPagingItems
    ) {
        stickyHeader {
            FriendVisitorTraceListListHeader(item = viewModel.tabItem)
        }

        items(
            count = lazyPagingItems.itemCount,
            key = lazyPagingItems.itemKey { it.uuid },
        ) { index ->
            val item = lazyPagingItems[index]
            item?.let {
                FriendVisitorTraceListListItemContent(
                    item = it,
                    avatarClick = {
                        navController.navigate(RouteName.PROFILE_DETAIL + "/${it.uid}")
                        StatisticsTool.instance.eventObject(
                            context = context,
                            resId = R.string.event_page_profile,
                            keyAndValue = mapOf(
                                R.string.key_source to viewModel.tabItem.title
                            )
                        )
                    }
                )
            }
        }
    }
}
@Composable
fun FriendVisitorTraceListListHeader(
    item: ProfileFriendTabItem
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            text = item.header
        )
    }
}

@Composable
fun FriendVisitorTraceListListItemContent(
    item: FriendVisitorTraceListModel,
    avatarClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            modifier = Modifier
                .size(45.dp)
                .clip(shape = RoundedCornerShape(5))
                .clickable { avatarClick() },
            model = item.avatar,
            placeholder = painterResource(id = R.drawable.icon),
            contentDescription = null
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.clickable { avatarClick() },
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = item.time,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.topic
            )
        }
    }

    Divider(modifier = Modifier)
}