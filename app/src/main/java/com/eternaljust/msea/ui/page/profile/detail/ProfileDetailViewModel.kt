package com.eternaljust.msea.ui.page.profile.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.eternaljust.msea.utils.HTMLURL
import com.eternaljust.msea.utils.NetworkUtil
import com.eternaljust.msea.utils.configPager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileDetailViewModel : ViewModel() {
    private var uid = ""
    private var username = ""

    var viewStates by mutableStateOf(ProfileDetailViewState())
        private set
    private val _viewEvents = Channel<ProfileDetailViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    val profileItems: List<ProfileDetailTabItem>
        get() = listOf(
            ProfileDetailTabItem.TOPIC,
            ProfileDetailTabItem.FRIEND
        )

    fun dispatch(action: ProfileDetailViewAction) {
        when (action) {
            is ProfileDetailViewAction.PopBack -> popBack()
            is ProfileDetailViewAction.SetUid -> {
                uid = action.uid
                if (uid.isNotEmpty()) {
                    getProfile()
                }
            }
            is ProfileDetailViewAction.SetUsername -> {
                username = action.username
                if (username.isNotEmpty()) {
                    getProfile()
                }
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
            var url = HTMLURL.PROFILE + uid
            if (username.isNotEmpty()) {
                url = HTMLURL.BASE + "/space-username-${username}.html"
            }
            val document = NetworkUtil.getRequest(url)
            val text = document.selectXpath("//div[@id='messagetext']/p[1]").text()
            if (text.isNotEmpty()) {
                _viewEvents.send(ProfileDetailViewEvent.Message(text))
                return@launch
            }
            val profile = ProfileDetailModel()
            if (uid.isNotEmpty()) {
                profile.uid = uid
            } else {
                val id = document.selectXpath("//div[@class='h cl']//a").attr("href")
                if (id.contains("uid-")) {
                    profile.uid = NetworkUtil.getUid(id)
                    println("profile.uid = ${profile.uid }")
                }
            }
            val src = document.selectXpath("//div[@class='h cl']//img").attr("src")
            if (src.isNotEmpty()) {
                val avatar = src.replace("&size=small", "" )
                println("avatar=$avatar")
                profile.avatar = NetworkUtil.getAvatar(avatar)
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
                    var group = it.selectXpath("em[@class='xg1']").text()
                    group = group.replace(" ", "")
                    println("group=$group")
                    if (group.isNotEmpty()) {
                        var lv = ""
                        val text1 = it.selectXpath("span[@class='xi2']/a").text()
                        if (text1.isNotEmpty()) {
                            lv = text1
                        } else {
                            val text2 = it.text()
                            if (text2.isNotEmpty()) {
                                lv = text2.replace(group, "")
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

    data class Message(val message: String) : ProfileDetailViewEvent()
}

sealed class ProfileDetailViewAction {
    object PopBack : ProfileDetailViewAction()
    object GetProfile : ProfileDetailViewAction()

    data class SetUid(val uid: String) : ProfileDetailViewAction()
    data class SetUsername(val username: String) : ProfileDetailViewAction()
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

interface ProfileDetailTab {
    val id: String
    val title: String
}

enum class ProfileDetailTabItem : ProfileDetailTab {
    TOPIC {
        override val id: String
            get() = "topic"

        override val title: String
            get() = "主题"
    },

    FRIEND {
        override val id: String
            get() = "friend"

        override val title: String
            get() = "好友"
    }
}

class ProfileDetailFriendViewModel : ViewModel() {
    private var uid = ""

    private val pager by lazy {
        configPager(PagingConfig(pageSize = 30, prefetchDistance = 1)) {
            loadData(page = it)
        }
    }

    var viewStates by mutableStateOf(ProfileDetailFriendViewState(pagingData = pager))
        private set

    fun dispatch(action: ProfileDetailFriendViewAction) {
        when (action) {
            is ProfileDetailFriendViewAction.SetUid -> uid = action.uid
        }
    }

    private suspend fun loadData(page: Int) : List<ProfileDetailFriendModel> {
        val list = mutableListOf<ProfileDetailFriendModel>()

        withContext(Dispatchers.IO) {
            val url = HTMLURL.FRIEND_DETAIL_LIST + "&uid=$uid&page=${page}"
            val document = NetworkUtil.getRequest(url)
            val count = document.selectXpath("//div[@class='bm_c']/p/span[@class='xw1']").text()
            println("count = $count")
            if (count.isNotEmpty()) {
                viewStates = viewStates.copy(count = count)
            }
            val li = document.selectXpath("//ul[@class='buddy cl']/li[@class='bbda cl']")
            li.forEach {
                val friend = ProfileDetailFriendModel()
                val avatar = it.selectXpath("div[@class='avt']/a/img").attr("src")
                if (avatar.isNotEmpty()) {
                    friend.avatar = NetworkUtil.getAvatar(avatar)
                }
                val name = it.selectXpath("h4/a").text()
                if (name.isNotEmpty()) {
                    friend.name = name
                }
                val uid = it.selectXpath("h4/a").attr("href")
                if (uid.contains("uid-")) {
                    friend.uid = NetworkUtil.getUid(uid)
                }
                val content = it.selectXpath("p[@class='maxh']").text()
                if (content.isNotEmpty()) {
                    friend.content = content
                }
                println("friend.name--${friend.name}")

                list.add(friend)
            }
        }

        return list
    }
}

data class ProfileDetailFriendViewState(
    val pagingData: Flow<PagingData<ProfileDetailFriendModel>>,
    val privacyText: String = "",
    val count: String = ""
)

sealed class ProfileDetailFriendViewAction {
    data class SetUid(val uid: String) : ProfileDetailFriendViewAction()
}

class ProfileDetailFriendModel {
    var name = ""
    var uid = ""
    var avatar = ""
    var content = ""
}