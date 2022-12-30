package com.eternaljust.msea.ui.page.home.topic

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import coil.compose.AsyncImage
import com.eternaljust.msea.R
import com.eternaljust.msea.ui.page.node.tag.TagItemModel
import com.eternaljust.msea.ui.theme.ColorTheme
import com.eternaljust.msea.ui.widget.*
import com.eternaljust.msea.utils.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

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
    val context = LocalContext.current
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect {
            when (it) {
                is TopicDetailViewEvent.PopBack -> {
                    navController.popBackStack()
                }
                is TopicDetailViewEvent.Share -> {
                    openSystemShare(
                        text = viewModel.viewStates.topic.url,
                        title = viewModel.viewStates.topic.title,
                        context = context
                    )
                }
                is TopicDetailViewEvent.Message -> {
                    scaffoldState.showSnackbar(message = it.message)
                }
                is TopicDetailViewEvent.Refresh -> {
                    lazyPagingItems.refresh()
                }
                is TopicDetailViewEvent.Login -> {
                    navController.navigate(route = RouteName.LOGIN)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "帖子详情") },
                navigationIcon = {
                    Row {
                        IconButton(
                            onClick = {
                                viewModel.dispatch(TopicDetailViewAction.PopBack)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "返回"
                            )
                        }
                    }
                },
                actions = {
                    if (viewModel.viewStates.topic.title.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                viewModel.dispatch(TopicDetailViewAction.Share)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "分享"
                            )
                        }
                    }

                    if (viewModel.viewStates.topic.favorite.isNotEmpty() &&
                        UserInfo.instance.auth.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .clickable { viewModel.dispatch(TopicDetailViewAction.Favorite) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "收藏"
                            )
                            
                            val count = viewModel.viewStates.favoriteCount
                            if (count != "0") {
                                Text(text = count)
                            }
                        }
                    }
                },
                colors = mseaTopAppBarColors()
            )
        },
        floatingActionButton = {
            if (viewModel.viewStates.topic.action.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    text = {
                        Text(text = "评论")
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "评论"
                        )
                    },
                    onClick = {
                        viewModel.dispatch(TopicDetailViewAction.CommentShowDialog(isShow = true))
                    },
                    expanded = listState.isScrollInProgress
                )
            }
        },
        content = { paddingValues ->
            Surface(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                TopicAlertDialog(
                    commentText = viewModel.viewStates.commentText,
                    commentTextChange = {
                        viewModel.dispatch(TopicDetailViewAction.CommentTextChange(it))
                    },
                    commentConfirm = {
                        viewModel.dispatch(TopicDetailViewAction.Comment)
                    },
                    showCommentDialog = viewModel.viewStates.showCommentDialog,
                    commentDialogClick = {
                        viewModel.dispatch(TopicDetailViewAction.CommentShowDialog(it))
                    }
                )
                
                RefreshListState(
                    lazyPagingItems = lazyPagingItems,
                    listState = listState
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
                            TopicDetailItemContent(
                                item = it,
                                avatarClick = {
                                    navController.navigate(RouteName.PROFILE_DETAIL + "/${it.uid}")
                                },
                                userOrTopicClick = { url ->
                                    if (url.contains("uid")) {
                                        val uid = NetworkUtil.getUid(url)
                                        navController.navigate(RouteName.PROFILE_DETAIL + "/$uid")
                                    } else if (url.contains("tid") || url.contains("thread")) {
                                        val tid = NetworkUtil.getTid(url)
                                        navController.navigate(RouteName.TOPIC_DETAIL + "/$tid")
                                    }
                                },
                                supportClick = { action ->
                                    viewModel.dispatch(TopicDetailViewAction.Support(action = action))
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicAlertDialog(
    commentText: String,
    commentTextChange: (String) -> Unit,
    commentConfirm: () -> Unit,
    showCommentDialog: Boolean,
    commentDialogClick: (Boolean) -> Unit,
) {
    if (showCommentDialog) {
        AlertDialog(
            onDismissRequest = { commentDialogClick(false) },
            title = { Text(text = "帖子主题回复") },
            text = {
                TextField(
                    modifier = Modifier.height(100.dp),
                    value = commentText,
                    onValueChange = { commentTextChange(it) },
                    placeholder = { Text(text = "输入回复评论内容") }
                )
            },
            dismissButton = {
                Button(onClick = { commentDialogClick(false) }) {
                    Text(text = "取消")
                }
            },
            confirmButton = {
                Button(
                    enabled = commentText.isNotEmpty(),
                    onClick = { commentConfirm() }
                ) {
                    Text(text = "发表回复")
                }
            }
        )
    }
}

@Composable
fun TopicDetailHeader(
    topic: TopicDetailModel,
    nodeClick: (String) -> Unit,
    nodeListClick: (String) -> Unit,
    tagClick: (TagItemModel) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
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

@Composable
fun TopicDetailItemContent(
    item: TopicCommentModel,
    avatarClick: () -> Unit,
    userOrTopicClick: (String) -> Unit,
    supportClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
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

                Column {
                    Text(
                        modifier = Modifier.clickable { avatarClick() },
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = item.time,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            Text(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 5.dp),
                text = item.sup,
                color = Color.White,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }

    if (item.isText) {
        if (item.blockquoteTime.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .clip(shape = RoundedCornerShape(4))
                    .background(
                        ColorTheme(
                            light = Color(0xFFF9F9F9),
                            dark = Color(0xFF2C2C2E)
                        )
                    )
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 30.dp, vertical = 4.dp),
                    text = item.blockquoteTime,
                    color = Color(0xFF999999),
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    modifier = Modifier
                        .padding(horizontal = 30.dp, vertical = 4.dp),
                    text = item.blockquoteContent
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            text = item.content,
            textAlign = TextAlign.Left
        )
    } else {
        WebHTML(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            html = item.content,
            userOrTopicClick = { userOrTopicClick(it) }
        )
    }

    Spacer(modifier = Modifier.height(4.dp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        if (item.reply.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .height(20.dp)
                    .clickable { },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(id = R.drawable.ic_baseline_sms_24),
                    contentDescription = "回复",
                    tint = MaterialTheme.colorScheme.secondaryContainer
                )

                Text(
                    text = " 回复",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondaryContainer
                )
            }
        }

        if (item.support.isNotEmpty()) {
            Spacer(modifier = Modifier.width(10.dp))

            Row(
                modifier = Modifier
                    .height(20.dp)
                    .clickable { supportClick(item.support) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = "支持",
                    tint = MaterialTheme.colorScheme.secondaryContainer
                )

                Text(
                    text = " 支持",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondaryContainer
                )

                Text(
                    text = " ${item.supportCount}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Red
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(4.dp))

    Divider(modifier = Modifier)
}