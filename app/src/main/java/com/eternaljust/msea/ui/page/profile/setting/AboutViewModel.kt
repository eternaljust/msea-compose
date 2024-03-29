package com.eternaljust.msea.ui.page.profile.setting

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eternaljust.msea.BuildConfig
import com.eternaljust.msea.ui.data.ConfigVersionModel
import com.eternaljust.msea.utils.*
import com.umeng.cconfig.UMRemoteConfig
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AboutViewModel : ViewModel() {
    val items: List<AboutListItem>
        get() = listOf(
            AboutListItem.LICENSE,
            AboutListItem.SDK_LIST,
            AboutListItem.SOURCE_CODE,
            AboutListItem.UPDATE_VERSION
        )
    val versionName: String = BuildConfig.VERSION_NAME
    val versionCode: Int = BuildConfig.VERSION_CODE

    var viewStates by mutableStateOf(AboutViewStates())
        private set
    private val _viewEvents = Channel<AboutViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: AboutViewAction) {
        when (action) {
            is AboutViewAction.PopBack -> popBack()
            is AboutViewAction.GetVersion -> getVersion()
            is AboutViewAction.VersionShowDialog -> versionShowDialog(action.isShow)
        }
    }

    private fun popBack() {
        viewModelScope.launch {
            _viewEvents.send(AboutViewEvent.PopBack)
        }
    }

    private fun getVersion() {
        val configVersion = UMRemoteConfig.getInstance().getConfigValue("config_version")
        println("config_version $configVersion")
        val version = configVersion.fromJson<ConfigVersionModel>()
        version?.let {
            viewStates = viewStates.copy(configVersion = it)
        }
    }

    private fun versionShowDialog(isShow: Boolean) {
        viewStates = viewStates.copy(versionShowDialog = isShow)
    }
}

data class AboutViewStates(
    var configVersion: ConfigVersionModel = ConfigVersionModel(),
    var versionShowDialog: Boolean = false
)

sealed class AboutViewEvent {
    object PopBack : AboutViewEvent()
}

sealed class AboutViewAction {
    object PopBack: AboutViewAction()
    object GetVersion: AboutViewAction()

    data class VersionShowDialog(val isShow: Boolean) : AboutViewAction()
}

interface AboutList {
    val route: String
    val title: String
}

enum class AboutListItem : AboutList {
    LICENSE {
        override val route: String
            get() = RouteName.LICENSE

        override val title: String
            get() = "开源协议"
    },

    SOURCE_CODE {
        override val route: String
            get() = RouteName.SOURCE_CODE

        override val title: String
            get() = "源代码"
    },

    SDK_LIST {
        override val route: String
            get() = RouteName.SDK_LIST

        override val title: String
            get() = "SDK 目录"
    },

    UPDATE_VERSION {
        override val route: String
            get() = RouteName.UPDATE_VERSION

        override val title: String
            get() = "版本更新"
    }
}
