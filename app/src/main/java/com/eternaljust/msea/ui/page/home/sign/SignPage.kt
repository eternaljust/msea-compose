package com.eternaljust.msea.ui.page.home.sign

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.EnergySavingsLeaf
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.Help
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eternaljust.msea.ui.widget.mseaSmallTopAppBarColors
import com.eternaljust.msea.utils.RouteName
import kotlinx.coroutines.launch
import okhttp3.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: SignViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()

    viewModel.dispatch(SignViewAction.GetDaySign)
    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect {
            when (it) {
                is SignViewEvent.PopBack -> {
                    navController.popBackStack()
                }
                is SignViewEvent.Login -> {
                    navController.navigate(route = RouteName.LOGIN)
                }
                is SignViewEvent.Message -> {
                    scope.launch {
                        scaffoldState.showSnackbar(message = it.message)
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("签到") },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.dispatch(SignViewAction.PopBack) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                colors = mseaSmallTopAppBarColors()
            )
        },
        content = { paddingValues ->
            Surface(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                signHeader(
                    daySign = viewModel.viewStates.daySign,
                    signClick = { viewModel.dispatch(SignViewAction.Sign)}
                )
            }
        }
    )
}

@Composable
private fun signHeader(
    daySign: DaySignModel,
    signClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .height(180.dp)
            .fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            val color = if (daySign.isSign) Color.Gray
            else MaterialTheme.colorScheme.secondary
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = color
                ),
                enabled = !daySign.isSign,
                onClick = signClick,
            ) {
                Text(text = daySign.signText)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "连续签到")

                    Text(text = "${daySign.days}天")
                }

                Spacer(modifier = Modifier.width(30.dp))

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Outlined.Help,
                            contentDescription = "每日福利规则",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(30.dp))

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "累计获得")

                    Text(text = "${daySign.bits}Bit")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row {
                    Icon(
                        imageVector = Icons.Outlined.EnergySavingsLeaf,
                        contentDescription = "今日已签到人数",
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = daySign.today,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.width(20.dp))

                    Icon(
                        imageVector = Icons.Outlined.EnergySavingsLeaf,
                        contentDescription = "昨日已签到人数",
                        tint = MaterialTheme.colorScheme.secondary
                    )

                    Text(
                        text = daySign.yesterday,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Row {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = "本月总签到人数",
                        tint = MaterialTheme.colorScheme.secondary
                    )

                    Text(
                        text = daySign.month,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Spacer(modifier = Modifier.width(20.dp))

                    Icon(
                        imageVector = Icons.Outlined.EventAvailable,
                        contentDescription = "已参与人数",
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = daySign.total,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}