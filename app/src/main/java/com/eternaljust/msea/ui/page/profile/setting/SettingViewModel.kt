package com.eternaljust.msea.ui.page.profile.setting

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eternaljust.msea.utils.RouteName
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SettingViewModel : ViewModel() {
    val itemGroups: List<List<SettingListItem>>
        get() {
            val isDynamic = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            var themeList = mutableListOf<SettingListItem>(
                SettingListItem.DARK_MODE
            )
            if (isDynamic) {
                themeList.add(SettingListItem.COLOR_SCHEME)
            }
            return listOf(
                themeList,
                listOf(
                    SettingListItem.FEEDBACK,
                    SettingListItem.CONTACT_US,
                    SettingListItem.SHARE,
                    SettingListItem.CLEAN_CACHE,
                ),
                listOf(
                    SettingListItem.TERMS_OF_SERVICE
                )
            )
        }

    private val _viewEvents = Channel<SettingViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: SettingViewAction) {
        when (action) {
            is SettingViewAction.PopBack -> popBack()
        }
    }

    private fun popBack() {
        viewModelScope.launch {
            _viewEvents.send(SettingViewEvent.PopBack)
        }
    }
}

sealed class SettingViewEvent {
    object PopBack : SettingViewEvent()
}

sealed class SettingViewAction {
    object PopBack: SettingViewAction()
}

interface SettingList {
    val route: String
    val title: String
}

enum class SettingListItem : SettingList {
    DARK_MODE {
        override val route: String
            get() = "dark_mode"

        override val title: String
            get() = "深色模式"
    },

    COLOR_SCHEME {
        override val route: String
            get() = "color_scheme"

        override val title: String
            get() = "主题壁纸动态配色(Android 12 +)"
    },

    FEEDBACK {
        override val route: String
            get() = "feedback"

        override val title: String
            get() = "反馈问题"
    },

    CONTACT_US {
        override val route: String
            get() = "contact_us"

        override val title: String
            get() = "联系我们"
    },

    SHARE {
        override val route: String
            get() = "share"

        override val title: String
            get() = "分享给朋友"
    },

    CLEAN_CACHE {
        override val route: String
            get() = "clean_cache"

        override val title: String
            get() = "清理缓存"
    },

    TERMS_OF_SERVICE {
        override val route: String
            get() = RouteName.TERMS_OF_SERVICE

        override val title: String
            get() = "使用条款"
    }
}