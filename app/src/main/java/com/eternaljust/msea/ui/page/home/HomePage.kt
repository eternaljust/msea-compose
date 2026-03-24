package com.eternaljust.msea.ui.page.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.eternaljust.msea.R
import com.eternaljust.msea.ui.page.home.topic.TopicListPage
import com.eternaljust.msea.ui.page.home.topic.TopicListViewModel
import com.eternaljust.msea.utils.StatisticsTool
import kotlinx.coroutines.launch

@Composable
fun HomePage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: HomeViewModel = viewModel()
) {
    val items = viewModel.topicItems
    val pagerState = rememberPagerState { items.size }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        Column {
            TabRow(selectedTabIndex = pagerState.currentPage) {
                items.forEachIndexed { index, item ->
                    Tab(
                        text = { Text( text = item.title ) },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.scrollToPage(index)
                            }
                            StatisticsTool.instance.eventObject(
                                context = context,
                                resId = R.string.event_page_tab,
                                keyAndValue = mapOf(
                                    R.string.key_name_home to item.title
                                )
                            )
                        }
                    )
                }
            }

            HorizontalPager(state = pagerState) { page ->
                val vm = when (items[page]) {
                    TopicTabItem.NEW -> TopicListViewModel.new
                    TopicTabItem.HOT -> TopicListViewModel.hot
                    TopicTabItem.NEWTHREAD -> TopicListViewModel.newthread
                }
                TopicListPage(
                    scaffoldState = scaffoldState,
                    navController = navController,
                    viewModel = vm
                )
            }
        }
    }
}
