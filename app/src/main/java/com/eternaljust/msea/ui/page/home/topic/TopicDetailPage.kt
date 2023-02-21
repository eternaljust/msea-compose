package com.eternaljust.msea.ui.page.home.topic

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.eternaljust.msea.R
import com.eternaljust.msea.ui.page.node.tag.TagItemModel
import com.eternaljust.msea.ui.theme.colorTheme
import com.eternaljust.msea.ui.theme.getIconTintColorPrimary
import com.eternaljust.msea.ui.theme.getIconTintColorSecondary
import com.eternaljust.msea.ui.widget.*
import com.eternaljust.msea.utils.*
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TopicDetailPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    tid: String,
    isNodeFid125: Boolean = false,
    viewModel: TopicDetailViewModel = viewModel()
) {
    val context = LocalContext.current

    viewModel.dispatch(TopicDetailViewAction.SetTid(tid = tid))
    if (viewModel.isFirstLoad) {
        viewModel.dispatch(TopicDetailViewAction.LoadData)
        eventObject(context = context, params = mapOf(R.string.key_list_page to viewModel.page.toString()))
    }

    val viewStates = viewModel.viewStates
    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = viewModel.viewStates.isRefreshing
    )
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
                    viewModel.dispatch(TopicDetailViewAction.LoadData)
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
                    if (viewModel.viewStates.pageNumber > 1 &&
                        viewModel.viewStates.pageLoadCompleted) {
                        Box {
                            IconButton(
                                onClick = {
                                    viewModel.dispatch(TopicDetailViewAction.ShowPageNumberMenu(true))
                                    eventObject(context = context, params = mapOf(R.string.key_action to "分页"))
                                }
                            ) {
                                Icon(
                                    modifier = Modifier.size(32.dp),
                                    painter = painterResource(id = R.drawable.ic_baseline_swap_horiz_24),
                                    contentDescription = "选择分页"
                                )
                            }

                            DropdownMenu(
                                expanded = viewModel.viewStates.showPageNumberMenuExpanded,
                                onDismissRequest = {
                                    viewModel.dispatch(TopicDetailViewAction.ShowPageNumberMenu(false))
                                }
                            ) {
                                (1..viewModel.viewStates.pageNumber).forEach { index ->
                                    DropdownMenuItem(
                                        text = { Text("第 $index 页") },
                                        leadingIcon = {
                                            Icon(
                                                painter = painterResource(id = R.drawable.ic_baseline_keyboard_double_arrow_right_24),
                                                contentDescription = "第 $index 页"
                                            )
                                        },
                                        onClick = {
                                            viewModel.dispatch(TopicDetailViewAction.ShowPageNumberMenu(false))
                                            if (index == 1) {
                                                viewModel.dispatch(TopicDetailViewAction.LoadData)
                                            } else {
                                                viewModel.dispatch(TopicDetailViewAction.LoadPageNumber(index))

                                                viewModel.viewModelScope.launch {
                                                    scaffoldState.showSnackbar(message = "开始加载第 $index 页")
                                                }
                                            }
                                            eventObject(context = context, params = mapOf(R.string.key_page_click to index.toString()))
                                        },
                                        enabled = index != viewModel.page
                                    )

                                    Divider()
                                }
                            }
                        }
                    }

                    if (viewModel.viewStates.topic.title.isNotEmpty() && !isNodeFid125) {
                        IconButton(
                            onClick = {
                                viewModel.dispatch(TopicDetailViewAction.Share)
                                eventObject(context = context, params = mapOf(R.string.key_action to "分享"))
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "分享"
                            )
                        }
                    }

                    if (viewModel.viewStates.topic.favorite.isNotEmpty() &&
                        UserInfo.instance.auth.isNotEmpty() && !isNodeFid125) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .clickable {
                                    viewModel.dispatch(TopicDetailViewAction.Favorite)
                                    eventObject(context = context, params = mapOf(R.string.key_action to "收藏"))
                                }
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
                colors = if (!isNodeFid125) mseaTopAppBarColors() else {
                    TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = Color.Gray,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                }
            )
        },
        floatingActionButton = {
            if (viewModel.viewStates.topic.action.isNotEmpty() && !isNodeFid125) {
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
                        eventObject(context = context, params = mapOf(R.string.key_action to "评论"))
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
                val msg = viewModel.viewStates.replyParams.noticeauthormsg
                var title = "帖子主题回复"
                if (msg.isNotEmpty()) {
                    title = "回复@${viewModel.viewStates.replyParams.username}: $msg"
                }
                TopicAlertDialog(
                    title = title,
                    commentText = viewModel.viewStates.commentText,
                    commentTextChange = {
                        viewModel.dispatch(TopicDetailViewAction.CommentTextChange(it))
                    },
                    commentConfirm = {
                        if (msg.isNotEmpty()) {
                            viewModel.dispatch(TopicDetailViewAction.Reply)
                            eventObject(context = context, params = mapOf(R.string.key_reply to "回复"))
                        } else {
                            viewModel.dispatch(TopicDetailViewAction.Comment)
                            eventObject(context = context, params = mapOf(R.string.key_comment to "评论"))
                        }
                    },
                    showCommentDialog = viewModel.viewStates.showCommentDialog,
                    commentDialogClick = {
                        viewModel.dispatch(TopicDetailViewAction.CommentShowDialog(it))
                        if (msg.isNotEmpty()) {
                            eventObject(context = context, params = mapOf(R.string.key_reply to "取消"))
                        } else {
                            eventObject(context = context, params = mapOf(R.string.key_comment to "取消"))
                        }
                    }
                )

                SwipeRefresh(
                    state = swipeRefreshState,
                    onRefresh = {
                        viewModel.dispatch(TopicDetailViewAction.LoadData)
                        eventObject(context = context, params = mapOf(R.string.key_list_page to viewModel.page.toString()))
                    },
                    indicator = { state, refreshTrigger ->
                        RefreshIndicator(
                            state = state,
                            refreshTriggerDistance = refreshTrigger,
                            contentColor = if (isNodeFid125) Color.Gray else
                                MaterialTheme.colorScheme.primary
                        )
                    }
                ) {
                    swipeRefreshState.isRefreshing = viewModel.viewStates.isRefreshing

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        state = listState
                    ) {
                        stickyHeader{
                            TopicDetailHeader(
                                topic = viewStates.topic,
                                isNodeFid125 = isNodeFid125,
                                recommendAddCount = viewStates.recommendAddCount,
                                nodeClick = {
                                    navController.navigate(RouteName.NODE_DETAIL + "/$it")
                                    eventObject(context = context, params = mapOf(R.string.key_action to "节点"))
                                    if (it == "-1") {
                                        eventObject(context = context, params = mapOf(R.string.key_node to "节点"))
                                    } else {
                                        eventObject(context = context, params = mapOf(R.string.key_node to "分区"))
                                    }
                                },
                                nodeListClick = {
                                    navController.navigate(RouteName.NODE_LIST + "/$it")
                                    eventObject(context = context, params = mapOf(R.string.key_action to "节点"))
                                    eventObject(context = context, params = mapOf(R.string.key_node to "板块"))
                                },
                                tagClick = {
                                    val args = String.format("/%s", Uri.encode(it.toJson()))
                                    navController.navigate(RouteName.TAG_LIST + args)
                                    eventObject(context = context, params = mapOf(R.string.key_action to "标签"))
                                    eventObject(context = context, params = mapOf(R.string.key_tag to it.title))
                                },
                                recommendAdd = {
                                    viewModel.dispatch(TopicDetailViewAction.RecommendAdd)
                                    eventObject(context = context, params = mapOf(R.string.key_action to "顶"))
                                },
                                recommendSubtract = {
                                    viewModel.dispatch(TopicDetailViewAction.RecommendSubtract)
                                    eventObject(context = context, params = mapOf(R.string.key_action to "踩"))
                                }
                            )
                        }

                        itemsIndexed(viewModel.viewStates.list) { index, item ->
                            TopicDetailItemContent(
                                item = item,
                                isNodeFid125 = isNodeFid125,
                                avatarClick = {
                                    navController.navigate(RouteName.PROFILE_DETAIL + "/${item.uid}")
                                    eventObject(context = context, params = mapOf(R.string.key_action to "头像"))
                                },
                                userOrTopicClick = { url ->
                                    if (url.contains("uid")) {
                                        val uid = NetworkUtil.getUid(url)
                                        navController.navigate(RouteName.PROFILE_DETAIL + "/$uid")
                                        eventObject(context = context, params = mapOf(R.string.key_link to "@用户"))
                                    } else if (url.contains("tid") || url.contains("thread")) {
                                        val id = NetworkUtil.getTid(url)
                                        val topic = TopicDetailRouteModel(tid = id)
                                        val args = String.format("/%s", Uri.encode(topic.toJson()))
                                        navController.navigate(RouteName.TOPIC_DETAIL + args)
                                        StatisticsTool.instance.eventObject(
                                            context = context,
                                            resId = R.string.event_topic_detail,
                                            keyAndValue = mapOf(
                                                R.string.key_source to "跟帖链接"
                                            )
                                        )
                                        eventObject(context = context, params = mapOf(R.string.key_link to "帖子"))
                                    }
                                },
                                supportClick = { action ->
                                    viewModel.dispatch(TopicDetailViewAction.Support(action = action))
                                    eventObject(context = context, params = mapOf(R.string.key_action to "支持"))
                                },
                                replyClick = { action ->
                                    viewModel.dispatch(TopicDetailViewAction.GetReplyParam(
                                        action = action,
                                        username = item.name
                                    ))
                                    eventObject(context = context, params = mapOf(R.string.key_action to "回复"))
                                }
                            )

                            DisposableEffect(Unit) {
                                if (viewModel.viewStates.pageLoadCompleted &&
                                    index == viewModel.viewStates.list.lastIndex) {
                                    viewModel.dispatch(TopicDetailViewAction.LoadMoreData)
                                    eventObject(context = context, params = mapOf(R.string.key_list_page to viewModel.page.toString()))
                                }
                                onDispose {}
                            }
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
    title: String,
    commentText: String,
    commentTextChange: (String) -> Unit,
    commentConfirm: () -> Unit,
    showCommentDialog: Boolean,
    commentDialogClick: (Boolean) -> Unit,
) {
    if (showCommentDialog) {
        AlertDialog(
            onDismissRequest = { commentDialogClick(false) },
            title = { Text(text = title) },
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
    isNodeFid125: Boolean,
    recommendAddCount: String,
    nodeClick: (String) -> Unit,
    nodeListClick: (String) -> Unit,
    tagClick: (TagItemModel) -> Unit,
    recommendAdd: () -> Unit,
    recommendSubtract: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = if (!isNodeFid125) CardDefaults.cardColors() else
            CardDefaults.cardColors(containerColor = Color.LightGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.ic_baseline_grid_view_24),
                        contentDescription = "节点",
                        tint = getIconTintColorPrimary(isNodeFid125)
                    )

                    Text(
                        modifier = Modifier.clickable { nodeClick("-1") },
                        text = "节点",
                        color = getIconTintColorPrimary(isNodeFid125)
                    )

                    if (topic.indexTitle.isNotEmpty()) {
                        NodeArrowIcon(isNodeFid125)

                        Text(
                            modifier = Modifier.clickable { nodeClick(topic.gid) },
                            text = topic.indexTitle,
                            color = getIconTintColorPrimary(isNodeFid125)
                        )
                    }

                    if (topic.nodeTitle.isNotEmpty()) {
                        NodeArrowIcon(isNodeFid125)

                        Text(
                            modifier = Modifier.clickable { nodeListClick(topic.nodeFid) },
                            text = topic.nodeTitle,
                            color = getIconTintColorPrimary(isNodeFid125)
                        )
                    }
                }

                if (topic.recommendAdd.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(50))
                            .background(getIconTintColorSecondary(isNodeFid125))
                            .clickable { recommendAdd() },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_arrow_drop_up_24),
                            contentDescription = "顶",
                            tint = Color.White
                        )

                        if (recommendAddCount.isNotEmpty()) {
                            Text(
                                modifier = Modifier.padding(
                                    vertical = 3.dp,
                                    horizontal = 5.dp
                                ),
                                text = "顶 $recommendAddCount",
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Column {
                Text(
                    text = topic.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = topic.commentCount,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )

                    if (topic.recommendSubtract.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(50))
                                .background(getIconTintColorSecondary(isNodeFid125))
                                .clickable { recommendSubtract() },
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_baseline_arrow_drop_down_24),
                                contentDescription = "踩",
                                tint = Color.White
                            )

                            Text(
                                modifier = Modifier.padding(
                                    vertical = 3.dp,
                                    horizontal = 5.dp
                                ),
                                text = "踩",
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }

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
                            tint = getIconTintColorPrimary(isNodeFid125)
                        )

                        topic.tags.forEach {
                            Column(
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(50.dp))
                                    .background(getIconTintColorSecondary(isNodeFid125))
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
}

@Composable
private fun NodeArrowIcon(isNodeFid125: Boolean) {
    Icon(
        modifier = Modifier.size(20.dp),
        painter = painterResource(id = R.drawable.ic_baseline_arrow_forward_ios_24),
        contentDescription = null,
        tint = getIconTintColorPrimary(isNodeFid125)
    )
}

@Composable
fun TopicDetailItemContent(
    item: TopicCommentModel,
    isNodeFid125: Boolean,
    avatarClick: () -> Unit,
    userOrTopicClick: (String) -> Unit,
    supportClick: (String) -> Unit,
    replyClick: (String) -> Unit
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
                    contentDescription = null,
                    colorFilter = if (!isNodeFid125) null else
                        ColorFilter.tint(Color.LightGray, BlendMode.Color)
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
                        color = getIconTintColorSecondary(isNodeFid125),
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
                        colorTheme(
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
            isNodeFid125Gray = isNodeFid125,
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
                    .clickable { replyClick(item.reply) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(id = R.drawable.ic_baseline_sms_24),
                    contentDescription = "回复",
                    tint = getTextSecondaryContainer(isNodeFid125)
                )

                Text(
                    text = " 回复",
                    style = MaterialTheme.typography.labelMedium,
                    color = getTextSecondaryContainer(isNodeFid125)
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
                    tint = getTextSecondaryContainer(isNodeFid125)
                )

                Text(
                    text = " 支持",
                    style = MaterialTheme.typography.labelMedium,
                    color = getTextSecondaryContainer(isNodeFid125)
                )

                Text(
                    text = " ${item.supportCount}",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isNodeFid125) Color.Gray else Color.Red
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(4.dp))

    Divider(modifier = Modifier)
}

@Composable
private fun getTextSecondaryContainer(isNodeFid125: Boolean): Color {
    return if (isNodeFid125) Color.Gray else MaterialTheme.colorScheme.secondaryContainer
}

private fun eventObject(
    context: Context,
    params: Map<Int, String>
) {
    StatisticsTool.instance.eventObject(
        context = context,
        resId = R.string.event_topic_detail,
        keyAndValue = params
    )
}