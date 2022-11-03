package com.eternaljust.msea.ui.page.node.tag

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.eternaljust.msea.ui.page.notice.system.SystemListItemContent
import com.eternaljust.msea.ui.widget.RefreshList
import com.eternaljust.msea.ui.widget.mseaTopAppBarColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagListPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    tagItem: TagItemModel,
    viewModel: TagListViewModel = viewModel()
) {
    viewModel.dispatch(TagListViewAction.SetTid(tagItem.tid))
    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect {
            when (it) {
                is TagListViewEvent.PopBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("标签：${tagItem.title}") },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.dispatch(TagListViewAction.PopBack) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
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
            ) {
                val viewStates = viewModel.viewStates
                val lazyPagingItems = viewStates.pagingData.collectAsLazyPagingItems()

                RefreshList(
                    lazyPagingItems = lazyPagingItems
                ) {
                    itemsIndexed(lazyPagingItems) { _, item ->
                        Text(text = item!!.title)
                    }
                }
            }
        }
    )
}