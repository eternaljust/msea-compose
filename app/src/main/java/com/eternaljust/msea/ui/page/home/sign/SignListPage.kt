package com.eternaljust.msea.ui.page.home.sign

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

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
            items(viewModel.viewStates.list) { item ->
                Row {
                    Text(text = item.no)

                    Text(text = item.name)

                    Text(text = item.content)

                    Text(text = item.bits)

                    Text(text = item.time)
                }
            }
        }
    }
}

@Composable
fun SignDayListPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    tabItem: SignTabItem,
    viewModel: SignListViewModel = viewModel()
) {
    Text(text = tabItem.title)
}