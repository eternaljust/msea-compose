package com.eternaljust.msea.ui.page.notice.system

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Feed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.eternaljust.msea.ui.widget.RefreshList

@Composable
fun SystemPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: SystemViewModel = viewModel()
) {
    val viewStates = viewModel.viewStates
    val lazyPagingItems = viewStates.pagingData.collectAsLazyPagingItems()

    RefreshList(
        lazyPagingItems = lazyPagingItems
    ) {
        itemsIndexed(lazyPagingItems) { _, item ->
            SystemListItemContent(item!!)
        }
    }
}

@Composable
fun SystemListItemContent(item: SystemListModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Feed,
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

            Text(
                text = item.content
            )
        }
    }

    Divider(modifier = Modifier)
}