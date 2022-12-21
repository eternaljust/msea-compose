@file:Suppress("IMPLICIT_CAST_TO_ANY")

package com.eternaljust.msea.ui.page.home.topic

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
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
import com.eternaljust.msea.ui.widget.RefreshList
import com.eternaljust.msea.ui.widget.WebViewModel
import com.eternaljust.msea.utils.HTMLURL
import com.eternaljust.msea.utils.RouteName
import com.eternaljust.msea.utils.toJson

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
            item?.let {
                TopicListItemContent(
                    item = item,
                    avatarClick = {
                        navController.navigate(RouteName.PROFILE_DETAIL + "/${item.uid}")
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

@Composable
fun TopicListItemContent(
    item: TopicListModel,
    avatarClick: () -> Unit,
    contentClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Row {
            AsyncImage(
                modifier = Modifier
                    .size(45.dp)
                    .clip(shape = RoundedCornerShape(5))
                    .clickable { avatarClick() },
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
                        modifier = Modifier.clickable { avatarClick() },
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

        val annotatedText = buildAnnotatedString {
            pushStringAnnotation(
                tag = "content",
                annotation = ""
            )

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
            }
        )
    }

    Divider(modifier = Modifier)
}

@Composable
fun TopicAttachmentIcon(icon: String) = when (icon) {
    "image" -> Icon(
        painter = painterResource(id = R.drawable.ic_baseline_image_24),
        contentDescription = "帖子包含图片",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(20.dp)
    )
    "fire" -> Icon(
        painter = painterResource(id = R.drawable.ic_baseline_local_fire_department_24),
        contentDescription = "帖子高热度",
        tint = Color.Red,
        modifier = Modifier.size(20.dp)
    )
    "hand" -> Icon(
        painter = painterResource(id = R.drawable.ic_baseline_waving_hand_24),
        contentDescription = "帖子评价指数",
        tint = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.size(20.dp)
    )
    "link" -> Icon(
        painter = painterResource(id = R.drawable.ic_baseline_link_24),
        contentDescription = "帖子包含链接",
        tint = Color.Blue,
        modifier = Modifier.size(20.dp)
    )
    "premium" -> Icon(
        painter = painterResource(id = R.drawable.ic_baseline_workspace_premium_24),
        contentDescription = "帖子精华",
        tint = Color.Yellow,
        modifier = Modifier.size(20.dp)
    )
    else -> Color.Unspecified
}