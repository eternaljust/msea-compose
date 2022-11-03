package com.eternaljust.msea.ui.page.node.tag

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.eternaljust.msea.ui.theme.ColorTheme
import com.eternaljust.msea.ui.widget.mseaTopAppBarColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: TagViewModel = viewModel()
) {
    viewModel.dispatch(TagViewAction.GetTagList)
    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect {
            when (it) {
                is TagViewEvent.PopBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("标签") },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.dispatch(TagViewAction.PopBack) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = mseaTopAppBarColors()
            )
        },
        content = { paddingValues ->
            Surface(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                LazyVerticalGrid(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    columns = GridCells.Fixed(count = 3),
                    content = {
                        items(viewModel.viewStates.list) {
                            Column(
                                modifier = Modifier
                                    .clip(shape = RoundedCornerShape(50.dp))
                                    .background(MaterialTheme.colorScheme.secondary),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    modifier = Modifier
                                        .padding(vertical = 8.dp),
                                    text = it.title,
                                    color = Color.White
                                )
                            }
                        }
                    })
            }
        }
    )
}