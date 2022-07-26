package com.eternaljust.msea.ui.page.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.pager.ExperimentalPagerApi
import com.eternaljust.msea.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomePage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController
) {
    val scope = rememberCoroutineScope()
    val text = LocalContext.current.resources.getString(R.string.bottom_navigation_home)

    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        Column() {
            val titles = listOf(TopicTabItem.NEW, TopicTabItem.HOT, TopicTabItem.NEWTHREAD, TopicTabItem.SOFA)
            val pagerState = rememberPagerState()
            val scope = rememberCoroutineScope()

            Column {
                TabRow(selectedTabIndex = pagerState.currentPage) {
                    titles.forEachIndexed { index, item ->
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

                HorizontalPager(count = titles.size, state = pagerState) {
                    TopicListPage(
                        scaffoldState = scaffoldState,
                        navController = navController,
                        tabItem = titles[pagerState.currentPage],
                    )
                }
            }
        }
    }
}

interface TopicTab {
    val id: String
    val title: String
}

enum class TopicTabItem : TopicTab {
    NEW{
        override val id: String
            get() = "new"

        override val title: String
            get() = "最新回复"
    },

    HOT{
        override val id: String
        get() = "hot"

        override val title: String
        get() = "热门"
    },

    NEWTHREAD{
        override val id: String
            get() = "newthread"

        override val title: String
            get() = "最新发表"
    },

    SOFA{
        override val id: String
            get() = "sofa"

        override val title: String
            get() = "前排"
    }
}