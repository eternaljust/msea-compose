package com.eternaljust.msea.ui.widget

import android.os.Parcelable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.eternaljust.msea.utils.openSystemBrowser
import com.google.accompanist.web.LoadingState
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewNavigator
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
    val navigator = rememberWebViewNavigator()
    val title = if(state.pageTitle != null) state.pageTitle else web.title

    Scaffold(
        topBar = {
            TopAppBar(
                title = { title?.let { Text(text = it) }},
                navigationIcon = {
                    Row {
                        IconButton(
                            onClick = {
                                if (navigator.canGoBack) {
                                    navigator.navigateBack()
                                } else {
                                    navController.popBackStack()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "返回"
                            )
                        }

                        if (navigator.canGoBack) {
                            IconButton(
                                onClick = { navController.popBackStack() }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "关闭网页"
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            openSystemBrowser(
                                url = web.url,
                                context = context
                            )
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
                    state = state,
                    navigator = navigator
                )

                when (val loading = state.loadingState) {
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