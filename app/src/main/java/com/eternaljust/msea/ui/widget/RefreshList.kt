@file:Suppress("DEPRECATION")

package com.eternaljust.msea.ui.widget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun <T : Any> RefreshList(
    lazyPagingItems: LazyPagingItems<T>,
    noMoreDataText: String = "没有更多了",
    tint: Color = MaterialTheme.colorScheme.primary,
    isRefreshing: Boolean = false,
    onRefresh: (() -> Unit) = {},
    itemContent: LazyListScope.() -> Unit
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = false)

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            onRefresh.invoke()
            lazyPagingItems.refresh()
        },
        indicator = { state, refreshTrigger ->
            RefreshIndicator(
                state = state,
                refreshTriggerDistance = refreshTrigger,
                contentColor = tint
            )
        }
    ) {
        swipeRefreshState.isRefreshing =
            ((lazyPagingItems.loadState.refresh is LoadState.Loading) || isRefreshing)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemContent()

            refresh(
                lazyPagingItems = lazyPagingItems,
                swipeRefreshState = swipeRefreshState,
                noMoreDataText = noMoreDataText,
                tint = tint
            )
        }
    }
}

@Composable
fun <T : Any> RefreshListState(
    lazyPagingItems: LazyPagingItems<T>,
    listState: LazyListState,
    noMoreDataText: String = "没有更多了",
    tint: Color = MaterialTheme.colorScheme.primary,
    isRefreshing: Boolean = false,
    onRefresh: (() -> Unit) = {},
    itemContent: LazyListScope.() -> Unit
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = false)

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            onRefresh.invoke()
            lazyPagingItems.refresh()
        },
        indicator = { state, refreshTrigger ->
            RefreshIndicator(
                state = state,
                refreshTriggerDistance = refreshTrigger,
                contentColor = tint
            )
        }
    ) {
        swipeRefreshState.isRefreshing =
            ((lazyPagingItems.loadState.refresh is LoadState.Loading) || isRefreshing)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            state = listState
        ) {
            itemContent()

            refresh(
                lazyPagingItems = lazyPagingItems,
                swipeRefreshState = swipeRefreshState,
                noMoreDataText = noMoreDataText,
                tint = tint
            )
        }
    }
}

fun <T : Any> LazyListScope.refresh(
    lazyPagingItems: LazyPagingItems<T>,
    swipeRefreshState: SwipeRefreshState,
    noMoreDataText: String,
    tint: Color
) {
    // 上拉加载更多的状态：加载中、加载错误以及没有更多数据
    if (!swipeRefreshState.isRefreshing) {
        item {
            lazyPagingItems.apply {
                when (loadState.append) {
                    is LoadState.Loading -> LoadingItem(tint)
                    is LoadState.Error -> ErrorItem { retry() }
                    is LoadState.NotLoading -> {
                        if (loadState.append.endOfPaginationReached) {
                            NoMoreItem(text = noMoreDataText)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun <T : Any> RefreshGrid(
    lazyPagingItems: LazyPagingItems<T>,
    noMoreDataText: String = "没有更多了",
    isRefreshing: Boolean = false,
    onRefresh: (() -> Unit) = {},
    columnCount: Int = 2,
    itemContent: LazyGridScope.() -> Unit
) {
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = true)

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = {
            onRefresh.invoke()
            lazyPagingItems.refresh()
        },
        indicator = { state, refreshTrigger ->
            RefreshIndicator(
                state = state,
                refreshTriggerDistance = refreshTrigger,
                contentColor = MaterialTheme.colorScheme.primary
            )
        }
    ) {
        swipeRefreshState.isRefreshing =
            ((lazyPagingItems.loadState.refresh is LoadState.Loading) || isRefreshing)

        LazyVerticalGrid(
            contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            columns = GridCells.Fixed(count = columnCount),
            content = {
                itemContent()

                // 上拉加载更多的状态：加载中、加载错误以及没有更多数据
                if (!swipeRefreshState.isRefreshing) {
                    item(
                        span = {
                            GridItemSpan(columnCount)
                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            lazyPagingItems.apply {
                                when (loadState.append) {
                                    is LoadState.Loading -> LoadingItem()
                                    is LoadState.Error -> ErrorItem { retry() }
                                    is LoadState.NotLoading -> {
                                        if (loadState.append.endOfPaginationReached) {
                                            NoMoreItem(text = noMoreDataText)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun RefreshIndicator(
    state: SwipeRefreshState,
    refreshTriggerDistance: Dp,
    contentColor: Color = MaterialTheme.colorScheme.primary
) {
    SwipeRefreshIndicator(
        state = state,
        refreshTriggerDistance = refreshTriggerDistance,
        contentColor = contentColor
    )
}

@Composable
fun ErrorItem(retry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = { retry() },
            modifier = Modifier.padding(10.dp)
        ) {
            Text(text = "重试")
        }
    }
}

@Composable
fun NoMoreItem(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

@Composable
fun LoadingItem(tint: Color = MaterialTheme.colorScheme.primary) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            color = tint,
            modifier = Modifier
                .padding(10.dp)
                .height(50.dp)
        )
    }
}