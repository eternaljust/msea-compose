package com.eternaljust.msea.ui.page.home.sign

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eternaljust.msea.utils.HTMLURL
import com.eternaljust.msea.utils.NetworkUtil
import com.eternaljust.msea.utils.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SignViewModel : ViewModel() {
    var viewStates by mutableStateOf(SignViewState())
        private set
    private val _viewEvents = Channel<SignViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: SignViewAction) {
        when (action) {
            is SignViewAction.GetDaySign -> getDaySign()
            is SignViewAction.Sign -> sign()
            is SignViewAction.PopBack -> popBack()
            is SignViewAction.RuleShowDialog -> showDialog(action.isShow)
        }
    }

    private fun getDaySign() {
        viewModelScope.launch(Dispatchers.IO) {
            val url = HTMLURL.GETDAYSIGN
            val document = NetworkUtil.getRequest(url)

            val daySign = DaySignModel()
            val liPath = "//div[@class='wqpc_sign_info']/ul/"
            val li1 = document.selectXpath("${liPath}li[1]").text()
            if (li1.isNotEmpty()) {
                daySign.today = li1
            }
            val li2 = document.selectXpath("${liPath}li[2]").text()
            if (li2.isNotEmpty()) {
                daySign.yesterday = li2
            }
            val li3 = document.selectXpath("${liPath}li[3]").text()
            if (li3.isNotEmpty()) {
                daySign.month = li3
            }
            val li4 = document.selectXpath("${liPath}li[4]").text()
            if (li4.isNotEmpty()) {
                daySign.total = li4
            }

            val ulPath = "//div[@class='wqpc_sign_continuity']/ul/"
            val span1 = document.selectXpath("${ulPath}li[1]/span").text()
            if (span1.isNotEmpty()) {
                daySign.days = span1
            }
            val span2 = document.selectXpath("${ulPath}li[2]/span").text()
            if (span2.isNotEmpty()) {
                daySign.bits = span2.replace("Bit", "")
            }

            val sign1 = document.selectXpath("//a[@class='wqpc_sign_btn_red']").text()
            val sign2 = document.selectXpath("//div[@class='wqpc_sign_btna']").text()
            var signText = ""
            if (sign1.isNotEmpty()) {
                signText = sign1
            } else if (sign2.isNotEmpty()) {
                signText = sign2
            }
            signText = signText.replace(" ", "")
            daySign.signText = signText
            daySign.isSign = signText.contains("已签到")

            var rule = ""
            val p = document.selectXpath("//div[@class='wqc_sign_rule']/p")
            p.forEach {
                val text = it.text()
                if (text.isNotEmpty()) {
                    rule += text + "\n"
                }
            }
            daySign.rule = rule

            viewStates = viewStates.copy(daySign = daySign)
        }
    }

    private fun sign() {
        viewModelScope.launch {
            if (UserInfo.instance.isLogin) {

            } else {
                _viewEvents.send(SignViewEvent.Login)
            }
        }
    }

    private fun showDialog(isShow: Boolean) {
        viewStates = viewStates.copy(showRuleDialog = isShow)
    }

    private fun popBack() {
        viewModelScope.launch {
            _viewEvents.send(SignViewEvent.PopBack)
        }
    }
}

data class SignViewState(
    val daySign: DaySignModel = DaySignModel(),
    val showRuleDialog: Boolean = false
)

sealed class SignViewEvent {
    object Login : SignViewEvent()
    object PopBack : SignViewEvent()
    data class Message(val message: String) : SignViewEvent()
}

sealed class SignViewAction {
    object GetDaySign : SignViewAction()
    object Sign : SignViewAction()
    object PopBack : SignViewAction()

    data class RuleShowDialog(val isShow: Boolean) : SignViewAction()
}

class DaySignModel {
    var isSign: Boolean = false
    var signText: String = "请先登录"
    var days = "0"
    var bits = "0"
    var rule = ""

    var today = "今日已签到 0 人"
    var yesterday = "昨日总签到 0 人"
    var month = "本月总签到 0 人"
    var total = "已有 0 人参与"
}