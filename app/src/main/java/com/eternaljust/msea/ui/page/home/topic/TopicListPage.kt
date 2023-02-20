package com.eternaljust.msea.ui.page.home.topic

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.eternaljust.msea.R
import com.eternaljust.msea.ui.theme.colorTheme
import com.eternaljust.msea.ui.widget.RefreshIndicator
import com.eternaljust.msea.utils.RouteName
import com.eternaljust.msea.utils.StatisticsTool
import com.eternaljust.msea.utils.toJson
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Suppress("DEPRECATION")
@Composable
fun TopicListPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: TopicListViewModel = viewModel()
) {
    val context = LocalContext.current

    if (viewModel.isFirstLoad) {
        viewModel.dispatch(TopicListViewAction.LoadData)
        eventPageLoad(context = context, page = viewModel.page)
    }
    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = viewModel.viewStates.isRefreshing
    )
    val listState = rememberLazyListState()

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            viewModel.dispatch(TopicListViewAction.LoadData)
            eventPageLoad(context = context, page = viewModel.page)
        },
        indicator = { state, refreshTrigger ->
            RefreshIndicator(
                state = state,
                refreshTriggerDistance = refreshTrigger
            )
        }
    ) {
        swipeRefreshState.isRefreshing = viewModel.viewStates.isRefreshing

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            state = listState
        ) {
            itemsIndexed(viewModel.viewStates.list) { index, item ->
                TopicListItemContent(
                    item = item,
                    avatarClick = {
                        navController.navigate(RouteName.PROFILE_DETAIL + "/${item.uid}")
                    },
                    contentClick = {
                        val topic = TopicDetailRouteModel(tid = item.tid)
                        val args = String.format("/%s", Uri.encode(topic.toJson()))
                        navController.navigate(RouteName.TOPIC_DETAIL + args)
                    }
                )

                DisposableEffect(Unit) {
                    if ((index + 1) % viewModel.pageSize == 0 &&
                        index == viewModel.viewStates.list.lastIndex) {
                        viewModel.dispatch(TopicListViewAction.LoadMoreData)
                        eventPageLoad(context = context, page = viewModel.page)
                    }
                    onDispose {}
                }
            }
        }
    }
}

@Composable
fun TopicListItemContent(
    item: TopicListModel,
    isNodeFid125: Boolean = false,
    avatarClick: () -> Unit,
    contentClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
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
                        modifier = Modifier
                            .background(
                                color = if (isNodeFid125) Color.LightGray else
                                    MaterialTheme.colorScheme.secondary,
                                shape = RoundedCornerShape(50)
                            )
                            .padding(horizontal = 5.dp),
                        text = "${item.reply}/${item.examine}",
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = item.time,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    if (item.icon1.isNotEmpty()) {
                        topicAttachmentIcon(
                            icon = item.icon1,
                            isNodeFid125 = isNodeFid125
                        )

                        Spacer(modifier = Modifier.width(5.dp))
                    }

                    if (item.icon2.isNotEmpty()) {
                        topicAttachmentIcon(
                            icon = item.icon2,
                            isNodeFid125 = isNodeFid125
                        )

                        Spacer(modifier = Modifier.width(5.dp))
                    }

                    if (item.icon3.isNotEmpty()) {
                        topicAttachmentIcon(
                            icon = item.icon3,
                            isNodeFid125 = isNodeFid125
                        )

                        Spacer(modifier = Modifier.width(5.dp))
                    }

                    if (item.icon4.isNotEmpty()) {
                        topicAttachmentIcon(
                            icon = item.icon4,
                            isNodeFid125 = isNodeFid125
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        val annotatedText = buildAnnotatedString {
            pushStringAnnotation(
                tag = "content",
                annotation = ""
            )

            withStyle(
                style = SpanStyle(
                    color = colorTheme(light = Color.Black, dark = Color.White)
                )
            ) {
                append(item.title)
            }

            val color = if (isNodeFid125) Color.Gray else
                if (item.attachmentColorRed) Color.Red else
                    colorTheme(light = Color.Black, dark = Color.White)
            withStyle(
                style = SpanStyle( color = color )
            ) {
                append(item.attachment)
            }

            pop()
        }

        ClickableText(
            text = annotatedText,
            onClick = { offset ->
                annotatedText.getStringAnnotations(
                    tag = "content",
                    start = offset,
                    end = offset
                ).firstOrNull()?.let {
                    contentClick()
                }
            }
        )
    }

    Divider(modifier = Modifier)
}

@Suppress("IMPLICIT_CAST_TO_ANY")
@Composable
fun topicAttachmentIcon(
    icon: String,
    isNodeFid125: Boolean = false
) = when (icon) {
    "image" -> Icon(
        painter = painterResource(id = R.drawable.ic_baseline_image_24),
        contentDescription = "帖子包含图片",
        tint = if (isNodeFid125) Color.Gray else MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(20.dp)
    )
    "fire" -> Icon(
        painter = painterResource(id = R.drawable.ic_baseline_local_fire_department_24),
        contentDescription = "帖子高热度",
        tint = if (isNodeFid125) Color.Gray else Color.Red,
        modifier = Modifier.size(20.dp)
    )
    "hand" -> Icon(
        painter = painterResource(id = R.drawable.ic_baseline_waving_hand_24),
        contentDescription = "帖子评价指数",
        tint = if (isNodeFid125) Color.Gray else MaterialTheme.colorScheme.secondary,
        modifier = Modifier.size(20.dp)
    )
    "link" -> Icon(
        painter = painterResource(id = R.drawable.ic_baseline_link_24),
        contentDescription = "帖子包含链接",
        tint = if (isNodeFid125) Color.Gray else Color.Blue,
        modifier = Modifier.size(20.dp)
    )
    "premium" -> Icon(
        painter = painterResource(id = R.drawable.ic_baseline_workspace_premium_24),
        contentDescription = "帖子精华",
        tint = Color.Yellow,
        modifier = Modifier.size(20.dp)
    )
    else -> Color.Unspecified
}

private fun eventPageLoad(
    context: Context,
    page: Int
) {
    StatisticsTool.instance.eventObject(
        context = context,
        resId = R.string.event_page_home,
        keyAndValue = mapOf(
            R.string.key_page_load to page
        )
    )
}