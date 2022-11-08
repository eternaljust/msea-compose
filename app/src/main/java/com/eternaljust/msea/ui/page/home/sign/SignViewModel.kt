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

    val signItems: List<SignTabItem>
        get() = listOf(
            SignTabItem.DAY_SIGN,
            SignTabItem.TOTAL_DAYS,
            SignTabItem.TOTAL_REWARD
        )

    fun dispatch(action: SignViewAction) {
        when (action) {
            is SignViewAction.GetDaySign -> getDaySign()
            is SignViewAction.Sign -> sign()
            is SignViewAction.PopBack -> popBack()
            is SignViewAction.CalendarShowDialog -> calendarDialog(action.isShow)
            is SignViewAction.RuleShowDialog -> ruleDialog(action.isShow)
            is SignViewAction.SignShowDialog -> signDialog(action.isShow)
            is SignViewAction.SignTextChange -> signChange(action.text)
            is SignViewAction.SignConfirm -> signComfirm()
        }
    }

    private fun getDaySign() {
        viewModelScope.launch(Dispatchers.IO) {
            val url = HTMLURL.GET_DAY_SIGN
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

            val title = document.selectXpath("//h3[@class='wqpc_title']").text()
            if (title.isNotEmpty()) {
                daySign.monthTitle = title
            }

            var list = mutableListOf<CalendarDayModel>()
            val weeks = document.selectXpath("//ul[@class='wq_week']/li")
            weeks.forEach {
                var model = CalendarDayModel()
                val text = it.text()
                if (text.isNotEmpty()) {
                    model.title = text
                }
                model.isWeek = true
                val style = it.attr("style")
                if (style.isNotEmpty()) {
                    model.isWeekend = true
                }
                list.add(model)
            }
            val dates = document.selectXpath("//ul[@class='wq_date']/li")
            dates.forEach {
                var model = CalendarDayModel()
                val text = it.text()
                if (text.isNotEmpty()) {
                    model.title = text
                }
                val liClassText = it.selectXpath("span/i").attr("class")
                if (liClassText == "wqsign_dot_red" || liClassText == "wqsign_dot_white") {
                    model.isSign = true
                }
                val classText = it.selectXpath("span").attr("class")
                if (classText == "wq_sign_today") {
                    model.isToday = true
                }

                list.add(model)
            }
            daySign.calendars = list

            viewStates = viewStates.copy(daySign = daySign)
        }
    }

    private fun sign() {
        viewModelScope.launch {
            if (UserInfo.instance.auth.isNotEmpty()) {
                viewStates = viewStates.copy(showSignDialog = true)
            } else {
                _viewEvents.send(SignViewEvent.Login)
            }
        }
    }

    private fun signDialog(isShow: Boolean) {
        viewStates = viewStates.copy(showSignDialog = isShow)
    }

    private fun signChange(text: String) {
        viewStates = viewStates.copy(signText = text)
    }

    private fun signComfirm() {
        viewModelScope.launch(Dispatchers.IO) {
            val url = HTMLURL.SIGN_MESSAGE
            val message = NetworkUtil.urlEncode(viewStates.signText)
            val encodedParams = mapOf(
                "message" to message
            )
            val document = NetworkUtil.postRequest(url, emptyMap(), encodedParams)
            val text = document.selectXpath("//div[@id='messagetext']/p[1]").text()
            val script = document.selectXpath("//div[@id='messagetext']/p[1]/script").text()
            if (text.isNotEmpty()) {
                if (script.isNotEmpty() && text.contains(script)) {
                    _viewEvents.send(SignViewEvent.Message(text.replace(script, "")))
                } else {
                    _viewEvents.send(SignViewEvent.Message(text))
                }
                viewStates = viewStates.copy(signText = "")
            } else {
                _viewEvents.send(SignViewEvent.Message("签到失败！"))
            }
            signDialog(false)
            getDaySign()
        }
    }

    private fun ruleDialog(isShow: Boolean) {
        viewStates = viewStates.copy(showRuleDialog = isShow)
    }

    private fun calendarDialog(isShow: Boolean) {
        viewStates = viewStates.copy(showCalendarDialog = isShow)
    }

    private fun popBack() {
        viewModelScope.launch {
            _viewEvents.send(SignViewEvent.PopBack)
        }
    }
}

data class SignViewState(
    val daySign: DaySignModel = DaySignModel(),
    val showSignDialog: Boolean = false,
    val signText: String = "",
    val showRuleDialog: Boolean = false,
    val showCalendarDialog: Boolean = false
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
    object SignConfirm : SignViewAction()

    data class CalendarShowDialog(val isShow: Boolean) : SignViewAction()
    data class RuleShowDialog(val isShow: Boolean) : SignViewAction()
    data class SignShowDialog(val isShow: Boolean) : SignViewAction()
    data class SignTextChange(val text: String) : SignViewAction()
}

class DaySignModel {
    var isSign: Boolean = false
    var signText: String = "请先登录"
    var days = "0"
    var bits = "0"
    var rule = ""
    var signTitle = "签到留言，你的心情随笔，愿望清单...今天吃啥？"
    var signPlaceholder = "提倡沿袭古法的纯手工打卡，反对自动签到，" +
            "自动签到每次将被扣除 10 倍于所得积分 :)"

    var today = "今日已签到 0 人"
    var yesterday = "昨日总签到 0 人"
    var month = "本月总签到 0 人"
    var total = "已有 0 人参与"

    var monthTitle = ""
    var calendars: List<CalendarDayModel> = emptyList()
}

class CalendarDayModel {
    var title = ""
    var isWeek = false
    var isWeekend = false
    var isSign = false
    var isToday = false
}

interface SignTab {
    val id: String
    val title: String
}

enum class SignTabItem : SignTab {
    DAY_SIGN {
        override val id: String
            get() = "daysign"

        override val title: String
            get() = "今日签到列表(Bit)"
    },

    TOTAL_DAYS {
        override val id: String
            get() = "totaldays"

        override val title: String
            get() = "总天数排名(天)"
    },

    TOTAL_REWARD {
        override val id: String
            get() = "totalreward"

        override val title: String
            get() = "总奖励排名(天)"
    }
}