package com.eternaljust.msea.ui.page.profile

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.eternaljust.msea.BuildConfig
import com.eternaljust.msea.R
import com.eternaljust.msea.ui.page.profile.setting.AboutViewAction
import com.eternaljust.msea.ui.page.profile.setting.AboutViewEvent
import com.eternaljust.msea.ui.page.profile.setting.AboutViewModel
import com.eternaljust.msea.ui.widget.ListArrowForward
import com.eternaljust.msea.ui.widget.WebViewModel
import com.eternaljust.msea.ui.widget.mseaTopAppBarColors
import com.eternaljust.msea.utils.RouteName
import com.eternaljust.msea.utils.toJson
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AboutPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: AboutViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect {
            when (it) {
                is AboutViewEvent.PopBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("关于 Msea") },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.dispatch(AboutViewAction.PopBack) }
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
            LazyColumn(contentPadding = paddingValues) {
                stickyHeader {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Image(
                            modifier = Modifier
                                .clip(shape = RoundedCornerShape(6)),
                            painter = painterResource(id = R.drawable.icon),
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        val versionName = BuildConfig.VERSION_NAME
                        val versionCode = BuildConfig.VERSION_CODE

                        Text(text = "$versionName ($versionCode)")
                    }
                }

                items(viewModel.items) {
                    ListItem(
                        modifier = Modifier
                            .clickable {
                                if (it.route == RouteName.SOURCE_CODE) {
                                    val url = "https://github.com/eternaljust/msea-compose"
                                    val web = WebViewModel(url = url)
                                    val args = String.format("/%s", Uri.encode(web.toJson()))
                                    navController.navigate(RouteName.WEBVIEW + args)
                                }
                            },
                        headlineText = { Text(text = it.title) },
                        trailingContent = { ListArrowForward() }
                    )

                    Divider()
                }
            }
        }
    )
}