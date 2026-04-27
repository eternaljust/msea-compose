package com.eternaljust.msea.ui.page.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.eternaljust.msea.R
import com.eternaljust.msea.ui.page.home.topic.TopicListPage
import com.eternaljust.msea.ui.page.home.topic.TopicListViewModel
import com.eternaljust.msea.ui.widget.MainTopAppBar
import com.eternaljust.msea.utils.RouteName
import com.eternaljust.msea.utils.StatisticsTool
import kotlinx.coroutines.launch

@Composable
fun HomePage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    onMenuClick: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val items = viewModel.topicItems
    val pagerState = rememberPagerState { items.size }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Column {
        MainTopAppBar(
            onMenuClick = onMenuClick,
            actions = {
                IconButton(
                    modifier = Modifier.height(60.dp),
                    onClick = {
                        StatisticsTool.instance.eventObject(
                            context = context,
                            resId = R.string.event_page_home,
                            keyAndValue = mapOf(
                                R.string.key_category to "搜索"
                            )
                        )
                        navController.navigate(route = RouteName.SEARCH)
                    }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "搜索"
                        )
                        Text(
                            text = "搜索",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                IconButton(
                    modifier = Modifier.height(60.dp),
                    onClick = {
                        StatisticsTool.instance.eventObject(
                            context = context,
                            resId = R.string.event_page_home,
                            keyAndValue = mapOf(
                                R.string.key_category to "签到"
                            )
                        )
                        StatisticsTool.instance.eventObject(
                            context = context,
                            resId = R.string.event_page_sign,
                            keyAndValue = mapOf(
                                R.string.key_source to "首页"
                            )
                        )
                        navController.navigate(route = RouteName.SIGN)
                    }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_energy_savings_leaf_24),
                            contentDescription = "签到"
                        )
                        Text(
                            text = "签到",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        )

        Surface(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Column {
                PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
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
}
