package com.eternaljust.msea.ui.page.home.topic

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.eternaljust.msea.ui.widget.WebViewModel
import com.eternaljust.msea.ui.widget.mseaTopAppBarColors
import com.eternaljust.msea.utils.openSystemBrowser
import com.google.accompanist.web.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicDetailPage(
    web: WebViewModel,
    scaffoldState: SnackbarHostState,
    navController: NavHostController
) {
    val context = LocalContext.current
    val state = rememberWebViewState(web.url)
    val navigator = rememberWebViewNavigator()
    val title = if(state.pageTitle != null) state.pageTitle else web.title

    Scaffold(
        topBar = {
            TopAppBar(
                title = { title?.let { Text(text = it, maxLines = 2) } },
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
                            state.content.getCurrentUrl()?.let {
                                openSystemBrowser(
                                    url = it,
                                    context = context
                                )
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = com.eternaljust.msea.R.drawable.ic_baseline_public_24),
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
            ) {
                WebView(
                    state = state,
                    navigator = navigator,
                    onCreated = { webView ->
                        webView.settings.javaScriptEnabled = true
                        webView.settings.domStorageEnabled = true
                        webView.settings.blockNetworkImage = false
                        webView.settings.databaseEnabled = true
                    }
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