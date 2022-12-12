package com.eternaljust.msea.ui.page.profile.detail

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.eternaljust.msea.R
import com.eternaljust.msea.ui.widget.NormalTopAppBar
import com.eternaljust.msea.ui.widget.RefreshList
import com.eternaljust.msea.ui.widget.WebViewModel
import com.eternaljust.msea.utils.HTMLURL
import com.eternaljust.msea.utils.RouteName
import com.eternaljust.msea.utils.toJson

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
                val viewStates = viewModel.viewStates
                val lazyPagingItems = viewStates.pagingData.collectAsLazyPagingItems()

                RefreshList(
                    lazyPagingItems = lazyPagingItems
                ) {
                    itemsIndexed(lazyPagingItems) { _, item ->
                        item?.let {
                            ProfileFavoriteListItemContent(
                                item = it,
                                contentClick = {
                                    var url = HTMLURL.TOPIC_DETAIL + "-${it.tid}-1-1.html"
                                    val web = WebViewModel(url = url)
                                    val args = String.format("/%s", Uri.encode(web.toJson()))
                                    navController.navigate(RouteName.TOPIC_DETAIL + args)
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
    contentClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable { contentClick() },
        verticalAlignment = Alignment.CenterVertically
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

    Divider(modifier = Modifier)
}