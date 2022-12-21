package com.eternaljust.msea.ui.page.profile.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eternaljust.msea.utils.HTMLURL
import com.eternaljust.msea.utils.NetworkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ProfileDetailViewModel : ViewModel() {
    private var uid = ""

    var viewStates by mutableStateOf(ProfileDetailViewState())
        private set
    private val _viewEvents = Channel<ProfileDetailViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: ProfileDetailViewAction) {
        when (action) {
            is ProfileDetailViewAction.PopBack -> popBack()
            is ProfileDetailViewAction.SetUid -> {
                uid = action.uid
                getProfile()
            }
            is ProfileDetailViewAction.GetProfile -> getProfile()
        }
    }

    private fun popBack() {
        viewModelScope.launch {
            _viewEvents.send(ProfileDetailViewEvent.PopBack)
        }
    }

    private fun getProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            val url = HTMLURL.PROFILE + uid
            val document = NetworkUtil.getRequest(url)
            val profile = ProfileDetailModel()
            val src = document.selectXpath("//div[@class='h cl']//img").attr("src")
            if (src.isNotEmpty()) {
                val avatar = src.replace("&size=small", "" )
                println("avatar=$avatar")
                profile.avatar = avatar
            }

            val h2 = document.selectXpath("//div[@class='h cl']//h2").text()
            if (h2.isNotEmpty()) {
                val name = h2.replace("\n", "")
                println("name=$name")
                profile.name = name
            }

            val a1 = document.selectXpath("//ul[@class='cl bbda pbm mbm']//a[1]").text()
            if (a1.isNotEmpty() && a1.contains(" ")) {
                val friend = a1.split(" ")[1]
                println("friend=$friend")
                profile.friend = friend
            }

            val a2 = document.selectXpath("//ul[@class='cl bbda pbm mbm']//a[2]").text()
            if (a2.isNotEmpty() && a2.contains(" ")) {
                val reply = a2.split(" ")[1]
                println("reply=$reply")
                profile.reply = reply
            }

            val a3 = document.selectXpath("//ul[@class='cl bbda pbm mbm']//a[3]").text()
            if (a3.isNotEmpty() && a3.contains(" ")) {
                val topic = a3.split(" ")[1]
                println("topic=$topic")
                profile.topic = topic
            }

            val li2 = document.selectXpath("//div[@id='psts']/ul[@class='pf_l']/li[2]").text()
            if (li2.isNotEmpty()) {
                val integral = li2.replace("积分", "")
                println("integral=$integral")
                profile.integral = integral
            }

            val li3 = document.selectXpath("//div[@id='psts']/ul[@class='pf_l']/li[3]").text()
            if (li3.isNotEmpty()) {
                val bits = li3.replace("Bit", "")
                println("bits=$bits")
                profile.bits = bits
            }

            val li4 = document.selectXpath("//div[@id='psts']/ul[@class='pf_l']/li[4]").text()
            if (li4.isNotEmpty()) {
                val violation = li4.replace("违规", "")
                println("violation=$violation")
                profile.violation = violation
            }

            val x = "//div[@class='bm_c u_profile']/div[@class='pbm mbm bbda cl'][last()]/ul[1]/li"
            val li = document.selectXpath(x)
            if (li.isNotEmpty()) {
                val levels = mutableListOf<String>()
                li.forEach {
                    var name = it.selectXpath("//em[@class='xg1']").text()
                    name = name.trim()
                    println("name=$name")
                    if (name.isNotEmpty()) {
                        var lv = ""
                        val text = it.selectXpath("/span/a").text()
                        if (text.isNotEmpty()) {
                            lv = text.trim()
                        } else {
                            val text1 = it.text()
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
                    profile.level = level
                }
            }

            viewStates = viewStates.copy(profile = profile)
        }
    }
}

data class ProfileDetailViewState(
    val profile: ProfileDetailModel = ProfileDetailModel()
)

sealed class ProfileDetailViewEvent {
    object PopBack : ProfileDetailViewEvent()
}

sealed class ProfileDetailViewAction {
    object PopBack : ProfileDetailViewAction()
    object GetProfile : ProfileDetailViewAction()

    data class SetUid(val uid: String) : ProfileDetailViewAction()
}

class ProfileDetailModel {
    var uid: String = ""
    var name: String = ""
    var level: String = ""
    var avatar: String = ""
    var friend: String = ""
    var reply: String = ""
    var topic: String = ""
    var integral: String = ""
    var bits: String = ""
    var violation: String = ""
}