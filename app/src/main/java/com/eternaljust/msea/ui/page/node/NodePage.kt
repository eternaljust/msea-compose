package com.eternaljust.msea.ui.page.node

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.eternaljust.msea.R
import com.eternaljust.msea.ui.page.home.topic.TopicDetailRouteModel
import com.eternaljust.msea.ui.page.node.tag.TagViewAction
import com.eternaljust.msea.ui.page.node.tag.TagViewEvent
import com.eternaljust.msea.ui.theme.ColorTheme
import com.eternaljust.msea.ui.widget.NormalTopAppBar
import com.eternaljust.msea.ui.widget.RefreshList
import com.eternaljust.msea.ui.widget.WebViewModel
import com.eternaljust.msea.utils.HTMLURL
import com.eternaljust.msea.utils.RouteName
import com.eternaljust.msea.utils.toJson

@Composable
fun NodePage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: NodeViewModel = viewModel()
) {
    NodeContent(
        navController = navController,
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NodeDetailPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    gid: String = "",
    viewModel: NodeViewModel = viewModel()
) {
    if (gid != "-1") {
        viewModel.dispatch(NodeViewAction.SetGid(gid = gid))
    }
    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect {
            when (it) {
                is NodeViewEvent.PopBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            NormalTopAppBar(
                title = "节点",
                onClick = { viewModel.dispatch(NodeViewAction.PopBack) }
            )
        },
        content = { paddingValues ->
            Surface(
                modifier = Modifier.padding(paddingValues)
            ) {
                NodeContent(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NodeContent(
    navController: NavHostController,
    viewModel: NodeViewModel
) {
    val viewStates = viewModel.viewStates
    val lazyPagingItems = viewStates.pagingData.collectAsLazyPagingItems()

    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        RefreshList(
            lazyPagingItems = lazyPagingItems
        ) {
            lazyPagingItems.itemSnapshotList.forEach {
                it?.let {
                    stickyHeader {
                        NodeListHeader(
                            item = it
                        ) {
                            val route = RouteName.PROFILE_DETAIL_USERNAME + "/$it"
                            navController.navigate(route)
                        }
                    }

                    items(it.list) { item ->
                        NodeListItemContent(
                            item = item,
                            nodeClick = {
                                navController.navigate(RouteName.NODE_LIST + "/${item.fid}")
                            },
                            nicknameClick = {
                                val route = RouteName.PROFILE_DETAIL_USERNAME + "/${item.username}"
                                navController.navigate(route)
                            },
                            contentClick = {
                                val topic = TopicDetailRouteModel(
                                    tid = item.tid,
                                    isNodeFid125 = item.fid == "125"
                                )
                                val args = String.format("/%s", Uri.encode(topic.toJson()))
                                navController.navigate(RouteName.TOPIC_DETAIL + args)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NodeListHeader(
    item: NodeModel,
    nicknameClick: (String) -> Unit
) {
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
            Text(text = item.title)

            if (item.moderators.isNotEmpty()) {
                Row {
                    Text(text = "分区版主：")

                    item.moderators.forEach {
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .clickable { nicknameClick(it) },
                            text = it,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NodeListItemContent(
    item: NodeListModel,
    nodeClick: () -> Unit,
    nicknameClick: () -> Unit,
    contentClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.clickable { nodeClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            NodeCategoryIcon(item.fid)

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (item.today.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(5.dp))

                        Row(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color.Red),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = item.today,
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = item.count,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        val annotatedText = buildAnnotatedString {
            pushStringAnnotation(
                tag = "content",
                annotation = ""
            )
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.secondary
                )
            ) {
                append(item.content)
            }
            pop()

            append("  ")
            withStyle(
                style = SpanStyle(
                    color = ColorTheme(light = Color.Black, dark = Color.White)
                )
            ) {
                append(item.time)
            }
            append("  ")

            pushStringAnnotation(
                tag = "username",
                annotation = ""
            )
            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.secondary
                )
            ) {
                append(item.username)
            }
            pop()
        }

        ClickableText(
            text = annotatedText,
            onClick = { offset ->
                annotatedText.getStringAnnotations(
                    tag = "content",
                    start = offset,
                    end = offset
                ).firstOrNull()?.let {
                    contentClick()
                }
                annotatedText.getStringAnnotations(
                    tag = "username",
                    start = offset,
                    end = offset
                ).firstOrNull()?.let {
                    nicknameClick()
                }
            }
        )
    }

    Divider(modifier = Modifier)
}

@Composable
private fun NodeCategoryIcon(fid: String) = when (fid) {
    "2" -> Icon(
        painter = painterResource(id = R.drawable.ic_baseline_laptop_24),
        contentDescription = "黑板报",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "44" -> Icon(
        painter = painterResource(id = R.drawable.ic_baseline_key_24),
        contentDescription = "Tips",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "47" -> Icon(
        painter = painterResource(id = R.drawable.ic_baseline_android_24),
        contentDescription = "软件",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "93" -> Icon(
        painter = painterResource(id = R.drawable.ic_baseline_zoom_in_24),
        contentDescription = "Keyword",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "98" -> Icon(
        painter = painterResource(id = R.drawable.ic_baseline_web_24),
        contentDescription = "Wiki",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "112" -> Icon(
        painter = painterResource(id = R.drawable.ic_baseline_currency_exchange_24),
        contentDescription = "问答",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "113" -> Icon(
        painter = painterResource(id = R.drawable.ic_baseline_lightbulb_24),
        contentDescription = "方法论",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "114" -> Icon(
        painter = painterResource(id = R.drawable.ic_baseline_cloud_download_24),
        contentDescription = "资源",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "117" -> Icon(
        imageVector = Icons.Default.List,
        contentDescription = "题库",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "119" -> Icon(
        painter = painterResource(id = R.drawable.ic_baseline_visibility_24),
        contentDescription = "发现创造",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "120" -> Icon(
        painter = painterResource(id = R.drawable.ic_baseline_wb_sunny_24),
        contentDescription = "生活",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "121" -> Icon(
        painter = painterResource(id = R.drawable.ic_baseline_business_center_24),
        contentDescription = "职场",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "122" -> Icon(
        painter = painterResource(id = R.drawable.ic_baseline_light_24),
        contentDescription = "学途",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "123" -> Icon(
        painter = painterResource(id = R.drawable.ic_baseline_feedback_24),
        contentDescription = "反馈",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "125" -> Icon(
        painter = painterResource(id = R.drawable.ic_baseline_visibility_off_24),
        contentDescription = "石沉大海",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "126" -> Icon(
        painter = painterResource(id = R.drawable.ic_baseline_g_translate_24),
        contentDescription = "Google",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "127" -> Icon(
        painter = painterResource(id = R.drawable.ic_baseline_desktop_mac_24),
        contentDescription = "Apple",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "128" -> Icon(
        painter = painterResource(id = R.drawable.ic_baseline_sports_soccer_24),
        contentDescription = "探索杂谈",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    else -> Icon(
        painter = painterResource(id = R.drawable.ic_baseline_image_24),
        contentDescription = "默认",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
}