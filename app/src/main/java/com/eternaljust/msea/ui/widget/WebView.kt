package com.eternaljust.msea.ui.widget

import androidx.compose.runtime.Composable
import android.os.Parcelable
import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.eternaljust.msea.R
import com.eternaljust.msea.utils.openSystemBrowser
import com.google.accompanist.web.*
import com.google.accompanist.web.WebView
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
                            painter = painterResource(id = R.drawable.ic_baseline_public_24),
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

@Parcelize
data class WebViewModel(
    var url: String = "",
    var title: String = "",
    var tid: String = ""
) : Parcelable

const val cssStyle =
    """
a:link, a:visited, a:hover, a:active {
    color: #006e26;
    text-decoration: none;
    word-break: break-all;
    -webkit-touch-callout: none;
    -webkit-user-select: none;
}

img {
    max-width: 100%;
}

td {
    font-size: tdFontSize;
    line-height: tdLineHeight;
}

body {
    margin: 0px;
}

div.quote {
    width: 100%
    border-radius: 3px;
}

div.rsld {
    height: 55px;
    color: #666;
    background: #f6f6f6;
    border-radius: 3px;
    padding: 5px;
}

div.rsld img {
    width: 30px;
    height: 30px;
    border-radius: 5px;
}
    """

const val lightStyle =
    """
td {
    color: #000;
}

body {
    background-color: #fff;
}

div.quote, div.rsld {
    background-color: #f9f9f9;
}
    """

const val darkStyle =
    """
td {
    color: #fff;
}

body {
    background-color: #000;
}

div.quote, div.rsld {
    background-color: #1c1c1e;
}
    """

const val grayStyle = "html {-webkit-filter: grayscale(100%);}"

@Composable
fun WebHTML(
    modifier: Modifier = Modifier,
    html: String,
    isNodeFid125Gray: Boolean = false
) {
    var webView by remember { mutableStateOf<WebView?>(null) }
    val runningInPreview = LocalInspectionMode.current
    val gray = if(isNodeFid125Gray) grayStyle else ""
    val themeStyle = if(isSystemInDarkTheme()) darkStyle else lightStyle
    val fontPx = MaterialTheme.typography.bodyLarge.fontSize.value
    val lineHeight = MaterialTheme.typography.bodyLarge.lineHeight.value
    var htmlStyle = cssStyle.replace("tdFontSize","${fontPx}px")
    htmlStyle = htmlStyle.replace("tdLineHeight", "${lineHeight}px")

    BoxWithConstraints(modifier) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {

                    val width =
                        if (constraints.hasFixedWidth)
                            ViewGroup.LayoutParams.MATCH_PARENT
                        else
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    val height =
                        if (constraints.hasFixedHeight)
                            ViewGroup.LayoutParams.MATCH_PARENT
                        else
                            ViewGroup.LayoutParams.WRAP_CONTENT

                    layoutParams = ViewGroup.LayoutParams(
                        width,
                        height
                    )
                }.also { webView = it }
            }
        ) { view ->
            view.settings.javaScriptEnabled = true
            view.settings.domStorageEnabled = true
            view.settings.blockNetworkImage = false
            view.settings.databaseEnabled = true
            // AndroidViews are not supported by preview, bail early
            if (runningInPreview) return@AndroidView

            val head = "<head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\"><style>${htmlStyle + themeStyle + gray}</style></head>"
            val body = "<body><div id=\"Wrapper\">$html</div></body>"
            val html = "<html>$head$body</html>"
            view.loadDataWithBaseURL(null, html, "text/html", "utf-8",null)
        }
    }
}