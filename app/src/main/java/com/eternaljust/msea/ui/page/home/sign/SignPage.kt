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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eternaljust.msea.ui.widget.mseaTopAppBarColors
import com.eternaljust.msea.utils.RouteName
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

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
            TopAppBar(
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
                colors = mseaTopAppBarColors()
            )
        },
        content = { paddingValues ->
            Surface(
                modifier = Modifier
                    .padding(paddingValues)
            ) {
                Column {
                    SignHeader(
                        daySign = viewModel.viewStates.daySign,
                        signClick = { viewModel.dispatch(SignViewAction.Sign)},
                        signText = viewModel.viewStates.signText,
                        signTextChange = { viewModel.dispatch(SignViewAction.SignTextChange(it))},
                        signConfirm = { viewModel.dispatch(SignViewAction.SignConfirm)},
                        showSignDialog = viewModel.viewStates.showSignDialog,
                        signDialogClick = { viewModel.dispatch(SignViewAction.SignShowDialog(it))},
                        showRuleDialog = viewModel.viewStates.showRuleDialog,
                        ruleDialogClick = { viewModel.dispatch(SignViewAction.RuleShowDialog(it)) }
                    )

                    SignList(
                        items = viewModel.signItems,
                        scaffoldState = scaffoldState,
                        navController = navController
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignHeader(
    daySign: DaySignModel,
    signClick: () -> Unit,
    signText: String,
    signTextChange: (String) -> Unit,
    signConfirm: () -> Unit,
    showSignDialog: Boolean,
    signDialogClick: (Boolean) -> Unit,
    showRuleDialog: Boolean,
    ruleDialogClick: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .height(180.dp)
            .fillMaxWidth()
    ) {
        if (showSignDialog) {
            AlertDialog(
                onDismissRequest = { signDialogClick(false) },
                title = { Text(text = daySign.signTitle) },
                text = {
                    TextField(
                        modifier = Modifier.height(100.dp),
                        value = signText,
                        onValueChange = { signTextChange(it) },
                        placeholder = { Text(text = daySign.signPlaceholder) }
                    )
                },
                dismissButton = {
                    Button(onClick = { signDialogClick(false) }) {
                        Text(text = "取消")
                    }
                },
                confirmButton = {
                    Button(
                        enabled = signText.isNotEmpty(),
                        onClick = { signConfirm() }
                    ) {
                        Text(text = "发表签到")
                    }
                }
            )
        }

        if (showRuleDialog) {
            AlertDialog(
                onDismissRequest = { ruleDialogClick(false) },
                title = { Text(text = "每日福利规则") },
                text = { Text(text = daySign.rule) },
                confirmButton = {
                    Button(onClick = { ruleDialogClick(false) }) {
                        Text(text = "好的")
                    }
                }
            )
        }
        
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

                    Text(
                        buildAnnotatedString {
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                append(daySign.days)
                            }
                            append("天")
                        }
                    )
                }

                Spacer(modifier = Modifier.width(30.dp))

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(onClick = { ruleDialogClick(true) }) {
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

                    Text(
                        text = "${daySign.bits}Bit",
                        color = MaterialTheme.colorScheme.primary
                    )
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

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SignList(
    items: List<SignTabItem>,
    scaffoldState: SnackbarHostState,
    navController: NavHostController
) {
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()

    Column {
        TabRow(selectedTabIndex = pagerState.currentPage) {
            items.forEachIndexed { index, item ->
                Tab(
                    text = {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.labelMedium
                        )
                    },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scope.launch {
                            pagerState.scrollToPage(index)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        HorizontalPager(count = items.size, state = pagerState) {
            val item = items[pagerState.currentPage]
            if (item == SignTabItem.DAY_SIGN) {
                SignListPage(
                    scaffoldState = scaffoldState,
                    navController = navController,
                    tabItem = item
                )
            } else {
                SignDayListPage(
                    scaffoldState = scaffoldState,
                    navController = navController,
                    viewModel = if (item == SignTabItem.TOTAL_DAYS) SignDayListViewModel.days else
                        SignDayListViewModel.reward
                )
            }
        }
    }
}