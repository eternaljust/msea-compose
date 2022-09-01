package com.eternaljust.msea.ui.page.home.sign

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SignListPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    tabItem: SignTabItem,
    viewModel: SignListViewModel = viewModel()
) {
    val swipeRefreshState = rememberSwipeRefreshState(viewModel.viewStates.isRefreshing)
    if (viewModel.viewStates.list.isEmpty()) {
        viewModel.dispatch(SignListViewAction.LoadList)
    }

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = { viewModel.dispatch(SignListViewAction.LoadList) },
    ) {
        LazyColumn {
            stickyHeader {
                signListHeader()
            }

            items(viewModel.viewStates.list) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 13.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = item.no)

                    Text(
                        modifier = Modifier
                            .width(60.dp),
                        text = item.name
                    )

                    Text(
                        modifier = Modifier
                            .width(80.dp),
                        text = item.content
                    )

                    Text(text = item.bits)

                    Text(text = item.time)
                }
            }
        }
    }
}

@Composable
private fun signListHeader () {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "排名")

            Text(text = "昵称")

            Text(text = "签到内容")

            Text(text = "奖励")

            Text(text = "签到时间")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SignDayListPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: SignDayListViewModel
) {
    val swipeRefreshState = rememberSwipeRefreshState(viewModel.viewStates.isRefreshing)
    if (viewModel.viewStates.list.isEmpty()) {
        viewModel.dispatch(SignDayListViewAction.LoadList)
    }

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = { viewModel.dispatch(SignDayListViewAction.LoadList) },
    ) {
        LazyColumn {
            stickyHeader {
                signDayListHeader()
            }

            items(viewModel.viewStates.list) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 13.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = item.no)

                    Text(
                        modifier = Modifier
                            .width(80.dp),
                        text = item.name,
                        textAlign = TextAlign.Center
                    )

                    Text(text = item.continuous)

                    Text(text = item.month)

                    Text(text = item.total)

                    Text(
                        text = item.bits,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = item.time,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Thin
                    )
                }
            }
        }
    }
}

@Composable
private fun signDayListHeader () {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "排名")

            Text(text = "昵称")

            Text(text = "连续")

            Text(text = "本月")

            Text(text = "总")

            Text(text = "总奖励")

            Text(text = "上次签到")
        }
    }
}