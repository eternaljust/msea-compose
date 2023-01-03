package com.eternaljust.msea.ui.page.profile.login

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eternaljust.msea.R
import com.eternaljust.msea.utils.*
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

    fun dispatch(action: LoginViewAction) {
        when (action) {
            is LoginViewAction.Login -> login()
            is LoginViewAction.PopBack -> popBack(action.message)
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
        viewModelScope.launch(Dispatchers.IO) {
            if (viewStates.username.isEmpty() || viewStates.password.isEmpty()) {
                _viewEvents.send(LoginViewEvent.Message("请输入用户名｜邮箱或者密码"))
                return@launch
            }
            if (viewStates.question != LoginQuestionItem.No && viewStates.answer.isEmpty()) {
                _viewEvents.send(LoginViewEvent.Message("请输入安全提问的答案"))
                return@launch
            }
            viewStates = viewStates.copy(loginEnabled = false)

            val url = HTMLURL.LOGIN
            val username = NetworkUtil.urlEncode(viewStates.username)
            val password = NetworkUtil.urlEncode(viewStates.password)
            val answer = NetworkUtil.urlEncode(viewStates.answer)
            val params = mapOf(
                "loginfield" to viewStates.loginfield.id,
                "questionid" to viewStates.question.id,
            )
            val encodedParams = mapOf(
                "username" to username,
                "password" to password,
                "answer" to answer
            )

            val document = NetworkUtil.postRequest(url, params, encodedParams)
            val messagetext = document.selectXpath("//div[@class='alert_error']/p")
            val info = document.selectXpath("//div[@class='info']/li")
            val myinfo = document.selectXpath("//div[@id='myinfo']/p")
            if (messagetext.text().isNotEmpty()) {
                _viewEvents.send(LoginViewEvent.Message(messagetext.text()))
            } else if (info.text().isNotEmpty()) {
                _viewEvents.send(LoginViewEvent.Message(info.text()))
            } else if (myinfo.text().isNotEmpty()) {
                val level = myinfo.first()?.selectXpath("//a[@id='g_upmine']")?.text()
                val src = document.selectXpath("//div[@id='um']//img").attr("src")
                val xpath = "//div[@id='myinfo']/p//a[@target='_blank']"
                val name = document.selectXpath(xpath).text()
                val href = document.selectXpath(xpath).attr("href")
                if (href.isNotEmpty() && href.contains("-")) {
                    val ids = href.split("-")
                    val last = ids.last()
                    if (last.contains(".html")) {
                        val uid = last.replace(".html", "")
                        println("uid=$uid")
                        UserInfo.instance.uid = uid
                    }
                }
                if (name.isNotEmpty()) {
                    println("name=$name")
                    UserInfo.instance.name = name
                }
                if (level != null && level.isNotEmpty()) {
                    println("level=$level")
                    UserInfo.instance.level = level
                }
                if (src.isNotEmpty()) {
                    val avatar = NetworkUtil.getAvatar(src)
                    println("avatar=$avatar")
                    UserInfo.instance.avatar = avatar
                }
                popBack("欢迎您回来，$level $name")
            }
            viewStates = viewStates.copy(loginEnabled = true)
        }
    }

    private fun popBack(message: String = "") {
        viewModelScope.launch {
            _viewEvents.send(LoginViewEvent.PopBack(message))
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
    val questionExpanded: Boolean = false,
    val loginEnabled: Boolean = true,
)

sealed class LoginViewEvent {
    data class PopBack(val message: String) : LoginViewEvent()
    data class Message(val message: String) : LoginViewEvent()
}

sealed class LoginViewAction {
    object Login : LoginViewAction()
    object PasswordShowOrHidden : LoginViewAction()

    data class PopBack(val message: String = "") : LoginViewAction()
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
    val imageVector: ImageVector?,
    val painter: Int?
) {
    No(
        id = "0",
        title = "未设置请忽略",
        imageVector = null,
        painter = R.drawable.ic_baseline_visibility_24
    ),

    MotherName(
        id = "1",
        title = "母亲的名字",
        imageVector = null,
        painter = R.drawable.ic_baseline_woman_24
    ),

    GrandpaName(
        id = "2",
        title = "爷爷的名字",
        imageVector = null,
        painter = R.drawable.ic_baseline_elderly_24
    ),

    FatherBornCity(
        id = "3",
        title = "父亲出生的城市",
        imageVector = null,
        painter = R.drawable.ic_baseline_man_24
    ),

    OneTeacherName(
        id = "4",
        title = "您其中一位老师的名字",
        imageVector = null,
        painter = R.drawable.ic_baseline_school_24
    ),

    ComputerModel(
        id = "5",
        title = "您个人计算机的型号",
        imageVector = null,
        painter = R.drawable.ic_baseline_computer_24
    ),

    FavoriteRestaurantName(
        id = "6",
        title = "您最喜欢的餐馆名称",
        imageVector = null,
        painter = R.drawable.ic_baseline_restaurant_24
    ),

    LastFourDigitsOfDriverLicense(
        id = "7",
        title = "驾驶执照最后四位数字",
        imageVector = null,
        painter = R.drawable.ic_baseline_pin_24
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
