package com.eternaljust.msea.ui.page.profile.login

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    var viewStates by mutableStateOf(LoginViewState())
        private set
    private val _viewEvents = Channel<LoginViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    val fieldItems: List<LoginFieldItem>
        get() = listOf(
            LoginFieldItem.Username,
            LoginFieldItem.Email
        )

    val questionItems: List<LoginQuestionItem>
        get() = listOf(
            LoginQuestionItem.No,
            LoginQuestionItem.MotherName,
            LoginQuestionItem.GrandpaName,
            LoginQuestionItem.FatherBornCity,
            LoginQuestionItem.OneTeacherName,
            LoginQuestionItem.ComputerModel,
            LoginQuestionItem.FavoriteRestaurantName,
            LoginQuestionItem.LastFourDigitsOfDriverLicense
        )

    fun dispatch(action: LoginViewAction) {
        when (action) {
            is LoginViewAction.Login -> login()
            is LoginViewAction.PopBack -> popBack()
            is LoginViewAction.PasswordShowOrHidden -> passwordShowOrHidden()
            is LoginViewAction.UpdateUsername -> updateUsername(action.username)
            is LoginViewAction.UpdatePassword -> updatePassword(action.password)
            is LoginViewAction.UpdateAnswer -> updateAnswer(action.answer)
            is LoginViewAction.UpdateQuestion -> updateQuestion(action.question)
            is LoginViewAction.UpdateLoginfield -> updaterLoginfield(action.loginfield)
            is LoginViewAction.UpdateQuestionExpanded -> updaterQuestionExpanded(action.expanded)
            is LoginViewAction.UpdateLoginfieldExpanded -> updaterLoginfieldExpanded(action.expanded)
        }
    }

    private fun login() {
        viewModelScope.launch {
            _viewEvents.send(LoginViewEvent.Message("正在登录"))
        }
    }

    private fun popBack() {
        viewModelScope.launch {
            _viewEvents.send(LoginViewEvent.PopBack)
        }
    }

    private fun passwordShowOrHidden() {
        viewStates = viewStates.copy(passwordHidden = !viewStates.passwordHidden)
    }

    private fun updateUsername(username: String) {
        viewStates = viewStates.copy(username = username)
    }

    private fun updatePassword(password: String) {
        viewStates = viewStates.copy(password = password)
    }

    private fun updateAnswer(answer: String) {
        viewStates = viewStates.copy(answer = answer)
    }

    private fun updateQuestion(question: LoginQuestionItem) {
        viewStates = viewStates.copy(question = question)
    }

    private fun updaterLoginfield(loginfield: LoginFieldItem) {
        viewStates = viewStates.copy(loginfield = loginfield)
    }

    private fun updaterQuestionExpanded(expanded: Boolean) {
        viewStates = viewStates.copy(questionExpanded = expanded)
    }

    private fun updaterLoginfieldExpanded(expanded: Boolean) {
        viewStates = viewStates.copy(lgoinfieldExpanded = expanded)
    }
}

data class LoginViewState(
    val formhash: String = "",
    val loginfield: LoginFieldItem = LoginFieldItem.Username,
    val username: String = "",
    val password: String = "",
    val question: LoginQuestionItem = LoginQuestionItem.No,
    val answer: String = "",
    val passwordHidden: Boolean = true,
    val lgoinfieldExpanded: Boolean = false,
    val questionExpanded: Boolean = false
)

sealed class LoginViewEvent {
    object PopBack : LoginViewEvent()
    data class Message(val message: String) : LoginViewEvent()
}

sealed class LoginViewAction {
    object Login : LoginViewAction()
    object PopBack : LoginViewAction()
    object PasswordShowOrHidden : LoginViewAction()

    data class UpdateUsername(val username: String) : LoginViewAction()
    data class UpdatePassword(val password: String) : LoginViewAction()
    data class UpdateAnswer(val answer: String) : LoginViewAction()
    data class UpdateQuestion(val question: LoginQuestionItem) : LoginViewAction()
    data class UpdateLoginfield(val loginfield: LoginFieldItem) : LoginViewAction()
    data class UpdateQuestionExpanded(val expanded: Boolean) : LoginViewAction()
    data class UpdateLoginfieldExpanded(val expanded: Boolean) : LoginViewAction()
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
