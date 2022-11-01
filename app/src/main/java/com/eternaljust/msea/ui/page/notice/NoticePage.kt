package com.eternaljust.msea.ui.page.notice

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.eternaljust.msea.ui.page.notice.post.MyPostPage
import com.eternaljust.msea.ui.page.notice.system.SystemPage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun NoticePage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: NoticeViewModel = viewModel()
) {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val items = viewModel.items

    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
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
                val tabItem = items[pagerState.currentPage]
                if (tabItem == NoticeTabItem.MYPOST) {
                    MyPostPage(scaffoldState = scaffoldState, navController = navController)
                } else if (tabItem == NoticeTabItem.SYSTEM) {
                    SystemPage(scaffoldState = scaffoldState, navController = navController)
                } else {
                    Text(text = tabItem.title)
                }
            }
        }
    }
}