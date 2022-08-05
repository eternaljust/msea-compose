package com.eternaljust.msea.ui.page.profile

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.eternaljust.msea.ui.page.profile.login.LoginViewModel
import com.eternaljust.msea.ui.widget.MseaSmallTopAppBarColors
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eternaljust.msea.ui.page.profile.login.LoginViewAction
import com.eternaljust.msea.ui.page.profile.login.LoginViewEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LoginPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    viewModel: LoginViewModel = viewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect {
            if (it is LoginViewEvent.PopBack) {
                navController.popBackStack()
            } else if (it is LoginViewEvent.Message) {
                scope.launch {
                    scaffoldState.showSnackbar(message = it.message)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("登录") },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.dispatch(LoginViewAction.PopBack) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                colors = MseaSmallTopAppBarColors()
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                keyboardController?.hide()
                            }
                        )
                    },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box {
                    Row(
                        modifier = LoginModifier(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            modifier = Modifier.height(30.dp),
                            contentPadding = PaddingValues(
                                start = 14.dp,
                                top = 4.dp,
                                end = 14.dp,
                                bottom = 4.dp
                            ),
                            onClick = {
                                viewModel.dispatch(LoginViewAction.UpdateLoginfieldExpanded(!viewModel.viewStates.lgoinfieldExpanded))
                            }
                        ) {
                            Text(text = "${viewModel.viewStates.loginfield.title} ▼")
                        }
                    }

                    DropdownMenu(
                        expanded = viewModel.viewStates.lgoinfieldExpanded,
                        onDismissRequest = { viewModel.dispatch(LoginViewAction.UpdateLoginfieldExpanded(false)) }
                    ) {
                        viewModel.fieldItems.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item.title) },
                                onClick = {
                                    viewModel.dispatch(LoginViewAction.UpdateLoginfield(item))
                                    viewModel.dispatch(LoginViewAction.UpdateLoginfieldExpanded(false))
                                },
                                leadingIcon = {
                                    Icon(
                                        item.icon,
                                        contentDescription = item.title
                                    )
                                })

                            MenuDefaults.Divider()
                        }
                    }
                }

                OutlinedTextField(
                    modifier = LoginModifier(),
                    value = viewModel.viewStates.username,
                    singleLine = true,
                    onValueChange = { viewModel.dispatch(LoginViewAction.UpdateUsername(it)) },
                    label = { Text(viewModel.viewStates.loginfield.title) }
                )

                OutlinedTextField(
                    modifier = LoginModifier(),
                    value = viewModel.viewStates.password,
                    onValueChange = { viewModel.dispatch(LoginViewAction.UpdatePassword(it)) },
                    singleLine = true,
                    label = { Text("密码") },
                    visualTransformation =
                    if (viewModel.viewStates.passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(
                            onClick = { viewModel.dispatch(LoginViewAction.PasswordShowOrHidden)}
                        ) {
                            val visibilityIcon =
                                if (viewModel.viewStates.passwordHidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            val description = if (viewModel.viewStates.passwordHidden) "显示密码" else "隐藏密码"
                            Icon(imageVector = visibilityIcon, contentDescription = description)
                        }
                    }
                )

                LoginSpacer()

                Box {
                    Row(
                        modifier = LoginModifier(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "安全提问：")

                        OutlinedButton(
                            modifier = Modifier.height(30.dp),
                            contentPadding = PaddingValues(
                                start = 24.dp,
                                top = 4.dp,
                                end = 24.dp,
                                bottom = 4.dp
                            ),
                            onClick = {
                                viewModel.dispatch(LoginViewAction.UpdateQuestionExpanded(!viewModel.viewStates.questionExpanded))
                            }
                        ) {
                            Text(text = "${viewModel.viewStates.question.title} ▼")
                        }
                    }

                    DropdownMenu(
                        expanded = viewModel.viewStates.questionExpanded,
                        onDismissRequest = { viewModel.dispatch(LoginViewAction.UpdateQuestionExpanded(false))}
                    ) {
                        viewModel.questionItems.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item.title) },
                                onClick = {
                                    viewModel.dispatch(LoginViewAction.UpdateQuestion(item))
                                    viewModel.dispatch(LoginViewAction.UpdateQuestionExpanded(false))
                                },
                                leadingIcon = {
                                    Icon(
                                        item.icon,
                                        contentDescription = item.title
                                    )
                                }
                            )

                            MenuDefaults.Divider()
                        }
                    }
                }

                if (viewModel.viewStates.question != LoginQuestionItem.No) {
                    OutlinedTextField(
                        modifier = LoginModifier(),
                        value = viewModel.viewStates.answer,
                        singleLine = true,
                        onValueChange = { viewModel.dispatch(LoginViewAction.UpdateAnswer(it)) },
                        label = { Text("答案") }
                    )
                }

                LoginSpacer()
                
                Button(
                    modifier = LoginModifier(),
                    onClick = {
                        keyboardController?.hide()
                        viewModel.dispatch(LoginViewAction.Login)
                    }
                ) {
                    Text(text = "登录")
                }
            }
        }
    )
}

@Composable
fun LoginModifier(): Modifier {
    return Modifier
        .width(300.dp)
}

@Composable
fun LoginSpacer() {
    Spacer(modifier = Modifier.height(10.dp))
}

enum class LoginQuestionItem(
    val id: String,
    val title: String,
    val icon: ImageVector
) {
     No(
        id = "0",
        title = "未设置请忽略",
        icon = Icons.Filled.Visibility
    ),

    MotherName(
        id = "1",
        title = "母亲的名字",
        icon = Icons.Filled.Woman
    ),

    GrandpaName(
        id = "2",
        title = "爷爷的名字",
        icon = Icons.Filled.Elderly
    ),

    FatherBornCity(
        id = "3",
        title = "父亲出生的城市",
        icon = Icons.Filled.Man
    ),

    OneTeacherName(
        id = "4",
        title = "您其中一位老师的名字",
        icon = Icons.Filled.School
    ),

    ComputerModel(
        id = "5",
        title = "您个人计算机的型号",
        icon = Icons.Filled.Computer
    ),

    FavoriteRestaurantName(
        id = "6",
        title = "您最喜欢的餐馆名称",
        icon = Icons.Filled.Restaurant
    ),

    LastFourDigitsOfDriverLicense(
        id = "7",
        title = "驾驶执照最后四位数字",
        icon = Icons.Filled.Pin
    )
}

enum class LoginFieldItem(
    var id: String,
    var title: String,
    var icon: ImageVector
) {
    Username(
        id = "username",
        title = "用户名",
        icon = Icons.Filled.AccountCircle
    ),

    Email(
        id = "email",
        title = "邮箱",
        icon = Icons.Filled.Email
    )
}
