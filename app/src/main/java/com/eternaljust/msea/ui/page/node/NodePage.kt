package com.eternaljust.msea.ui.page.node

import android.graphics.drawable.Icon
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.eternaljust.msea.ui.widget.RefreshList

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
                stickyHeader {
                    NodeListHeader(it!!)
                }

                items(it!!.list) { item ->
                    NodeListItemContent(item)
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
fun NodeListItemContent(item: NodeListModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Row {
            NodeCategoryIcon(item.fid)

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = item.count,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    append(item.content)
                }

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
        imageVector = Icons.Default.HourglassTop,
        contentDescription = "Keyword",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "98" -> Icon(
        imageVector = Icons.Default.Work,
        contentDescription = "Wiki",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "112" -> Icon(
        imageVector = Icons.Default.Analytics,
        contentDescription = "问答",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "113" -> Icon(
        imageVector = Icons.Default.Light,
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
        imageVector = Icons.Default.PanoramaFishEye,
        contentDescription = "发现创造",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "120" -> Icon(
        imageVector = Icons.Default.WbSunny,
        contentDescription = "生活",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "121" -> Icon(
        imageVector = Icons.Default.Build,
        contentDescription = "职场",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "122" -> Icon(
        imageVector = Icons.Default.Highlight,
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
        imageVector = Icons.Default.RemoveRedEye,
        contentDescription = "石沉大海",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "126" -> Icon(
        imageVector = Icons.Default.Gesture,
        contentDescription = "Google",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "127" -> Icon(
        imageVector = Icons.Default.Apps,
        contentDescription = "Apple",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    "128" -> Icon(
        imageVector = Icons.Default.Psychology,
        contentDescription = "探索杂谈",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
    else -> Icon(
        imageVector = Icons.Default.Image,
        contentDescription = "帖子包含图片",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(40.dp)
    )
}