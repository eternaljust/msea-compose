package com.eternaljust.msea.ui.page.profile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.eternaljust.msea.ui.widget.mseaTopAppBarColors
import kotlinx.coroutines.launch


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

    val scope = rememberCoroutineScope()
    val text = "关于"

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
            Surface(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                LazyColumn {
                    stickyHeader {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
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
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = it.title)

                            ListArrowForward()
                        }

                        Divider()
                    }
                }
            }
        }
    )
}