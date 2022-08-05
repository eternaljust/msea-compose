package com.eternaljust.msea.ui.page.profile.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eternaljust.msea.ui.page.profile.LoginFieldItem
import com.eternaljust.msea.ui.page.profile.LoginQuestionItem
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