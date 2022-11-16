package com.eternaljust.msea.ui.page.profile.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ProfileFriendViewModel : ViewModel() {
    val items: List<ProfileFriendTabItem>
        get() = listOf(
            ProfileFriendTabItem.FRIEND,
            ProfileFriendTabItem.VISITOR,
            ProfileFriendTabItem.TRACE
        )

    private val _viewEvents = Channel<ProfileFriendViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: ProfileFriendViewAction) {
        when (action) {
            is ProfileFriendViewAction.PopBack -> popBack()
        }
    }

    private fun popBack() {
        viewModelScope.launch {
            _viewEvents.send(ProfileFriendViewEvent.PopBack)
        }
    }
}

sealed class ProfileFriendViewEvent {
    object PopBack : ProfileFriendViewEvent()
}

sealed class ProfileFriendViewAction {
    object PopBack: ProfileFriendViewAction()
}

interface ProfileFriendTab {
    val id: String
    val title: String
    val header: String
}

enum class ProfileFriendTabItem : ProfileFriendTab {
    FRIEND {
        override val id: String
            get() = "friend"

        override val title: String
            get() = "好友列表"

        override val header: String
            get() = "按照好友热度排序"
    },

    VISITOR {
        override val id: String
            get() = "visitor"

        override val title: String
            get() = "我的访客"

        override val header: String
            get() = "他们拜访过您，回访一下吧"
    },

    TRACE {
        override val id: String
            get() = "trace"

        override val title: String
            get() = "我的足迹"

        override val header: String
            get() = "您曾经拜访过的用户列表"
    }
}