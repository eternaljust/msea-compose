package com.eternaljust.msea.ui.page.node.tag

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import coil.compose.AsyncImage
import com.eternaljust.msea.R
import com.eternaljust.msea.ui.page.home.topic.TopicDetailRouteModel
import com.eternaljust.msea.ui.widget.NormalTopAppBar
import com.eternaljust.msea.ui.widget.RefreshList
import com.eternaljust.msea.ui.widget.WebViewModel
import com.eternaljust.msea.ui.widget.mseaTopAppBarColors
import com.eternaljust.msea.utils.HTMLURL
import com.eternaljust.msea.utils.RouteName
import com.eternaljust.msea.utils.toJson

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
            NormalTopAppBar(
                title = "标签：${tagItem.title}",
                onClick = { viewModel.dispatch(TagListViewAction.PopBack) }
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
                    stickyHeader {
                        TagListHeader()
                    }

                    itemsIndexed(lazyPagingItems) { _, item ->
                        item?.let {
                            TagListItemContent(
                                item = it,
                                nicknameClick = { name ->
                                    val route = RouteName.PROFILE_DETAIL_USERNAME + "/$name"
                                    navController.navigate(route)
                                },
                                contentClick = {
                                    val topic = TopicDetailRouteModel(tid = it.tid)
                                    val args = String.format("/%s", Uri.encode(topic.toJson()))
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
fun TagListHeader() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "作者")

            Text(text = "版块")

            Text(text = "最后发表")

            Text(text = "回复/查看")
        }
    }
}

@Composable
fun TagListItemContent(
    item: TagListModel,
    nicknameClick: (String) -> Unit,
    contentClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(20.dp),
                model = item.gif,
                placeholder = painterResource(id = R.drawable.icon),
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        modifier = Modifier.clickable { nicknameClick(item.name) },
                        text = item.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = item.time,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Text(text = item.forum)

                Column {
                    Text(
                        modifier = Modifier.clickable { nicknameClick(item.lastName) },
                        text = item.lastName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = item.lastTime,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Text(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.secondary,
                            shape = RoundedCornerShape(50)
                        )
                        .padding(horizontal = 5.dp),
                    text = "${item.reply}/${item.examine}",
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            modifier = Modifier
                .clickable {
                    contentClick()
                },
            text = item.title
        )
    }

    Divider(modifier = Modifier)
}
