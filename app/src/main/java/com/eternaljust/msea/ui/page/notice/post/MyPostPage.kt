package com.eternaljust.msea.ui.page.notice.post

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.paging.compose.itemsIndexed
import coil.compose.AsyncImage
import com.eternaljust.msea.R
import com.eternaljust.msea.ui.page.home.topic.TopicDetailRouteModel
import com.eternaljust.msea.ui.theme.colorTheme
import com.eternaljust.msea.ui.widget.RefreshList
import com.eternaljust.msea.utils.RouteName
import com.eternaljust.msea.utils.toJson

@Composable
fun MyPostPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: MyPostViewModel = viewModel()
) {
    val viewStates = viewModel.viewStates
    val lazyPagingItems = viewStates.pagingData.collectAsLazyPagingItems()

    RefreshList(
        lazyPagingItems = lazyPagingItems
    ) {
        itemsIndexed(lazyPagingItems) { _, item ->
            item?.let {
                MyPostListItemContent(
                    item = it,
                    avatarClick = {
                        navController.navigate(RouteName.PROFILE_DETAIL + "/${it.uid}")
                    },
                    contentClick = {
                        val topic = TopicDetailRouteModel(tid = it.ptid)
                        val args = String.format("/%s", Uri.encode(topic.toJson()))
                        navController.navigate(RouteName.TOPIC_DETAIL + args)
                    }
                )
            }
        }
    }
}

@Composable
fun MyPostListItemContent(
    item: PostListModel,
    avatarClick: () -> Unit,
    contentClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
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

        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = item.time,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Normal
            )

            val annotatedText = buildAnnotatedString {
                pushStringAnnotation(
                    tag = "avatar",
                    annotation = ""
                )
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary
                    )
                ) {
                    append(item.name)
                }
                pop()

                append("  ")
                withStyle(
                    style = SpanStyle(
                        color = colorTheme(light = Color.Black, dark = Color.White)
                    )
                ) {
                    append("回复了您的帖子")
                }
                append("  ")

                pushStringAnnotation(
                    tag = "content",
                    annotation = ""
                )
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary
                    )
                ) {
                    append(item.title)
                }
                pop()
            }
            ClickableText(
                text = annotatedText,
                onClick = { offset ->
                    annotatedText.getStringAnnotations(
                        tag = "avatar",
                        start = offset,
                        end = offset
                    ).firstOrNull()?.let {
                        avatarClick()
                    }

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
    }

    Divider(modifier = Modifier)
}