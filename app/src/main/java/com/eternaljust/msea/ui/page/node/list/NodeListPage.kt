package com.eternaljust.msea.ui.page.node.list

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import coil.compose.AsyncImage
import com.eternaljust.msea.R
import com.eternaljust.msea.ui.page.home.TopicListItemContent
import com.eternaljust.msea.ui.page.home.topic.TopicListModel
import com.eternaljust.msea.ui.widget.NormalTopAppBar
import com.eternaljust.msea.ui.widget.RefreshList
import com.eternaljust.msea.ui.widget.WebViewModel
import com.eternaljust.msea.ui.widget.mseaTopAppBarColors
import com.eternaljust.msea.utils.HTMLURL
import com.eternaljust.msea.utils.RouteName
import com.eternaljust.msea.utils.toJson

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NodeListPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    fid: String,
    viewModel: NodeListViewModel = viewModel()
) {
    viewModel.dispatch(NodeListViewAction.SetFid(fid))
    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect {
            when (it) {
                is NodeListViewEvent.PopBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            NormalTopAppBar(
                title = "节点：${viewModel.viewStates.title}",
                onClick = { viewModel.dispatch(NodeListViewAction.PopBack) }
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
                        NodeListHeader(viewStates.header)
                    }

                    itemsIndexed(lazyPagingItems) { _, item ->
                        item?.let {
                            TopicListItemContent(
                                item = item,
                                contentClick = {
                                    var url = HTMLURL.TOPIC_DETAIL + "-${item.tid}-1-1.html"
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
fun NodeListHeader(model: NodeListHeaderModel) {
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            )  {
                Text(text = "今日：")

                if (model.today.isNotEmpty()) {
                    Text(text = model.today, color = Color.Red)
                }
                
                if (model.todayImage.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(5.dp))

                    Icon(
                        imageVector = if (model.todayImage == "up") Icons.Default.ArrowUpward else
                            Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            )  {
                Text(text = "主题：")

                if (model.topic.isNotEmpty()) {
                    Text(text = model.topic, color = Color.Red)
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "排名：")

                if (model.rank.isNotEmpty()) {
                    Text(text = model.rank, color = Color.Red)
                }

                if (model.rankImage.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(5.dp))

                    Icon(
                        imageVector = if (model.rankImage == "up") Icons.Default.ArrowUpward else
                            Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}
