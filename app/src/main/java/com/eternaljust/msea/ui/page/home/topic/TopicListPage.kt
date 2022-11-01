@file:Suppress("IMPLICIT_CAST_TO_ANY")

package com.eternaljust.msea.ui.page.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.eternaljust.msea.R
import com.eternaljust.msea.ui.page.home.topic.TopicListModel
import com.eternaljust.msea.ui.page.home.topic.TopicListViewModel
import com.eternaljust.msea.ui.widget.RefreshList

@Composable
fun TopicListPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: TopicListViewModel = viewModel()
) {
    val viewStates = viewModel.viewStates
    val lazyPagingItems = viewStates.pagingData.collectAsLazyPagingItems()

    RefreshList(
        lazyPagingItems = lazyPagingItems
    ) {
        itemsIndexed(lazyPagingItems) { _, item ->
            TopicListItemContent(item!!)
        }
    }
}

@Composable
private fun TopicListItemContent(item: TopicListModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Row {
            AsyncImage(
                modifier = Modifier
                    .size(45.dp)
                    .clip(shape = RoundedCornerShape(5)),
                model = item.avatar,
                placeholder = painterResource(id = R.drawable.icon),
                contentDescription = null
            )
            
            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Row(
                    modifier = Modifier
                            .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

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

                Spacer(modifier = Modifier.height(5.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = item.time,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    if (item.icon1.isNotEmpty()) {
                        TopicAttachmentIcon(item.icon1)

                        Spacer(modifier = Modifier.width(5.dp))
                    }

                    if (item.icon2.isNotEmpty()) {
                        TopicAttachmentIcon(item.icon2)

                        Spacer(modifier = Modifier.width(5.dp))
                    }

                    if (item.icon3.isNotEmpty()) {
                        TopicAttachmentIcon(item.icon3)

                        Spacer(modifier = Modifier.width(5.dp))
                    }

                    if (item.icon4.isNotEmpty()) {
                        TopicAttachmentIcon(item.icon4)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            buildAnnotatedString {
                append(item.title)

                if (item.attachmentColorRed) {
                    withStyle(
                        style = SpanStyle(
                            color = Color.Red
                        )
                    ) {
                        append(item.attachment)
                    }
                } else {
                    append(item.attachment)
                }
            }
        )
    }

    Divider(modifier = Modifier)
}

@Composable
private fun TopicAttachmentIcon(icon: String) = when (icon) {
    "image" -> Icon(
        imageVector = Icons.Default.Image,
        contentDescription = "帖子包含图片",
        tint = MaterialTheme.colorScheme.primary
    )
    "fire" -> Icon(
        imageVector = Icons.Default.LocalFireDepartment,
        contentDescription = "帖子高热度",
        tint = Color.Red
    )
    "hand" -> Icon(
        imageVector = Icons.Default.WavingHand,
        contentDescription = "帖子评价指数",
        tint = MaterialTheme.colorScheme.secondary
    )
    "link" -> Icon(
        imageVector = Icons.Default.Link,
        contentDescription = "帖子包含链接",
        tint = Color.Blue
    )
    "premium" -> Icon(
        imageVector = Icons.Default.WorkspacePremium,
        contentDescription = "帖子精华",
        tint = Color.Yellow
    )
    else -> Color.Unspecified
}