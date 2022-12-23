package com.eternaljust.msea.ui.page.node.list

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.eternaljust.msea.R
import com.eternaljust.msea.ui.page.home.topic.TopicListItemContent
import com.eternaljust.msea.ui.widget.NormalTopAppBar
import com.eternaljust.msea.ui.widget.RefreshList
import com.eternaljust.msea.ui.widget.WebViewModel
import com.eternaljust.msea.utils.HTMLURL
import com.eternaljust.msea.utils.RouteName
import com.eternaljust.msea.utils.toJson

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NodeListPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    fid: String,
    viewModel: NodeListViewModel = viewModel()
) {
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
            NormalTopAppBar(
                title = "节点：${viewModel.viewStates.title}",
                onClick = { viewModel.dispatch(NodeListViewAction.PopBack) }
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
                    lazyPagingItems = lazyPagingItems
                ) {
                    stickyHeader {
                        NodeListHeader(viewStates.header)
                    }

                    itemsIndexed(lazyPagingItems) { _, item ->
                        item?.let {
                            TopicListItemContent(
                                item = item,
                                avatarClick = {
                                    navController.navigate(RouteName.PROFILE_DETAIL + "/${item.uid}")
                                },
                                contentClick = {
                                    navController.navigate(RouteName.TOPIC_DETAIL + "/${item.tid}")
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
fun NodeListHeader(model: NodeListHeaderModel) {
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
            val paint = if (model.todayImage == "up") R.drawable.ic_baseline_arrow_upward_24
            else R.drawable.ic_baseline_arrow_downward_24

            Row(
                verticalAlignment = Alignment.CenterVertically
            )  {
                Text(text = "今日：")

                if (model.today.isNotEmpty()) {
                    Text(text = model.today, color = Color.Red)
                }
                
                if (model.todayImage.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(5.dp))

                    Icon(
                        painter = painterResource(id = paint),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            )  {
                Text(text = "主题：")

                if (model.topic.isNotEmpty()) {
                    Text(text = model.topic, color = Color.Red)
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "排名：")

                if (model.rank.isNotEmpty()) {
                    Text(text = model.rank, color = Color.Red)
                }

                if (model.rankImage.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(5.dp))

                    Icon(
                        painter = painterResource(id = paint),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}
