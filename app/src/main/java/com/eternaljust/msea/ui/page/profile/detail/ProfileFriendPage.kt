package com.eternaljust.msea.ui.page.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.eternaljust.msea.ui.page.profile.detail.*
import com.eternaljust.msea.ui.widget.NormalTopAppBar
import com.eternaljust.msea.ui.widget.mseaTopAppBarColors
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProfileFriendPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: ProfileFriendViewModel = viewModel()
) {
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
                val pagerState = rememberPagerState()
                val scope = rememberCoroutineScope()
                val items = viewModel.items

                Column {
                    TabRow(selectedTabIndex = pagerState.currentPage) {
                        items.forEachIndexed { index, item ->
                            Tab(
                                text = { Text(item.title) },
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    scope.launch {
                                        pagerState.scrollToPage(index)
                                    }
                                }
                            )
                        }
                    }

                    HorizontalPager(count = items.size, state = pagerState) {
                        val item = items[pagerState.currentPage]
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
