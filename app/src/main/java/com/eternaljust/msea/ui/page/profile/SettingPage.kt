package com.eternaljust.msea.ui.page.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@Composable
fun SettingPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController
) {
    val scope = rememberCoroutineScope()
    val text = "设置"

    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        Column {
            Text(text = text)

            Button(onClick = {
                scope.launch {
                    scaffoldState.showSnackbar(message = text)
                }
            }) {
                Text(text)
            }
        }
    }
}