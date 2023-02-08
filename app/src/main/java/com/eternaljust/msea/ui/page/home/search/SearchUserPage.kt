package com.eternaljust.msea.ui.page.home.search

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun SearchUserPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: SearchViewModel = viewModel()
) {
    Text(text = "用户")
}