package com.eternaljust.msea.ui.page.node.list

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.eternaljust.msea.R
import com.eternaljust.msea.ui.data.TopicDetailRouteModel
import com.eternaljust.msea.ui.page.home.topic.TopicListItemContent
import com.eternaljust.msea.ui.theme.getIconTintColorSecondary
import com.eternaljust.msea.ui.widget.RefreshList
import com.eternaljust.msea.ui.widget.mseaTopAppBarColors
import com.eternaljust.msea.utils.RouteName
import com.eternaljust.msea.utils.StatisticsTool
import com.eternaljust.msea.utils.toJson

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NodeListPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    fid: String,
    viewModel: NodeListViewModel = viewModel()
) {
    val context = LocalContext.current

    viewModel.dispatch(NodeListViewAction.SetFid(fid))
    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect {
            when (it) {
                is NodeListViewEvent.PopBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("节点：${viewModel.viewStates.title}") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.dispatch(NodeListViewAction.PopBack)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = if (!viewModel.isNodeFid125) mseaTopAppBarColors() else {
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Gray,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                }
            )
        },
        content = { paddingValues ->
            Surface(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                val viewStates = viewModel.viewStates
                val lazyPagingItems = viewStates.pagingData.collectAsLazyPagingItems()

                RefreshList(
                    lazyPagingItems = lazyPagingItems,
                    tint = if (viewModel.isNodeFid125) Color.Gray else
                        MaterialTheme.colorScheme.primary
                ) {
                    stickyHeader {
                        NodeListHeader(
                            model = viewStates.header,
                            isNodeFid125 = viewModel.isNodeFid125
                        )
                    }

                    items(
                        count = lazyPagingItems.itemCount,
                        key = lazyPagingItems.itemKey { it.uuid },
                    ) { index ->
                        val item = lazyPagingItems[index]
                        item?.let {
                            TopicListItemContent(
                                item = item,
                                avatarClick = {
                                    navController.navigate(RouteName.PROFILE_DETAIL + "/${item.uid}")
                                    StatisticsTool.instance.eventObject(
                                        context = context,
                                        resId = R.string.event_page_profile,
                                        keyAndValue = mapOf(
                                            R.string.key_source to "节点：${viewModel.viewStates.title}"
                                        )
                                    )
                                },
                                contentClick = {
                                    val topic = TopicDetailRouteModel(
                                        tid = item.tid,
                                        isNodeFid125 = viewModel.isNodeFid125
                                    )
                                    val args = String.format("/%s", Uri.encode(topic.toJson()))
                                    navController.navigate(RouteName.TOPIC_DETAIL + args)

                                    StatisticsTool.instance.eventObject(
                                        context = context,
                                        resId = R.string.event_topic_detail,
                                        keyAndValue = mapOf(
                                            R.string.key_source to "节点：${viewModel.viewStates.title}"
                                        )
                                    )
                                },
                                isNodeFid125 = viewModel.isNodeFid125
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun NodeListHeader(
    model: NodeListHeaderModel,
    isNodeFid125: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = if (!isNodeFid125) CardDefaults.cardColors() else
            CardDefaults.cardColors(containerColor = Color.LightGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val paint = if (model.todayImage == "up") R.drawable.ic_baseline_arrow_upward_24
            else R.drawable.ic_baseline_arrow_downward_24

            Row(
                verticalAlignment = Alignment.CenterVertically
            )  {
                Text(text = "今日：")

                if (model.today.isNotEmpty()) {
                    Text(text = model.today, color = getTextColor(isNodeFid125))
                }
                
                if (model.todayImage.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(5.dp))

                    Icon(
                        painter = painterResource(id = paint),
                        contentDescription = null,
                        tint = getIconTintColorSecondary(isNodeFid125)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            )  {
                Text(text = "主题：")

                if (model.topic.isNotEmpty()) {
                    Text(text = model.topic, color = getTextColor(isNodeFid125))
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "排名：")

                if (model.rank.isNotEmpty()) {
                    Text(text = model.rank, color = getTextColor(isNodeFid125))
                }

                if (model.rankImage.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(5.dp))

                    Icon(
                        painter = painterResource(id = paint),
                        contentDescription = null,
                        tint = getIconTintColorSecondary(isNodeFid125)
                    )
                }
            }
        }
    }
}

@Composable
private fun getTextColor(isNodeFid125: Boolean): Color {
    return if (isNodeFid125) Color.Gray else Color.Red
}