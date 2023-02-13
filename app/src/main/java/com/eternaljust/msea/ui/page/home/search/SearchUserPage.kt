package com.eternaljust.msea.ui.page.home.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.eternaljust.msea.R
import com.eternaljust.msea.ui.widget.ListArrowForward
import com.eternaljust.msea.utils.RouteName
import com.eternaljust.msea.utils.UserInfo

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchUserPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    keyword: String,
    viewModel: SearchUserViewModel = viewModel()
) {
    println("---user---$keyword")
    if (UserInfo.instance.auth.isNotEmpty()) {
        viewModel.dispatch(SearchUserAction.SearchKeyword(keyword))
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (viewModel.viewStates.list.isEmpty()) {
            if (keyword.isNotEmpty()) {
                Text("没有找到\"${keyword}\"相关用户")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                stickyHeader {
                    SearchListHeader(
                        count = "以下是查找到的用户列表(${viewModel.viewStates.list.count()})个"
                    )
                }

                items(viewModel.viewStates.list) {
                    SearchUserListItemContent(
                        item = it,
                        contentClick = {
                            navController.navigate(RouteName.PROFILE_DETAIL + "/${it.uid}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchListHeader(
    count: String
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = count)
        }
    }
}

@Composable
fun SearchUserListItemContent(
    item: UserListModel,
    contentClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable { contentClick() }
    ) {
        AsyncImage(
            modifier = Modifier
                .size(45.dp)
                .clip(shape = RoundedCornerShape(5)),
            model = item.avatar,
            placeholder = painterResource(id = R.drawable.icon),
            contentDescription = null,
        )

        Spacer(modifier = Modifier.width(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = item.content
                )
            }

            ListArrowForward()
        }
    }

    Divider(modifier = Modifier)
}