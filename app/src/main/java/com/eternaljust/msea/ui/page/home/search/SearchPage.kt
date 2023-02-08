package com.eternaljust.msea.ui.page.home.search

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.eternaljust.msea.ui.widget.NormalTopAppBar
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: SearchViewModel = viewModel()
) {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val items = viewModel.items

    Scaffold(
        topBar = {
            NormalTopAppBar(
                title = "站内搜索",
                onClick = { navController.popBackStack() }
            )
        },
        content = { paddingValues ->
            Surface(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                Column {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = "请输入搜索内容",
                        singleLine = true,
                        onValueChange = {  },
                        label = { Text("搜索") }
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    TabRow(selectedTabIndex = pagerState.currentPage) {
                        items.forEachIndexed { index, item ->
                            Tab(
                                text = { Text( text = item.title ) },
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
                        when (items[pagerState.currentPage]) {
                            SearchTabItem.POST -> SearchPostPage(
                                scaffoldState = scaffoldState,
                                navController = navController
                            )
                            SearchTabItem.USER -> SearchUserPage(
                                scaffoldState = scaffoldState,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    )
}