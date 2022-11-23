package com.eternaljust.msea.ui.page.profile

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Favorite
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
                        headlineText = { Text(text = it.title) },
                        trailingContent = { ListArrowForward() }
                    )

                    Divider()
                }
            }
        }
    )
}
// 使用SDK名称：友盟+SDK
//服务类型：请根据SDK提供功能填写
//收集个人信息类型：设备信息（IMEI/MAC/Android ID/IDFA/OpenUDID/GUID/SIM卡IMSI/ICCID/地理位置等）
//隐私权政策链接：https://www.umeng.com/page/policy