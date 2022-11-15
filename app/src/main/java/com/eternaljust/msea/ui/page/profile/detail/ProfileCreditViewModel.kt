package com.eternaljust.msea.ui.page.profile.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ProfileCreditViewModel : ViewModel() {
    val items: List<ProfileCreditTabItem>
        get() = listOf(
            ProfileCreditTabItem.LOG,
            ProfileCreditTabItem.SYSTEM,
            ProfileCreditTabItem.RULE
        )

    private val _viewEvents = Channel<ProfileCreditViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: ProfileCreditViewAction) {
        when (action) {
            is ProfileCreditViewAction.PopBack -> popBack()
        }
    }

    private fun popBack() {
        viewModelScope.launch {
            _viewEvents.send(ProfileCreditViewEvent.PopBack)
        }
    }
}

sealed class ProfileCreditViewEvent {
    object PopBack : ProfileCreditViewEvent()
}

sealed class ProfileCreditViewAction {
    object PopBack: ProfileCreditViewAction()
}

interface ProfileCreditTab {
    val id: String
    val title: String
}

enum class ProfileCreditTabItem : ProfileCreditTab {
    LOG {
        override val id: String
            get() = "log"

        override val title: String
            get() = "积分收益"
    },

    SYSTEM {
        override val id: String
            get() = "system"

        override val title: String
            get() = "系统奖励"
    },

    RULE {
        override val id: String
            get() = "rule"

        override val title: String
            get() = "积分规则"
    }
}