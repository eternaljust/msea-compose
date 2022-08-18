package com.eternaljust.msea.ui.page.profile.login

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eternaljust.msea.utils.DataStoreUtil
import com.eternaljust.msea.utils.HTMLURL
import com.eternaljust.msea.utils.NetworkUtil
import com.eternaljust.msea.utils.UserInfoKey
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
                if (href.isNotEmpty()) {
                    val ids = href.split("&")
                    val last = ids.last()
                    if (last.contains("uid")) {
                        val uid = last.split("=")[1]
                        println("uid=$uid")
                        DataStoreUtil.syncSetData(UserInfoKey.UID, uid)
                    }
                }
                if (name.isNotEmpty()) {
                    println("name=$name")
                    DataStoreUtil.syncSetData(UserInfoKey.NAME, name)
                }
                if (level != null && level.isNotEmpty()) {
                    println("level=$level")
                    DataStoreUtil.syncSetData(UserInfoKey.LEVEL, level)
                }
                if (src.isNotEmpty()) {
                    val avatar = src.replace("&size=small", "" )
                    println("avatar=$avatar")
                    DataStoreUtil.syncSetData(UserInfoKey.AVATAR, avatar)
                }
                _viewEvents.send((LoginViewEvent.Message("欢迎您回来，$level $name")))
            }
            viewStates = viewStates.copy(loginEnabled = true)
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
    val username: String = "远恒之义",
    val password: String = "tzq1118?CBL",
    val question: LoginQuestionItem = LoginQuestionItem.No,
    val answer: String = "",
    val passwordHidden: Boolean = true,
    val lgoinfieldExpanded: Boolean = false,
    val questionExpanded: Boolean = false,
    val loginEnabled: Boolean = true,
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
