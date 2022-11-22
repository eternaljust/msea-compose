package com.eternaljust.msea.ui.page.profile.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AboutViewModel : ViewModel() {
    val items: List<AboutListItem>
    get() = listOf(
        AboutListItem.LICENSE,
        AboutListItem.SDK_LIST,
        AboutListItem.SOURCE_CODE
    )

    private val _viewEvents = Channel<AboutViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: AboutViewAction) {
        when (action) {
            is AboutViewAction.PopBack -> popBack()
        }
    }

    private fun popBack() {
        viewModelScope.launch {
            _viewEvents.send(AboutViewEvent.PopBack)
        }
    }
}

sealed class AboutViewEvent {
    object PopBack : AboutViewEvent()
}

sealed class AboutViewAction {
    object PopBack: AboutViewAction()
}

interface AboutList {
    val id: String
    val title: String
}

enum class AboutListItem : AboutList {
    LICENSE {
        override val id: String
            get() = "license"

        override val title: String
            get() = "开源协议"
    },

    SOURCE_CODE {
        override val id: String
            get() = "source_code"

        override val title: String
            get() = "源代码"
    },

    SDK_LIST {
        override val id: String
            get() = "sdk_list"

        override val title: String
            get() = "SDK 目录"
    }
}