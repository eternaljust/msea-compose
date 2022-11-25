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
import com.eternaljust.msea.ui.widget.mseaTopAppBarColors
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProfileCreditPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: ProfileCreditViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect {
            when (it) {
                is ProfileCreditViewEvent.PopBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("我的积分") },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.dispatch(ProfileCreditViewAction.PopBack) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = mseaTopAppBarColors()
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
                            ProfileCreditTabItem.LOG -> CreditLogPage(
                                scaffoldState = scaffoldState,
                                navController = navController
                            )
                            ProfileCreditTabItem.SYSTEM -> CreditSystemPage(
                                scaffoldState = scaffoldState,
                                navController = navController
                            )
                            ProfileCreditTabItem.RULE -> CreditRulePage(
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
