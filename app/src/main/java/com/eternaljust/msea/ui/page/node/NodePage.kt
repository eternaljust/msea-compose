package com.eternaljust.msea.ui.page.node

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
import com.eternaljust.msea.R
import kotlinx.coroutines.launch

@Composable
fun NodePage(
    scaffoldState: SnackbarHostState
)  {
    val scope = rememberCoroutineScope()
    val text = LocalContext.current.resources.getString(R.string.bottom_navigation_node)

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