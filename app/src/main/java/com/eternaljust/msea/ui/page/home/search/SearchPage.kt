package com.eternaljust.msea.ui.page.home.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.eternaljust.msea.ui.widget.NormalTopAppBar
import com.eternaljust.msea.utils.RouteName
import com.eternaljust.msea.utils.UserInfo
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class,
       ExperimentalComposeUiApi::class
)
@Composable
fun SearchPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: SearchViewModel = viewModel()
) {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val items = viewModel.items
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect {
            when (it) {
                is SearchViewEvent.PopBack -> navController.popBackStack()
                is SearchViewEvent.SearchKeyboard -> {
                    keyboardController?.hide()

                    viewModel.viewModelScope.launch {
                        if (UserInfo.instance.auth.isEmpty()) {
                            navController.navigate(RouteName.LOGIN)
                            scaffoldState.showSnackbar(
                                message = "站内搜索请先登录"
                            )
                        } else {
                            scaffoldState.showSnackbar(
                                message = if (viewModel.viewStates.keyword.isEmpty()) "请输入搜索关键字！"
                                else "正在搜索\"${viewModel.viewStates.keyword}\"中"
                            )
                        }
                    }
                }
            }
        }
    }
    Scaffold(
        topBar = {
            NormalTopAppBar(
                title = "站内搜索",
                onClick = { viewModel.dispatch(SearchViewAction.PopBack) }
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
                        value = viewModel.viewStates.keyword,
                        singleLine = true,
                        onValueChange = { viewModel.dispatch(SearchViewAction.UpdateKeyword(it)) },
                        label = { Text("搜索") },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            println("搜索")
                            viewModel.dispatch(SearchViewAction.SearchKeyboard)
                        })
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
                                navController = navController,
                                keyword = viewModel.viewStates.searchContent
                            )
                            SearchTabItem.USER -> SearchUserPage(
                                scaffoldState = scaffoldState,
                                navController = navController,
                                keyword = viewModel.viewStates.searchContent
                            )
                        }
                    }
                }
            }
        }
    )
}