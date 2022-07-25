package com.eternaljust.msea.ui.page.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.eternaljust.msea.R
import com.eternaljust.msea.utils.RouteName
import kotlinx.coroutines.launch

@Composable
fun HomePage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController
) {
    val scope = rememberCoroutineScope()
    val text = LocalContext.current.resources.getString(R.string.bottom_navigation_home)

    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        Column() {
            Text(text)

            Button(onClick = {
                navController.navigate(RouteName.TOPIC_DETAIL)
                scope.launch {
                    scaffoldState.showSnackbar(message = text)
                }
            }) {
                Text(text)
            }
        }
    }
}