package com.eternaljust.msea.ui.page.profile.detail

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.eternaljust.msea.ui.data.TopicDetailRouteModel
import com.eternaljust.msea.ui.widget.NormalTopAppBar
import com.eternaljust.msea.ui.widget.RefreshList
import com.eternaljust.msea.utils.RouteName
import com.eternaljust.msea.utils.StatisticsTool
import com.eternaljust.msea.utils.toJson

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ProfileTopicPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    uid: String,
    showTopBar: Boolean = true,
    viewModel: ProfileTopicViewModel = viewModel()
) {
    val context = LocalContext.current
    val title = if (showTopBar) "主题列表" else "个人空间主题列表"

    viewModel.dispatch(ProfileTopicViewAction.SetUid(uid = uid))
    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect {
            when (it) {
                is ProfileTopicViewEvent.PopBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            if (showTopBar) {
                NormalTopAppBar(
                    title = title,
                    onClick = { viewModel.dispatch(ProfileTopicViewAction.PopBack) }
                )
            }
        },
        content = { paddingValues ->
            Surface(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = if (showTopBar) 16.dp else 0.dp)
            ) {
                val viewStates = viewModel.viewStates
                val lazyPagingItems = viewStates.pagingData.collectAsLazyPagingItems()

                RefreshList(
                    lazyPagingItems = lazyPagingItems
                ) {
                    stickyHeader {
                        ProfileTopicListHeader()
                    }

                    items(
                        count = lazyPagingItems.itemCount,
                        key = lazyPagingItems.itemKey { it.uuid },
                    ) { index ->
                        val item = lazyPagingItems[index]
                        item?.let {
                            ProfileTopicListItemContent(
                                item = it,
                                nicknameClick = {
                                    val route = RouteName.PROFILE_DETAIL_USERNAME + "/${it.lastName}"
                                    navController.navigate(route)

                                    eventObject(context = context, params = mapOf( R.string.key_source to title ))
                                    if (showTopBar) {
                                        eventObject(context = context, params = mapOf( R.string.key_action to "用户名" ))
                                    }
                                },
                                contentClick = {
                                    val topic = TopicDetailRouteModel(
                                        tid = it.tid,
                                        isNodeFid125 = it.fid == "125"
                                    )
                                    val args = String.format("/%s", Uri.encode(topic.toJson()))
                                    navController.navigate(RouteName.TOPIC_DETAIL + args)
                                    StatisticsTool.instance.eventObject(
                                        context = context,
                                        resId = R.string.event_topic_detail,
                                        keyAndValue = mapOf(
                                            R.string.key_source to title
                                        )
                                    )
                                    if (showTopBar) {
                                        eventObject(context = context, params = mapOf( R.string.key_action to "帖子" ))
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun ProfileTopicListHeader() {
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
            Text(text = "最后发贴")

            Text(text = "版块")

            Text(text = "回复/查看")
        }
    }
}

@Composable
fun ProfileTopicListItemContent(
    item: ProfileTopicListModel,
    nicknameClick: () -> Unit,
    contentClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(20.dp),
                model = item.gif,
                placeholder = painterResource(id = R.drawable.icon),
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        modifier = Modifier.clickable { nicknameClick() },
                        text = item.lastName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = item.lastTime,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Text(text = item.forum)

                Text(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(50)
                        )
                        .padding(horizontal = 5.dp),
                    text = "${item.reply}/${item.examine}",
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            modifier = Modifier
                .clickable {
                    contentClick()
                },
            text = item.title
        )
    }

    Divider(modifier = Modifier)
}

private fun eventObject(
    context: Context,
    params: Map<Int, String>
) {
    StatisticsTool.instance.eventObject(
        context = context,
        resId = R.string.event_page_profile,
        keyAndValue = params
    )
}