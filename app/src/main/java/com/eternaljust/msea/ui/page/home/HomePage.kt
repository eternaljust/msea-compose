package com.eternaljust.msea.ui.page.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomePage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: HomeViewModel = viewModel()
) {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val items = viewModel.topicItems
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

            HorizontalPager(count = items.size, state = pagerState) {
                if (it == pagerState.currentPage) {

                    val vm = when (items[pagerState.currentPage]) {
                        TopicTabItem.NEW -> TopicListViewModel.new
                        TopicTabItem.HOT -> TopicListViewModel.hot
                        TopicTabItem.NEWTHREAD -> TopicListViewModel.newthread
                        TopicTabItem.SOFA -> TopicListViewModel.sofa
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
}
