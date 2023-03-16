package com.eternaljust.msea.ui.page.profile.drawer

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eternaljust.msea.BuildConfig
import com.eternaljust.msea.R
import com.eternaljust.msea.ui.page.profile.setting.ConfigVersionModel
import com.eternaljust.msea.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DrawerViewModel : ViewModel() {
    var viewStates by mutableStateOf(DrawerViewState())
        private set

    val profileItems: List<DrawerNavigationItem>
        get() = listOf(
            DrawerNavigationItem.Sign,
            DrawerNavigationItem.Topic,
            DrawerNavigationItem.Friend,
            DrawerNavigationItem.Favorite,
            DrawerNavigationItem.Credit,
//            DrawerNavigationItem.Group
        )

    val settingItems: List<DrawerNavigationItem>
        get() = listOf(
            DrawerNavigationItem.Setting,
            DrawerNavigationItem.About
        )

    val logoutItems: List<DrawerNavigationItem>
        get() = listOf(DrawerNavigationItem.Logout)

    fun dispatch(action: DrawerViewAction) {
        when (action) {
            is DrawerViewAction.Login -> login()
            is DrawerViewAction.GetProfile -> getProfile()
            is DrawerViewAction.GetVersion -> getVersion()
            is DrawerViewAction.LoadVersion -> loadVersion()
            is DrawerViewAction.LogoutDialog -> updaterLogoutDialog(action.show)
        }
    }

    private fun getProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            val url = HTMLURL.PROFILE + UserInfo.instance.uid
            val document = NetworkUtil.getRequest(url)
            val src = document.selectXpath("//div[@class='h cl']//img").attr("src")
            if (src.isNotEmpty()) {
                val avatar = NetworkUtil.getAvatar(src)
                println("avatar=$avatar")
                UserInfo.instance.avatar = avatar
            }

            val h2 = document.selectXpath("//div[@class='h cl']//h2").text()
            if (h2.isNotEmpty()) {
                val name = h2.replace("\n", "")
                println("name=$name")
                UserInfo.instance.name = name
            }

            val a1 = document.selectXpath("//ul[@class='cl bbda pbm mbm']//a[1]").text()
            if (a1.isNotEmpty() && a1.contains(" ")) {
                val friend = a1.split(" ")[1]
                println("friend=$friend")
                UserInfo.instance.friend = friend
            }

            val a2 = document.selectXpath("//ul[@class='cl bbda pbm mbm']//a[2]").text()
            if (a2.isNotEmpty() && a2.contains(" ")) {
                val reply = a2.split(" ")[1]
                println("reply=$reply")
                UserInfo.instance.reply = reply
            }

            val a3 = document.selectXpath("//ul[@class='cl bbda pbm mbm']//a[3]").text()
            if (a3.isNotEmpty() && a3.contains(" ")) {
                val topic = a3.split(" ")[1]
                println("topic=$topic")
                UserInfo.instance.topic = topic
            }

            val li2 = document.selectXpath("//div[@id='psts']/ul[@class='pf_l']/li[2]").text()
            if (li2.isNotEmpty()) {
                val integral = li2.replace("积分", "")
                println("integral=$integral")
                UserInfo.instance.integral = integral
            }

            val li3 = document.selectXpath("//div[@id='psts']/ul[@class='pf_l']/li[3]").text()
            if (li3.isNotEmpty()) {
                val bits = li3.replace("Bit", "")
                println("bits=$bits")
                UserInfo.instance.bits = bits
            }

            val li4 = document.selectXpath("//div[@id='psts']/ul[@class='pf_l']/li[4]").text()
            if (li4.isNotEmpty()) {
                val violation = li4.replace("违规", "")
                println("violation=$violation")
                UserInfo.instance.violation = violation
            }

            val x = "//div[@class='bm_c u_profile']/div[@class='pbm mbm bbda cl'][last()]/ul[1]/li"
            val li = document.selectXpath(x)
            if (li.isNotEmpty()) {
                val levels = mutableListOf<String>()
                li.forEach {
                    var group = it.selectXpath("em[@class='xg1']").text()
                    group = group.replace(" ", "")
                    println("group=$group")
                    if (group.isNotEmpty()) {
                        var lv = ""
                        val text = it.selectXpath("span[@class='xi2']/a").text()
                        if (text.isNotEmpty()) {
                            lv = text
                        } else {
                            val text1 = it.text()
                            if (text1.isNotEmpty()) {
                                lv = text1.replace(group, "")
                            }
                        }
                        lv = lv.replace(" ", "")
                        println("lv=$lv")
                        levels.add("$group($lv)")
                    }
                }
                if (levels.isNotEmpty()) {
                    val level = levels.joinToString(separator = " ")
                    println("level=$level")
                    UserInfo.instance.level = level
                }
            }

            val userInfo = UserInfo()
            userInfo.avatar = UserInfo.instance.avatar
            userInfo.name = UserInfo.instance.name
            userInfo.level = UserInfo.instance.level
            userInfo.friend = UserInfo.instance.friend
            userInfo.reply = UserInfo.instance.reply
            userInfo.topic = UserInfo.instance.topic
            userInfo.integral = UserInfo.instance.integral
            userInfo.bits = UserInfo.instance.bits
            userInfo.violation = UserInfo.instance.violation

            viewStates = viewStates.copy(userInfo = userInfo)
        }
    }


    private fun loadVersion() {
        viewModelScope.launch(Dispatchers.IO) {
            val url = HTMLURL.GET_VERSION
            val content = NetworkUtil.getData(url)
            println("ConfigVersion---$content")
            SettingInfo.instance.configVersion = content
            SettingInfo.instance.cycleCount = 0
            getVersion()
        }
    }
    private fun getVersion() {
        val version = SettingInfo.instance.configVersion.fromJson<ConfigVersionModel>()
        version?.let {
            val isNewVersion = BuildConfig.VERSION_CODE < version.versionCode
            viewStates = viewStates.copy(version = version, isNewVersion = isNewVersion)
        }
    }

    private fun login() {
        viewStates = viewStates.copy(isLogin = UserInfo.instance.auth.isNotEmpty())
    }

    private fun updaterLogoutDialog(show: Boolean) {
        viewStates = viewStates.copy(showLogout = show)
    }
}

data class DrawerViewState(
    val isLogin: Boolean = UserInfo.instance.auth.isNotEmpty(),
    val showLogout: Boolean = false,
    val userInfo: UserInfo = UserInfo.instance,
    val version: ConfigVersionModel = ConfigVersionModel(),
    val isNewVersion: Boolean = false
)

sealed class DrawerViewAction {
    object Login : DrawerViewAction()
    object GetProfile : DrawerViewAction()
    object GetVersion : DrawerViewAction()
    object LoadVersion : DrawerViewAction()

    data class LogoutDialog(val show: Boolean) : DrawerViewAction()
}

sealed class DrawerNavigationItem(
    val route: String,
    val title: String,
    val imageVector: ImageVector?,
    val painter: Int?
) {
    object Sign : DrawerNavigationItem(
        route = RouteName.SIGN,
        title = "签到",
        imageVector = null,
        painter = R.drawable.ic_baseline_energy_savings_leaf_24
    )

    object Topic : DrawerNavigationItem(
        route = RouteName.PROFILE_TOPIC,
        title = "主题",
        imageVector = null,
        painter = R.drawable.ic_baseline_topic_24
    )

    object Friend : DrawerNavigationItem(
        route = RouteName.PROFILE_FRIEND,
        title = "好友",
        imageVector = null,
        painter = R.drawable.ic_baseline_group_24
    )

    object Favorite : DrawerNavigationItem(
        route = RouteName.PROFILE_FAVORITE,
        title = "收藏",
        imageVector = Icons.Default.Favorite,
        painter = null
    )

    object Credit : DrawerNavigationItem(
        route = RouteName.PROFILE_CREDIT,
        title = "积分",
        imageVector = null,
        painter = R.drawable.ic_baseline_paid_24
    )

    object Group : DrawerNavigationItem(
        route = RouteName.PROFILE_GROUP,
        title = "用户组",
        imageVector = null,
        painter = R.drawable.ic_baseline_admin_panel_settings_24
    )

    object Setting : DrawerNavigationItem(
        route = RouteName.SETTING,
        title = "设置",
        imageVector = Icons.Default.Settings,
        painter = null
    )

    object About : DrawerNavigationItem(
        route = RouteName.ABOUT,
        title = "关于",
        imageVector = Icons.Default.Info,
        painter = null
    )

    object Logout : DrawerNavigationItem(
        route = RouteName.LOGOUT,
        title = "退出登录",
        imageVector = null,
        painter = R.drawable.ic_baseline_logout_24
    )
}