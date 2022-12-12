package com.eternaljust.msea.ui.page.profile.setting

import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eternaljust.msea.utils.RouteName
import com.eternaljust.msea.utils.SettingInfo
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalTime

class SettingViewModel : ViewModel() {
    val itemGroups: List<List<SettingListItem>>
        get() {
            val isDynamic = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            var themeList = mutableListOf(
                SettingListItem.DARK_MODE
            )
            if (isDynamic) {
                themeList.add(SettingListItem.COLOR_SCHEME)
            }
            themeList.add(SettingListItem.DAY_SIGN)
            return listOf(
                themeList,
                listOf(
                    SettingListItem.FEEDBACK,
                    SettingListItem.CONTACT_US,
                    SettingListItem.SHARE,
//                    SettingListItem.CLEAN_CACHE,
                ),
                listOf(
                    SettingListItem.TERMS_OF_SERVICE
                )
            )
        }

    val themeStyleItems: List<String>
        get() = listOf("自动", "浅色", "深色")

    var viewStates by mutableStateOf(SettingViewState())
        private set
    private val _viewEvents = Channel<SettingViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: SettingViewAction) {
        when (action) {
            is SettingViewAction.PopBack -> popBack()
            is SettingViewAction.UpdateTimePickerShow -> updateTimePickerShow(show = action.show)
            is SettingViewAction.UpdateContactUsShow-> updateContactUsShow(show = action.show)
            is SettingViewAction.UpdateDaysignChecked -> updateDaysignChecked(check = action.check)
            is SettingViewAction.UpdateDaysginTime -> updateDaysignTime(time = action.time)
            is SettingViewAction.UpdateColorSchemeChecked -> updateColorSchemeChecked(check = action.check)
            is SettingViewAction.UpdateThemeStyleIndex -> updateThemeStyleIndex(index = action.index)
        }
    }

    private fun popBack() {
        viewModelScope.launch {
            _viewEvents.send(SettingViewEvent.PopBack)
        }
    }

    private fun updateTimePickerShow(show: Boolean) {
        viewStates = viewStates.copy(isTimePickerShow = show)
    }

    private fun updateContactUsShow(show: Boolean) {
        viewStates = viewStates.copy(isContactUsShow = show)
    }

    private fun updateDaysignChecked(check: Boolean) {
        SettingInfo.instance.daysignSwitch = check
        viewStates = viewStates.copy(daysignChecked = check)
    }

    private fun updateDaysignTime(time: LocalTime) {
        SettingInfo.instance.daysignHour = time.hour
        SettingInfo.instance.daysignMinute = time.minute
        viewStates = viewStates.copy(daysignTime = time)
    }

    private fun updateColorSchemeChecked(check: Boolean) {
        SettingInfo.instance.colorScheme = check
        viewStates = viewStates.copy(colorSchemeChecked = check)
    }

    private fun updateThemeStyleIndex(index: Int) {
        SettingInfo.instance.themeStyle = index
        viewStates = viewStates.copy(themeStyleIndex = index)
    }
}

data class SettingViewState constructor(
    val isTimePickerShow: Boolean = false,
    val isContactUsShow: Boolean = false,
    val daysignChecked: Boolean = SettingInfo.instance.daysignSwitch,
    val daysignTime: LocalTime = LocalTime.of(
        SettingInfo.instance.daysignHour,
        SettingInfo.instance.daysignMinute
    ),
    val colorSchemeChecked: Boolean = SettingInfo.instance.colorScheme,
    val themeStyleIndex: Int = SettingInfo.instance.themeStyle
)

sealed class SettingViewEvent {
    object PopBack : SettingViewEvent()
}

sealed class SettingViewAction {
    object PopBack : SettingViewAction()

    data class UpdateTimePickerShow(val show: Boolean) : SettingViewAction()
    data class UpdateContactUsShow(val show: Boolean) : SettingViewAction()
    data class UpdateDaysignChecked(val check: Boolean) : SettingViewAction()
    data class UpdateDaysginTime(val time: LocalTime) : SettingViewAction()
    data class UpdateColorSchemeChecked(val check: Boolean) : SettingViewAction()
    data class UpdateThemeStyleIndex(val index: Int) : SettingViewAction()
}

interface SettingList {
    val route: String
    val title: String
}

enum class SettingListItem : SettingList {
    DAY_SIGN {
        override val route: String
            get() = "day_sign"

        override val title: String
            get() = "签到提醒"
    },

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

    TERMS_OF_SERVICE {
        override val route: String
            get() = RouteName.TERMS_OF_SERVICE

        override val title: String
            get() = "使用条款"
    }
}