package com.eternaljust.msea.ui.page.home.sign

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            is SignViewAction.Login -> login()
            is SignViewAction.PopBack -> popBack()
        }
    }

    private fun login() {
    }

    private fun popBack() {
        viewModelScope.launch {
            _viewEvents.send(SignViewEvent.PopBack)
        }
    }
}

data class SignViewState(
    val daySign: DaySignModel = DaySignModel()
)

sealed class SignViewEvent {
    object PopBack : SignViewEvent()
    data class Message(val message: String) : SignViewEvent()
}

sealed class SignViewAction {
    object Login : SignViewAction()
    object PopBack : SignViewAction()
}

class DaySignModel {
    val isSign: Boolean = false
    val signText: String = "请先登录"
    var days = "0"
    var bits = "0"

    var today = "今日已签到 0 人"
    var yesterday = "昨日总签到 0 人"
    var month = "本月总签到 0 人"
    var total = "已有 0 人参与"
}