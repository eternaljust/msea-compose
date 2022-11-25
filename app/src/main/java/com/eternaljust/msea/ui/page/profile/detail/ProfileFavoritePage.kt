package com.eternaljust.msea.ui.page.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Feed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.eternaljust.msea.ui.page.profile.detail.ProfileFavoriteListModel
import com.eternaljust.msea.ui.page.profile.detail.ProfileFavoriteListViewAction
import com.eternaljust.msea.ui.page.profile.detail.ProfileFavoriteListViewEvent
import com.eternaljust.msea.ui.page.profile.detail.ProfileFavoriteViewModel
import com.eternaljust.msea.ui.widget.RefreshList
import com.eternaljust.msea.ui.widget.mseaTopAppBarColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileFavoritePage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: ProfileFavoriteViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect {
            when (it) {
                is ProfileFavoriteListViewEvent.PopBack -> {
                    navController.popBackStack()
                }
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("收藏列表") },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.dispatch(ProfileFavoriteListViewAction.PopBack) }
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
                val viewStates = viewModel.viewStates
                val lazyPagingItems = viewStates.pagingData.collectAsLazyPagingItems()

                RefreshList(
                    lazyPagingItems = lazyPagingItems
                ) {
                    itemsIndexed(lazyPagingItems) { _, item ->
                        item?.let {
                            ProfileFavoriteListItemContent(it)
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun ProfileFavoriteListItemContent(item: ProfileFavoriteListModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Feed,
            contentDescription = null
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            buildAnnotatedString {
                append(item.title + "  ")

                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Thin
                    )
                ) {
                    append(item.time)
                }
            }
        )
    }

    Divider(modifier = Modifier)
}