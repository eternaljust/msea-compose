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
import androidx.compose.material.icons.sharp.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.eternaljust.msea.ui.widget.RefreshList
import com.eternaljust.msea.ui.widget.WebViewModel
import com.eternaljust.msea.utils.HTMLURL
import com.eternaljust.msea.utils.RouteName
import com.eternaljust.msea.utils.toJson

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NodePage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: NodeViewModel = viewModel()
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
                        NodeListHeader(it)
                    }

                    items(it.list) { item ->
                        NodeListItemContent(
                            item = item,
                            nodeClick = {
                                navController.navigate(RouteName.NODE_LIST + "/${item.fid}")
                            },
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
}

@Composable
fun NodeListHeader(item: NodeModel) {
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
                            modifier = Modifier.padding(horizontal = 5.dp),
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
            append(item.time)
            append("  ")

            withStyle(
                style = SpanStyle(
                    color = MaterialTheme.colorScheme.secondary
                )
            ) {
                append(item.username)
            }
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
            }
        )
    }

    Divider(modifier = Modifier)
}

@Composable
private fun NodeCategoryIcon(fid: String) = when (fid) {
    "2" -> Icon(
        imageVector = Icons.Default.Laptop,
        contentDescription = "黑板报",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "44" -> Icon(
        imageVector = Icons.Default.Key,
        contentDescription = "Tips",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "47" -> Icon(
        imageVector = Icons.Default.Android,
        contentDescription = "软件",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "93" -> Icon(
        imageVector = Icons.Default.ZoomIn,
        contentDescription = "Keyword",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "98" -> Icon(
        imageVector = Icons.Default.Web,
        contentDescription = "Wiki",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "112" -> Icon(
        imageVector = Icons.Default.CurrencyExchange,
        contentDescription = "问答",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "113" -> Icon(
        imageVector = Icons.Default.Lightbulb,
        contentDescription = "方法论",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "114" -> Icon(
        imageVector = Icons.Default.CloudDownload,
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
        imageVector = Icons.Default.Visibility,
        contentDescription = "发现创造",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "120" -> Icon(
        imageVector = Icons.Sharp.WbSunny,
        contentDescription = "生活",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "121" -> Icon(
        imageVector = Icons.Default.BusinessCenter,
        contentDescription = "职场",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "122" -> Icon(
        imageVector = Icons.Default.Light,
        contentDescription = "学途",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "123" -> Icon(
        imageVector = Icons.Default.Feedback,
        contentDescription = "反馈",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "125" -> Icon(
        imageVector = Icons.Default.VisibilityOff,
        contentDescription = "石沉大海",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "126" -> Icon(
        imageVector = Icons.Default.GTranslate,
        contentDescription = "Google",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "127" -> Icon(
        imageVector = Icons.Default.DesktopMac,
        contentDescription = "Apple",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "128" -> Icon(
        imageVector = Icons.Default.SportsSoccer,
        contentDescription = "探索杂谈",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    else -> Icon(
        imageVector = Icons.Default.Image,
        contentDescription = "默认",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
}