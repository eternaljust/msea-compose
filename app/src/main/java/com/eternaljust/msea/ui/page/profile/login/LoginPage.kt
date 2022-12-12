package com.eternaljust.msea.ui.page.profile.login

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eternaljust.msea.R
import com.eternaljust.msea.ui.widget.NormalTopAppBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LoginPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController,
    loginMessage: (String) -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.viewEvents.collect {
            if (it is LoginViewEvent.PopBack) {
                if (it.message.isNotEmpty()) {
                    loginMessage(it.message)
                }
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
            NormalTopAppBar(
                title = "登录",
                onClick = { viewModel.dispatch(LoginViewAction.PopBack()) }
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
                        modifier = loginSpacer(),
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
                                    viewModel.dispatch(LoginViewAction.UpdateUsername(""))
                                },
                                leadingIcon = {
                                    Icon(
                                        item.icon,
                                        contentDescription = item.title
                                    )
                                })

                            Divider()
                        }
                    }
                }

                OutlinedTextField(
                    modifier = loginSpacer(),
                    value = viewModel.viewStates.username,
                    singleLine = true,
                    onValueChange = { viewModel.dispatch(LoginViewAction.UpdateUsername(it)) },
                    label = { Text(viewModel.viewStates.loginfield.title) }
                )

                OutlinedTextField(
                    modifier = loginSpacer(),
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
                            val visibilityIcon = if (viewModel.viewStates.passwordHidden)
                                R.drawable.ic_baseline_visibility_24
                            else R.drawable.ic_baseline_visibility_off_24
                            val description = if (viewModel.viewStates.passwordHidden) "显示密码"
                            else "隐藏密码"
                            if (viewModel.viewStates.password.isNotEmpty()) {
                                Icon(
                                    painter = painterResource(id = visibilityIcon),
                                    contentDescription = description
                                )
                            }
                        }
                    }
                )

                LoginSpacer()

                Box {
                    Row(
                        modifier = loginSpacer(),
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
                                    if (item.imageVector != null) {
                                        item.imageVector?.let {
                                            Icon(imageVector = it, contentDescription = item.title)
                                        }
                                    } else if (item.painter != null) {
                                        item.painter?.let {
                                            Icon(
                                                painter = painterResource(id = it),
                                                contentDescription = item.title
                                            )
                                        }
                                    }
                                }
                            )

                            Divider()
                        }
                    }
                }

                if (viewModel.viewStates.question != LoginQuestionItem.No) {
                    OutlinedTextField(
                        modifier = loginSpacer(),
                        value = viewModel.viewStates.answer,
                        singleLine = true,
                        onValueChange = { viewModel.dispatch(LoginViewAction.UpdateAnswer(it)) },
                        label = { Text("答案") }
                    )
                }

                LoginSpacer()
                
                Button(
                    modifier = loginSpacer(),
                    enabled = viewModel.viewStates.loginEnabled,
                    onClick = {
                        keyboardController?.hide()
                        viewModel.dispatch(LoginViewAction.Login)
                    }
                ) {
                    Text(text = if (viewModel.viewStates.loginEnabled) "登录" else "登录中...")
                }
            }
        }
    )
}

@Composable
private fun loginSpacer(): Modifier {
    return Modifier
        .width(300.dp)
}

@Composable
private fun LoginSpacer() {
    Spacer(modifier = Modifier.height(10.dp))
}
