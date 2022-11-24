package com.eternaljust.msea.ui.widget

import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BrowseGallery
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.web.LoadingState
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState
import kotlinx.android.parcel.Parcelize


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewPage(
    web: WebViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    val state = rememberWebViewState(web.url)
    val title = if(state.pageTitle != null) state.pageTitle else web.title

    Scaffold(
        topBar = {
            TopAppBar(
                title = { title?.let { Text(text = it) }},
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val uri: Uri = Uri.parse(web.url)
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "浏览器"
                        )
                    }
                },
                colors = mseaTopAppBarColors()
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxWidth()
            ) {
                WebView(
                    state = state
                )

                val loading = state.loadingState
                when (loading) {
                    is LoadingState.Loading -> {
                        println("loading.progress---${loading.progress}")
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            progress = loading.progress,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    else -> {}
                }
            }
        }
    )
}

@Parcelize
data class WebViewModel(
    var url: String = "",
    var title: String = ""
) : Parcelable