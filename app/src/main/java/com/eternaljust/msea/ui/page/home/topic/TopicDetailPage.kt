package com.eternaljust.msea.ui.page.home.topic

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.eternaljust.msea.R
import com.eternaljust.msea.ui.page.node.tag.TagItemModel
import com.eternaljust.msea.ui.widget.RefreshList
import com.eternaljust.msea.ui.widget.mseaTopAppBarColors
import com.eternaljust.msea.utils.RouteName
import com.eternaljust.msea.utils.toJson

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TopicDetailPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    tid: String,
    viewModel: TopicDetailViewModel = viewModel()
) {
    viewModel.dispatch(TopicDetailViewAction.SetTid(tid = tid))
    val viewStates = viewModel.viewStates
    val lazyPagingItems = viewStates.pagingData.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "帖子详情") },
                navigationIcon = {
                    Row {
                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "返回"
                            )
                        }
                    }
                },
                colors = mseaTopAppBarColors()
            )
        },
        content = { paddingValues ->
            Surface(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                RefreshList(
                    lazyPagingItems = lazyPagingItems
                ) {
                    stickyHeader{
                        TopicDetailHeader(
                            topic = viewStates.topic,
                            nodeClick = {
                                navController.navigate(RouteName.NODE_DETAIL + "/$it")
                            },
                            nodeListClick = {
                                navController.navigate(RouteName.NODE_LIST + "/$it")
                            },
                            tagClick = {
                                val args = String.format("/%s", Uri.encode(it.toJson()))
                                navController.navigate(RouteName.TAG_LIST + args)
                            }
                        )
                    }

                    itemsIndexed(lazyPagingItems) { _, item ->
                        item?.let {
                            Text(text = it.name)
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun TopicDetailHeader(
    topic: TopicDetailModel,
    nodeClick: (String) -> Unit,
    nodeListClick: (String) -> Unit,
    tagClick: (TagItemModel) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(id = R.drawable.ic_baseline_grid_view_24),
                contentDescription = "节点",
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                modifier = Modifier.clickable { nodeClick("-1") },
                text = "节点",
                color = MaterialTheme.colorScheme.primary
            )

            if (topic.indexTitle.isNotEmpty()) {
                NodeArrowIcon()

                Text(
                    modifier = Modifier.clickable { nodeClick(topic.gid) },
                    text = topic.indexTitle,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (topic.nodeTitle.isNotEmpty()) {
                NodeArrowIcon()

                Text(
                    modifier = Modifier.clickable { nodeListClick(topic.nodeFid) },
                    text = topic.nodeTitle,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = topic.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = topic.commentCount,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )

        if (topic.tags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(id = R.drawable.ic_baseline_tag_24),
                    contentDescription = "节点",
                    tint = MaterialTheme.colorScheme.primary
                )

                topic.tags.forEach {
                    Column(
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(50.dp))
                            .background(MaterialTheme.colorScheme.secondary)
                            .clickable { tagClick(it) },
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(vertical = 3.dp, horizontal = 5.dp),
                            text = it.title,
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NodeArrowIcon() {
    Icon(
        modifier = Modifier.size(20.dp),
        painter = painterResource(id = R.drawable.ic_baseline_arrow_forward_ios_24),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary
    )
}