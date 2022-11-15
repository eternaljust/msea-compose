package com.eternaljust.msea.ui.page.profile.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.eternaljust.msea.ui.widget.RefreshList
import com.eternaljust.msea.utils.UserInfo

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CreditLogPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: CreditLogViewModel = viewModel()
) {
    val viewStates = viewModel.viewStates
    val lazyPagingItems = viewStates.pagingData.collectAsLazyPagingItems()

    RefreshList(
        lazyPagingItems = lazyPagingItems
    ) {
        stickyHeader {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row {
                    Text(
                        text = "Bit: ${UserInfo.instance.bits}  ",
                        color = Color.Red
                    )

                    Text(text = "违规: ${UserInfo.instance.violation}")
                }

                Text(
                    text = "积分: ${UserInfo.instance.integral} " +
                            "( 总积分=发帖数 X 0.2 + 精华帖数 X 5 + Bit X 1.5 - 违规 X 10 )"
                )
            }
        }

        stickyHeader {
            CreditLogListHeader()
        }

        itemsIndexed(lazyPagingItems) { _, item ->
            item?.let {
                CreditLogListItemContent(it)
            }
        }
    }
}

@Composable
fun CreditLogListHeader() {
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
            Text(text = "操作")

            Text(text = "Bit")

            Text(text = "详情")

            Text(text = "变更时间")
        }
    }
}

@Composable
fun CreditLogListItemContent(item: CreditLogListModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = item.action)

        Text(
            text = item.bit,
            color = if (item.isAdd) Color.Red else Color.Gray
        )

        Text(
            modifier = Modifier.widthIn(min = 100.dp, max= 150.dp),
            text = item.content
        )

        Text(
            text = item.time,
            fontWeight = FontWeight.Thin,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }

    Divider(modifier = Modifier)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CreditSystemPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: CreditSystemViewModel = viewModel()
) {
    val viewStates = viewModel.viewStates
    val lazyPagingItems = viewStates.pagingData.collectAsLazyPagingItems()

    RefreshList(
        lazyPagingItems = lazyPagingItems
    ) {
        stickyHeader {
            CreditSystemListHeader()
        }

        itemsIndexed(lazyPagingItems) { _, item ->
            item?.let {
                CreditSystemListItemContent(it)
            }
        }
    }
}

@Composable
fun CreditSystemListHeader() {
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
            Text(text = "动作名称")

            Text(text = "总次数")

            Text(text = "周期次数")

            Text(text = "Bit")

            Text(text = "违规")

            Text(text = "最后奖励")
        }
    }
}

@Composable
fun CreditSystemListItemContent(item: CreditSystemListModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = item.action)

        Text(text = item.count)

        Text(text = item.cycles)

        Text(text = item.bit)

        Text(text = item.violation)

        Text(
            text = item.time,
            fontWeight = FontWeight.Thin,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }

    Divider(modifier = Modifier)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CreditRulePage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: CreditRuleViewModel = viewModel()
) {
    val viewStates = viewModel.viewStates
    val lazyPagingItems = viewStates.pagingData.collectAsLazyPagingItems()

    RefreshList(
        lazyPagingItems = lazyPagingItems
    ) {
        stickyHeader {
            Text(text = "进行以下事件动作，会得到积分奖励。不过，在一个周期内，您最多得到的奖励次数有限制")
        }

        stickyHeader {
            CreditRuleListHeader()
        }

        itemsIndexed(lazyPagingItems) { _, item ->
            item?.let {
                CreditRuleListItemContent(it)
            }
        }
    }
}
@Composable
fun CreditRuleListHeader() {
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
            Text(text = "动作名称")

            Text(text = "周期范围")

            Text(text = "周期内最多奖励次数")

            Text(text = "Bit")

            Text(text = "违规")
        }
    }
}

@Composable
fun CreditRuleListItemContent(item: CreditRuleListModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = item.action)

        Text(text = item.cycles)

        Text(text = item.count)

        Text(text = item.bit)

        Text(text = item.violation)
    }

    Divider(modifier = Modifier)
}