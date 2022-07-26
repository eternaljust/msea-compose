package com.eternaljust.msea.ui.page.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.eternaljust.msea.utils.RouteName
import kotlinx.coroutines.launch

@Composable
fun TopicListPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    tabItem: TopicTabItem
) {
    val scope = rememberCoroutineScope()
    val text = "帖子列表 ${tabItem.title}"

    Surface(
        modifier = Modifier
            .padding().fillMaxSize()
    ) {
        Column() {
            Text(text)

            Button(onClick = {
                navController.navigate(route = RouteName.TOPIC_DETAIL)
                scope.launch {
                    scaffoldState.showSnackbar(message = "帖子详情")
                }
            }) {
                Text("帖子详情")
            }
        }
    }
}