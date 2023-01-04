package com.eternaljust.msea.ui.widget

import android.annotation.SuppressLint
import android.os.Parcelable
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
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
import com.eternaljust.msea.utils.HTMLURL
import com.eternaljust.msea.utils.openSystemBrowser
import com.google.accompanist.web.*
import kotlinx.parcelize.Parcelize

@SuppressLint("SetJavaScriptEnabled")
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
    color: aColor;
    text-decoration: none;
    word-break: break-all;
    -webkit-touch-callout: none;
    -webkit-user-select: none;
}

img {
    max-width: 100%;
}

div.quote {
    width: 100%;
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

td {
    font-size: tdFontSize;
    line-height: tdLineHeight;
}

    """

const val lightStyle =
    """
body {
    background-color: bodyBackgroundColor;
}

div.quote, div.rsld {
    background-color: #f9f9f9;
}

td {
    color: #000;
}
    """

const val darkStyle =
    """
body {
    background-color: #1c1c1e;
}

div.quote, div.rsld {
    background-color: #2c2c2e;
}

td {
    color: #fff;
}

blockquote {
    color: #ffffff;
}
    """

const val grayStyle = "html {-webkit-filter: grayscale(100%);}"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebHTML(
    modifier: Modifier = Modifier,
    html: String,
    isNodeFid125Gray: Boolean = false,
    userOrTopicClick: (String) -> Unit
) {
    var webView by remember { mutableStateOf<WebView?>(null) }
    val runningInPreview = LocalInspectionMode.current
    val gray = if(isNodeFid125Gray) grayStyle else ""
    val themeStyle = if(isSystemInDarkTheme()) darkStyle else lightStyle
    val fontPx = MaterialTheme.typography.bodyLarge.fontSize.value.toInt()
    val lineHeight = MaterialTheme.typography.bodyLarge.lineHeight.value.toInt()
    val color = MaterialTheme.colorScheme.primary.value
    val hex = java.lang.Long.toHexString(color.toLong())
    val aColor = hex.substring(0, 8).removePrefix("ff")
    val background = MaterialTheme.colorScheme.background.value
    val backgroundHex = java.lang.Long.toHexString(background.toLong())
    val backgroundColor = backgroundHex.substring(0, 8).removePrefix("ff")
    var htmlStyle = (cssStyle + themeStyle).replace("tdFontSize","${fontPx}px")
    htmlStyle.replace("tdLineHeight", "${lineHeight}px").also { htmlStyle = it }
    htmlStyle.replace("aColor", "#$aColor").also { htmlStyle = it }
    htmlStyle.replace("bodyBackgroundColor", "#$backgroundColor").also { htmlStyle = it }
    val style = htmlStyle + gray
    val head = "<head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\"><style>$style</style></head>"
    val body = "<body><div id=\"Wrapper\"<table><tbody><tr>$html</tr></tbody></table></div></body>"
    val fullHtml = "<html>$head$body</html>"
    println("fullHtml---$fullHtml")
    val context = LocalContext.current

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

            view.loadDataWithBaseURL(null, fullHtml, "text/html", "utf-8",null)

            view.webViewClient = object : WebViewClient() {
                @Deprecated("Deprecated in Java")
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    url: String?
                ): Boolean {
                    url?.let {
                        println("shouldOverrideUrlLoading---${it}")
                        if (it.contains(HTMLURL.BASE)) {
                            // 用户、帖子
                            if (it.contains("mod=space&uid=") ||
                                it.contains("mod=viewthread&tid=") ||
                                it.contains("space-uid-") ||
                                it.contains("/thread-")) {
                                userOrTopicClick(it)
                            } else {
                                openSystemBrowser(it, context)
                            }
                        } else {
                            openSystemBrowser(it, context)
                        }
                    }
                    return true
                }
            }
        }
    }
}