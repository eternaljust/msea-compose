package com.eternaljust.msea.ui.page.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.eternaljust.msea.R
import com.eternaljust.msea.ui.page.home.sign.SignDayListModel
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
            .padding(horizontal = 13.dp, vertical = 5.dp)
    ) {
        Row {
            AsyncImage(
                modifier = Modifier
                    .size(40.dp)
                    .clip(shape = RoundedCornerShape(5)),
                model = item.avatar,
                placeholder = painterResource(id = R.drawable.icon),
                contentDescription = null
            )

            Column {
                Text(
                    text = item.name
                )

                Text(
                    text = item.time
                )
            }

            Text(
                modifier = Modifier
                    .background(Color.Cyan),
                text = "${item.reply}/${item.examine}",
            )
        }

        Text(
            text = item.title
        )
    }

    Divider(modifier = Modifier.padding(horizontal = 13.dp))
}