package com.eternaljust.msea.ui.page.home.search

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.eternaljust.msea.R
import com.eternaljust.msea.ui.page.home.topic.*
import com.eternaljust.msea.ui.theme.colorTheme
import com.eternaljust.msea.ui.widget.RefreshIndicator
import com.eternaljust.msea.utils.RouteName
import com.eternaljust.msea.utils.StatisticsTool
import com.eternaljust.msea.utils.UserInfo
import com.eternaljust.msea.utils.toJson
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalFoundationApi::class)
@Suppress("DEPRECATION")
@Composable
fun SearchPostPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    keyword: String,
    viewModel: SearchPostViewModel = viewModel()
) {
    println("---post---$keyword")
    if (UserInfo.instance.auth.isNotEmpty()) {
        viewModel.dispatch(SearchPostAction.SearchKeyword(keyword))
    }

    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = viewModel.viewStates.isRefreshing
    )
    val listState = rememberLazyListState()
    val context = LocalContext.current

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            viewModel.dispatch(SearchPostAction.SearchKeyword(keyword))
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
            if (keyword.isNotEmpty()) {
                stickyHeader {
                    SearchListHeader(
                        count = "结果: 找到 “${keyword}” 相关内容 ${viewModel.viewStates.list.count()} 个"
                    )
                }
            }

            itemsIndexed(viewModel.viewStates.list) { index, item ->
                SearchPostListItemContent(
                    item = item,
                    avatarClick = {
                        navController.navigate(RouteName.PROFILE_DETAIL_USERNAME + "/${item.name}")
                    },
                    contentClick = {
                        val topic = TopicDetailRouteModel(tid = item.tid)
                        val args = String.format("/%s", Uri.encode(topic.toJson()))
                        navController.navigate(RouteName.TOPIC_DETAIL + args)
                        StatisticsTool.instance.eventObject(
                            context = context,
                            resId = R.string.event_topic_detail,
                            keyAndValue = mapOf(
                                R.string.key_source to "搜索帖子"
                            )
                        )
                    },
                    forumClick = {
                        navController.navigate(RouteName.NODE_LIST + "/${item.fid}")
                    }
                )

                DisposableEffect(Unit) {
                    if ((index + 1) % viewModel.pageSize == 0 &&
                        index == viewModel.viewStates.list.lastIndex) {
                        viewModel.dispatch(SearchPostAction.LoadMoreData)
                    }
                    onDispose {}
                }
            }
        }
    }
}

@Composable
fun SearchPostListItemContent(
    item: SearchPostListModel,
    avatarClick: () -> Unit,
    contentClick: () -> Unit,
    forumClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        SearchKeywordTextItem(
            text = item.title,
            keyword = item.keyword,
            isTitle = true,
            contentClick = { contentClick() }
        )

        Text(
            text = item.replyViews,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )

        SearchKeywordTextItem(text = item.content, keyword = item.keyword)

        val annotatedText = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.primary
                )
            ) {
                append(item.time)
            }

            withStyle(
                style = SpanStyle(
                    color = colorTheme(light = Color.Black, dark = Color.White)
                )
            ) {
                append(" - ")
            }

            pushStringAnnotation(
                tag = "avatar",
                annotation = ""
            )
            withStyle(
                style = SpanStyle( color = Color.Gray )
            ) {
                append(item.name)
            }
            pop()

            withStyle(
                style = SpanStyle(
                    color = colorTheme(light = Color.Black, dark = Color.White)
                )
            ) {
                append(" - ")
            }

            pushStringAnnotation(
                tag = "forum",
                annotation = ""
            )
            withStyle(
                style = SpanStyle( color = Color.Gray )
            ) {
                append(item.plate)
            }
            pop()
        }

        ClickableText(
            text = annotatedText,
            onClick = { offset ->
                annotatedText.getStringAnnotations(
                    tag = "avatar",
                    start = offset,
                    end = offset
                ).firstOrNull()?.let {
                    avatarClick()
                }

                annotatedText.getStringAnnotations(
                    tag = "forum",
                    start = offset,
                    end = offset
                ).firstOrNull()?.let {
                    forumClick()
                }
            }
        )
    }

    Divider(modifier = Modifier)
}

@Composable
fun SearchKeywordTextItem(
    text: String,
    keyword: String,
    isTitle: Boolean = false,
    contentClick: (() -> Unit)? = null
) {
    if (!text.contains(keyword)) {
        if (isTitle) {
            Text(
                modifier = Modifier.clickable { contentClick?.let { it() } },
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
        } else {
            Text(
                text = text
            )
        }
    } else {
        val style1 = if (isTitle)
            MaterialTheme.typography.titleMedium
                .copy(color = Color.Red, fontWeight = FontWeight.Bold)
                .toSpanStyle()
        else
            LocalTextStyle.current.copy(color = Color.Red).toSpanStyle()

        val style2 = if (isTitle)
            MaterialTheme.typography.titleMedium
                .copy(color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                .toSpanStyle()
        else LocalTextStyle.current
            .copy(color = colorTheme(light = Color.Black, dark = Color.White)).toSpanStyle()
        var annotatedText = AnnotatedString("")

        val components = text.split(keyword)
        val texts = components.filter {
            it.isNotEmpty()
        }
        if (texts.count() == 1) {
            when (keyword) {
                text.commonPrefixWith(keyword), text.commonSuffixWith(keyword) -> {
                    val isPrefix = text.commonPrefixWith(keyword) == keyword
                    annotatedText = buildAnnotatedString {
                        if (isTitle) {
                            pushStringAnnotation(
                                tag = "title",
                                annotation = ""
                            )
                        }

                        withStyle(
                            style = if (isPrefix) style1 else style2
                        ) {
                            if (isPrefix) append(keyword) else append(texts.first())
                        }

                        withStyle(
                            style = if (isPrefix) style2 else style1
                        ) {
                            if (isPrefix) append(texts.first()) else append(keyword)
                        }

                        if (isTitle) {
                            pop()
                        }
                    }
                }
                else -> {
                    Text(text)
                }
            }
        } else {
            annotatedText = buildAnnotatedString {
                if (isTitle) {
                    pushStringAnnotation(
                        tag = "title",
                        annotation = ""
                    )
                }

                val last = texts.last()
                texts.forEach {
                    if (it != last) {
                        withStyle(
                            style = style2
                        ) {
                            append(it)
                        }

                        withStyle(
                            style = style1
                        ) {
                            append(keyword)
                        }
                    } else {
                        withStyle(
                            style = style2
                        ) {
                            append(it)
                        }
                    }
                }

                if (isTitle) {
                    pop()
                }
            }
        }

        ClickableText(
            text = annotatedText,
            onClick = { offset ->
                annotatedText.getStringAnnotations(
                    tag = "title",
                    start = offset,
                    end = offset
                ).firstOrNull()?.let {
                    contentClick?.let { it() }
                }
            }
        )
    }
}