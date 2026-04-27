package com.eternaljust.msea.ui.page.notice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.eternaljust.msea.R
import com.eternaljust.msea.ui.page.notice.interactive.InteractivePage
import com.eternaljust.msea.ui.page.notice.post.MyPostPage
import com.eternaljust.msea.ui.page.notice.system.SystemPage
import com.eternaljust.msea.ui.widget.MainTopAppBar
import com.eternaljust.msea.utils.RouteName
import com.eternaljust.msea.utils.StatisticsTool
import com.eternaljust.msea.utils.UserInfo
import kotlinx.coroutines.launch

@Composable
fun NoticePage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    onMenuClick: () -> Unit,
    viewModel: NoticeViewModel = viewModel()
) {
    val items = viewModel.items
    val pagerState = rememberPagerState { items.size }
    val scope = rememberCoroutineScope()
    val isLogin = UserInfo.instance.auth.isNotEmpty()
    val context = LocalContext.current

    Column {
        MainTopAppBar(
            onMenuClick = onMenuClick,
            subtitle = "Make search easier"
        )

        Surface(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (!isLogin) {
                    Button(
                        onClick = {
                            navController.navigate(RouteName.LOGIN)
                            StatisticsTool.instance.eventObject(
                                context = context,
                                resId = R.string.event_page_login,
                                keyAndValue = mapOf(R.string.key_source to "通知")
                            )
                        }
                    ) {
                        Text(text = "登录")
                    }
                } else {
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
                                            R.string.key_name_notice to item.title
                                        )
                                    )
                                }
                            )
                        }
                    }

                    HorizontalPager(state = pagerState) { page ->
                        when (items[page]) {
                            NoticeTabItem.MYPOST -> MyPostPage(
                                scaffoldState = scaffoldState,
                                navController = navController
                            )
                            NoticeTabItem.INTERACTIVE -> InteractivePage(
                                scaffoldState = scaffoldState,
                                navController = navController
                            )
                            NoticeTabItem.SYSTEM -> SystemPage(
                                scaffoldState = scaffoldState,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}
