package com.eternaljust.msea.ui.page.home.sign

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.eternaljust.msea.ui.widget.RefreshList

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SignListPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: SignListViewModel = viewModel()
) {
    val viewStates = viewModel.viewStates
    val lazyPagingItems = viewStates.pagingData.collectAsLazyPagingItems()

    RefreshList(
        lazyPagingItems = lazyPagingItems,
        noMoreDataText = "没有更多人签到哦！"
    ) {
        stickyHeader {
            SignListHeader()
        }

        itemsIndexed(lazyPagingItems) { _, item ->
            SignListItemContent(item!!)
        }
    }
}

@Composable
private fun SignListHeader () {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "排名")

            Text(text = "昵称")

            Text(text = "签到内容")

            Text(text = "奖励")

            Text(text = "签到时间")
        }
    }
}

@Composable
private fun SignListItemContent(item: SignListModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 13.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = item.no)

        Text(
            modifier = Modifier
                .width(60.dp),
            text = item.name
        )

        Text(
            modifier = Modifier
                .width(80.dp),
            text = item.content
        )

        Text(
            text = item.bits,
            color = MaterialTheme.colorScheme.primary
        )

        Text(text = item.time)
    }

    Divider(modifier = Modifier.padding(horizontal = 13.dp))
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SignDayListPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: SignDayListViewModel
) {
    val viewStates = viewModel.viewStates
    val lazyPagingItems = viewStates.pagingData.collectAsLazyPagingItems()

    RefreshList(
        lazyPagingItems = lazyPagingItems,
        noMoreDataText = "没有更多人签到哦！"
    ) {
        stickyHeader {
            SignDayListHeader()
        }

        itemsIndexed(lazyPagingItems) { _, item ->
            SignDayListItemContent(item!!)
        }
    }
}

@Composable
private fun SignDayListHeader () {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "排名")

            Text(text = "昵称")

            Text(text = "连续")

            Text(text = "本月")

            Text(text = "总")

            Text(text = "总奖励")

            Text(text = "上次签到")
        }
    }
}

@Composable
private fun SignDayListItemContent(item: SignDayListModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 13.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = item.no)

        Text(
            modifier = Modifier
                .width(80.dp),
            text = item.name,
            textAlign = TextAlign.Center
        )

        Text(text = item.continuous)

        Text(text = item.month)

        Text(text = item.total)

        Text(
            text = item.bits,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = item.time,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Thin,
            maxLines = 2
        )
    }

    Divider(modifier = Modifier.padding(horizontal = 13.dp))
}