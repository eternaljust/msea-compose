package com.eternaljust.msea.ui.page.profile.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.eternaljust.msea.R
import com.eternaljust.msea.ui.widget.NormalTopAppBar
import com.eternaljust.msea.utils.StatisticsTool
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileFriendPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: ProfileFriendViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect {
            when (it) {
                is ProfileFriendViewEvent.PopBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            NormalTopAppBar(
                title = "我的好友",
                onClick = { viewModel.dispatch(ProfileFriendViewAction.PopBack) }
            )
        },
        content = { paddingValues ->
            Surface(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                val items = viewModel.items
                val pagerState = rememberPagerState { items.size }
                val scope = rememberCoroutineScope()

                Column {
                    PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
                        items.forEachIndexed { index, item ->
                            Tab(
                                text = { Text(item.title) },
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    scope.launch {
                                        pagerState.scrollToPage(index)
                                    }
                                    StatisticsTool.instance.eventObject(
                                        context = context,
                                        resId = R.string.event_page_tab,
                                        keyAndValue = mapOf(
                                            R.string.key_name_friend to item.title
                                        )
                                    )
                                }
                            )
                        }
                    }

                    HorizontalPager(state = pagerState) { page ->
                        val item = items[page]
                        when (item) {
                            ProfileFriendTabItem.FRIEND -> FriendListPage(
                                scaffoldState = scaffoldState,
                                navController = navController
                            )
                            else -> FriendVisitorTraceListPage(
                                scaffoldState = scaffoldState,
                                navController = navController,
                                viewModel = if (item == ProfileFriendTabItem.VISITOR)
                                    FriendVisitorTraceListViewModel.visitor else
                                    FriendVisitorTraceListViewModel.trace
                            )
                        }
                    }
                }
            }
        }
    )
}
