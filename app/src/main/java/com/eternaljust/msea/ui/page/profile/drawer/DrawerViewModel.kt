package com.eternaljust.msea.ui.page.profile.drawer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eternaljust.msea.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DrawerViewModel : ViewModel() {
    var viewStates by mutableStateOf(DrawerViewState())
        private set

    val profileItems: List<DrawerNavigationItem>
        get() = listOf(
            DrawerNavigationItem.Topic,
            DrawerNavigationItem.Friend,
            DrawerNavigationItem.Favorite,
            DrawerNavigationItem.Credit,
            DrawerNavigationItem.Group
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
            is DrawerViewAction.LogoutDialog -> updaterLogoutDialog(action.show)
        }
    }

    private fun getProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            val url = HTMLURL.PROFILE + UserInfo.instance.uid
            val document = NetworkUtil.getRequest(url)
            val src = document.selectXpath("//div[@class='h cl']//img").attr("src")
            if (src.isNotEmpty()) {
                val avatar = src.replace("&size=small", "" )
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
                var levels = mutableListOf<String>()
                li.forEach {
                    var name = it.selectXpath("//em[@class='xg1']").text()
                    name = name.trim()
                    println("name=$name")
                    if (name.isNotEmpty()) {
                        var lv = ""
                        val text = it.selectXpath("/span/a").text()
                        println("text=$text")
                        if (text.isNotEmpty()) {
                            lv = text.trim()
                        } else {
                            val text1 = it.text()
                            println("text1=$text1")
                            if (text1.isNotEmpty()) {
                                lv = text1.replace(name, "").trim()
                            }
                        }
                        println("lv=$lv")
                        levels.add("$name($lv)")
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

    private fun login() {
        viewStates = viewStates.copy(isLogin = UserInfo.instance.isLogin)
    }

    private fun updaterLogoutDialog(show: Boolean) {
        viewStates = viewStates.copy(showLogout = show)
    }
}

data class DrawerViewState(
    val isLogin: Boolean = UserInfo.instance.isLogin,
    val showLogout: Boolean = false,
    val userInfo: UserInfo = UserInfo.instance
)

sealed class DrawerViewAction {
    object Login : DrawerViewAction()
    object GetProfile : DrawerViewAction()

    data class LogoutDialog(val show: Boolean) : DrawerViewAction()
}