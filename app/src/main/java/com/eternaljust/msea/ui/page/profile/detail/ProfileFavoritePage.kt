package com.eternaljust.msea.ui.page.profile.detail

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.eternaljust.msea.R
import com.eternaljust.msea.ui.page.home.topic.TopicDetailRouteModel
import com.eternaljust.msea.ui.widget.NormalTopAppBar
import com.eternaljust.msea.ui.widget.RefreshList
import com.eternaljust.msea.utils.RouteName
import com.eternaljust.msea.utils.toJson
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileFavoritePage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: ProfileFavoriteViewModel = viewModel()
) {
    val viewStates = viewModel.viewStates
    val lazyPagingItems = viewStates.pagingData.collectAsLazyPagingItems()

    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect {
            when (it) {
                is ProfileFavoriteListViewEvent.PopBack -> {
                    navController.popBackStack()
                }
                is ProfileFavoriteListViewEvent.Refresh -> {
                    lazyPagingItems.refresh()
                }
                is ProfileFavoriteListViewEvent.Message -> {
                    viewModel.viewModelScope.launch {
                        scaffoldState.showSnackbar(message = it.message)
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            NormalTopAppBar(
                title = "收藏列表",
                onClick = { viewModel.dispatch(ProfileFavoriteListViewAction.PopBack) }
            )
        },
        content = { paddingValues ->
            Surface(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                if (viewModel.viewStates.showDeleteDialog) {
                    AlertDialog(
                        title = { Text(text = "提示") },
                        text = { Text(text = "您确定要删除此收藏吗？") },
                        onDismissRequest = {
                            viewModel.dispatch(ProfileFavoriteListViewAction.DeleteShowDialog(false))
                        },
                        dismissButton = {
                            Button(
                                onClick = {
                                    viewModel.dispatch(
                                        ProfileFavoriteListViewAction.DeleteShowDialog(
                                            false
                                        )
                                    )
                                }
                            ) { Text(text = "取消") }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    viewModel.dispatch(ProfileFavoriteListViewAction.Delete)
                                }
                            ) { Text(text = "确认") }
                        }
                    )
                }

                RefreshList(
                    lazyPagingItems = lazyPagingItems
                ) {
                    itemsIndexed(lazyPagingItems) { _, item ->
                        item?.let {
                            ProfileFavoriteListItemContent(
                                item = it,
                                contentClick = {
                                    val topic = TopicDetailRouteModel(tid = it.tid)
                                    val args = String.format("/%s", Uri.encode(topic.toJson()))
                                    navController.navigate(RouteName.TOPIC_DETAIL + args)
                                },
                                deleteClick = { action ->
                                    viewModel.dispatch(ProfileFavoriteListViewAction.DeleteAction(action))
                                    viewModel.dispatch(ProfileFavoriteListViewAction.DeleteShowDialog(true))
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun ProfileFavoriteListItemContent(
    item: ProfileFavoriteListModel,
    contentClick: () -> Unit,
    deleteClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable { contentClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_feed_24),
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

        TextButton(onClick = { deleteClick(item.action) }) { Text(text = "删除") }
    }

    Divider(modifier = Modifier)
}