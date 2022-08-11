package com.eternaljust.msea.ui.page.profile.login

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eternaljust.msea.utils.NetworkUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

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

    private var action = ""
    private var formhash = ""

    init {
        loadData()
    }

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

    private fun loadData() {
        GlobalScope.launch {
            val url = "https://www.chongbuluo.com/member.php?mod=logging&action=login"
            val document = NetworkUtil.getRequest(url)
            val element1 = document.selectXpath("//form[@name='login']").first()
            val text1 = element1?.attr("action")
            if (text1 != null) {
                action = text1
                println("action=$action")
            }
            val element2 = document.selectXpath("//input[@name='formhash']").first()
            val text2 = element2?.attr("value")
            if (text2 != null) {
                formhash = text2
                println("formhash=$formhash")
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            if (viewStates.username.isEmpty() || viewStates.password.isEmpty()) {
                _viewEvents.send(LoginViewEvent.Message("请输入用户名｜邮箱或者密码"))
                return@launch
            }
            if (viewStates.question != LoginQuestionItem.No && viewStates.answer.isEmpty()) {
                _viewEvents.send(LoginViewEvent.Message("请输入安全提问的答案"))
                return@launch
            }
            _viewEvents.send(LoginViewEvent.Message("正在登录..."))

            val params = mapOf(
                "formhash" to viewStates.formhash,
                "loginfield" to viewStates.loginfield.id,
                "username" to viewStates.username,
                "questionid" to viewStates.loginfield.id,
                "answer" to viewStates.answer,
                "password" to viewStates.password
            )

            var url = "https://www.chongbuluo.com/$action"
            GlobalScope.launch {
                var document = NetworkUtil.postRequest(url = url, params = params)

                val messagetext = document.selectXpath("//div[@class='alert_error']/p")
                val info = document.selectXpath("//div[@class='info']/li")
                val myinfo = document.selectXpath("//div[@id='myinfo']/p")
                if (messagetext.text().isNotEmpty()) {
                    _viewEvents.send(LoginViewEvent.Message(messagetext.text()))
                } else if (info.text().isNotEmpty()) {
                    _viewEvents.send(LoginViewEvent.Message(info.text()))
                } else if (myinfo.text().isNotEmpty()) {
                    _viewEvents.send((LoginViewEvent.Message("欢迎回来")))
                }
            }
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
