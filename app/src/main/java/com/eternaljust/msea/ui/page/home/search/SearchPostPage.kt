package com.eternaljust.msea.ui.page.home.search

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun SearchPostPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    keyword: String,
    viewModel: SearchPostViewModel = viewModel()
) {
    Text(text = "帖子$keyword")
    println("---post---$keyword")
    viewModel.dispatch(SearchPostAction.SearchKeyword(keyword))
}