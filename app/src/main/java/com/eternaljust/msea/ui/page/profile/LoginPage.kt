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
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.eternaljust.msea.ui.widget.MseaSmallTopAppBarColors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LoginPage(
    scaffoldState: SnackbarHostState,
    navController: NavHostController
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var answer by rememberSaveable { mutableStateOf("") }
    var passwordHidden by rememberSaveable { mutableStateOf(true) }

    var fieldExpanded by remember { mutableStateOf(false) }
    var questionExpanded by remember { mutableStateOf(false) }
    val fieldItems = listOf(
        LoginFieldItem.Username,
        LoginFieldItem.Email
    )
    val questionItems = listOf(
        LoginQuestionItem.No,
        LoginQuestionItem.MotherName,
        LoginQuestionItem.GrandpaName,
        LoginQuestionItem.FatherBornCity,
        LoginQuestionItem.OneTeacherName,
        LoginQuestionItem.ComputerModel,
        LoginQuestionItem.FavoriteRestaurantName,
        LoginQuestionItem.LastFourDigitsOfDriverLicense
    )

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("登录") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
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
                            onClick = { fieldExpanded = !fieldExpanded }
                        ) {
                            Text(text = "用户名 ▼")
                        }
                    }

                    DropdownMenu(
                        expanded = fieldExpanded,
                        onDismissRequest = { fieldExpanded = false }
                    ) {
                        fieldItems.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item.title) },
                                onClick = { /* Handle settings! */ },
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
                    value = username,
                    singleLine = true,
                    onValueChange = { username = it },
                    label = { Text("用户名") }
                )

                OutlinedTextField(
                    modifier = LoginModifier(),
                    value = password,
                    onValueChange = { password = it },
                    singleLine = true,
                    label = { Text("密码") },
                    visualTransformation =
                    if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordHidden = !passwordHidden }) {
                            val visibilityIcon =
                                if (passwordHidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            val description = if (passwordHidden) "显示密码" else "隐藏密码"
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
                            onClick = { questionExpanded = !questionExpanded })
                        {
                            Text(text = "未设置请忽略 ▼")
                        }
                    }

                    DropdownMenu(
                        expanded = questionExpanded,
                        onDismissRequest = { questionExpanded = false }
                    ) {
                        questionItems.forEach { item ->
                            DropdownMenuItem(
                                text = { Text(item.title) },
                                onClick = { /* Handle settings! */ },
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
                    value = answer,
                    singleLine = true,
                    onValueChange = { answer = it },
                    label = { Text("答案") }
                )

                LoginSpacer()
                
                Button(
                    modifier = LoginModifier(),
                    onClick = { /*TODO*/ })
                {
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


sealed class LoginQuestionItem(
    val id: String,
    val title: String,
    val icon: ImageVector
) {
    object No : LoginQuestionItem(
        id = "0",
        title = "未设置请忽略",
        icon = Icons.Filled.Visibility
    )

    object MotherName : LoginQuestionItem(
        id = "1",
        title = "母亲的名字",
        icon = Icons.Filled.Woman
    )

    object GrandpaName : LoginQuestionItem(
        id = "2",
        title = "爷爷的名字",
        icon = Icons.Filled.Elderly
    )

    object FatherBornCity : LoginQuestionItem(
        id = "3",
        title = "父亲出生的城市",
        icon = Icons.Filled.Man
    )

    object OneTeacherName : LoginQuestionItem(
        id = "4",
        title = "您其中一位老师的名字",
        icon = Icons.Filled.School
    )

    object ComputerModel : LoginQuestionItem(
        id = "5",
        title = "您个人计算机的型号",
        icon = Icons.Filled.Computer
    )

    object FavoriteRestaurantName : LoginQuestionItem(
        id = "6",
        title = "您最喜欢的餐馆名称",
        icon = Icons.Filled.Restaurant
    )

    object LastFourDigitsOfDriverLicense : LoginQuestionItem(
        id = "7",
        title = "驾驶执照最后四位数字",
        icon = Icons.Filled.Pin
    )
}

sealed class LoginFieldItem(
    val id: String,
    val title: String,
    val icon: ImageVector
) {
    object Username : LoginFieldItem(
        id = "username",
        title = "用户名",
        icon = Icons.Filled.AccountCircle
    )

    object Email : LoginFieldItem(
        id = "email",
        title = "邮箱",
        icon = Icons.Filled.Email
    )
}
