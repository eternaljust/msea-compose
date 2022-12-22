package com.eternaljust.msea.ui.page.profile.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import coil.compose.AsyncImage
import com.eternaljust.msea.R
import com.eternaljust.msea.ui.widget.AutosizeText
import com.eternaljust.msea.ui.widget.NormalTopAppBar
import com.eternaljust.msea.ui.widget.RefreshGrid
import com.eternaljust.msea.ui.widget.RefreshList
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun ProfileDetailPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    uid: String,
    viewModel: ProfileDetailViewModel = viewModel()
) {
    viewModel.dispatch(ProfileDetailViewAction.SetUid(uid = uid))
    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect {
            when (it) {
                is ProfileDetailViewEvent.PopBack -> {
                    navController.popBackStack()
                }
                is ProfileDetailViewEvent.Message -> {
                    viewModel.viewModelScope.launch {
                        scaffoldState.showSnackbar(message = it.message)
                    }
                }
            }
        }
    }

    val pagerState = rememberPagerState()

    Scaffold(
        topBar = {
            NormalTopAppBar(
                title = "个人空间",
                onClick = { viewModel.dispatch(ProfileDetailViewAction.PopBack) }
            )
        },
        content = { paddingValues ->
            Surface(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ProfileDetailHeader(profile = viewModel.viewStates.profile)

                    TabRow(selectedTabIndex = pagerState.currentPage) {
                        viewModel.profileItems.forEachIndexed { index, item ->
                            Tab(
                                text = { AutosizeText(text = item.title) },
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    viewModel.viewModelScope.launch {
                                        pagerState.scrollToPage(index)
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    HorizontalPager(count = viewModel.profileItems.size, state = pagerState) {
                        val item = viewModel.profileItems[pagerState.currentPage]
                        if (item == ProfileDetailTabItem.TOPIC) {
                            ProfileTopicPage(
                                scaffoldState = scaffoldState,
                                navController = navController,
                                uid = uid,
                                showTopBar = false
                            )
                        } else {
                            ProfileDetailFriendPage(
                                scaffoldState = scaffoldState,
                                navController = navController,
                                uid = uid
                            )
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun ProfileDetailHeader(
    profile: ProfileDetailModel
) {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(10.dp))

        AsyncImage(
            modifier = Modifier
                .size(80.dp)
                .clip(shape = RoundedCornerShape(6)),
            model = profile.avatar,
            placeholder = painterResource(id = R.drawable.icon),
            contentDescription = null
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            modifier = Modifier,
            text = "${profile.name} uid(${profile.uid})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(5.dp))

        val level = if (profile.level.contains("(")) {
            profile.level
        } else {
            "用户组(${profile.level})"
        }
        Text(
            modifier = Modifier,
            text = level,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(5.dp))

        Row(
            modifier = Modifier
        ) {
            Text(
                text = "好友: "
            )

            Text(
                text = profile.friend,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = " 回帖: "
            )

            Text(
                text = profile.reply,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = " 主题: "
            )

            Text(
                text = profile.topic,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Row(
            modifier = Modifier
        ) {
            Text(
                text = "积分: "
            )

            Text(
                text = profile.integral,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = " Bit: "
            )

            Text(
                text = profile.bits,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = " 违规: "
            )

            Text(
                text = profile.violation,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ProfileDetailFriendPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    uid: String,
    viewModel: ProfileDetailFriendViewModel = viewModel()
) {
    viewModel.dispatch(ProfileDetailFriendViewAction.SetUid(uid = uid))
    val viewStates = viewModel.viewStates
    val lazyPagingItems = viewStates.pagingData.collectAsLazyPagingItems()

    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        RefreshGrid(
            lazyPagingItems = lazyPagingItems
        ) {
            items(lazyPagingItems.itemCount) { index ->
                val item = lazyPagingItems[index]
                item?.let {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = item.content,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }
}