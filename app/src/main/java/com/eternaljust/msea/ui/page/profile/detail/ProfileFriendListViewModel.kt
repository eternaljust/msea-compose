package com.eternaljust.msea.ui.page.profile.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.eternaljust.msea.utils.HTMLURL
import com.eternaljust.msea.utils.NetworkUtil
import com.eternaljust.msea.utils.UserInfo
import com.eternaljust.msea.utils.configPager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class FriendListViewModel : ViewModel() {
    private val pager by lazy {
        configPager(PagingConfig(pageSize = 30, prefetchDistance = 1)) {
            loadData(page = it)
        }
    }

    var viewStates by mutableStateOf(FriendListViewState(pagingData = pager))
        private set

    private suspend fun loadData(page: Int) : List<FriendListModel> {
        val list = mutableListOf<FriendListModel>()

        withContext(Dispatchers.IO) {
            val url = HTMLURL.FRIEND_LIST + "&order=num&page=${page}"
            val document = NetworkUtil.getRequest(url)
            val count = document.selectXpath("//div[@class='tbmu cl']/p/span[@class='xw1']").text()
            if (count.isNotEmpty()) {
                viewStates = viewStates.copy(count = count)
            }
            val li = document.selectXpath("//ul[@class='buddy cl']/li")

            li.forEach {
                val friend = FriendListModel()
                val avatar = it.selectXpath("div[@class='avt']/a/img").attr("src")
                if (avatar.isNotEmpty()) {
                    friend.avatar = NetworkUtil.getAvatar(avatar)
                }
                val name = it.selectXpath("h4/a").text()
                if (name.isNotEmpty()) {
                    friend.name = name
                }
                val hot = it.selectXpath("h4/span[@class='xg1 xw0 y']").text()
                if (hot.isNotEmpty()) {
                    friend.hot = hot.replace("\n", "")
                }
                val uid = it.selectXpath("h4/a").attr("href")
                if (uid.contains("uid-")) {
                    friend.uid = NetworkUtil.getUid(uid)
                }
                val topic = it.selectXpath("p[@class='maxh']").text()
                if (topic.isNotEmpty()) {
                    friend.topic = topic
                }

                list.add(friend)
            }
        }

        return list
    }
}

data class FriendListViewState(
    val pagingData: Flow<PagingData<FriendListModel>>,
    val count: String = "0"
)

class FriendListModel {
    val uuid = UUID.randomUUID()
    var name = ""
    var uid = ""
    var avatar = ""
    var hot = ""
    var topic = ""
}

class FriendVisitorTraceListViewModel(
    val tabItem: ProfileFriendTabItem
) : ViewModel() {
    companion object {
        val visitor by lazy { FriendVisitorTraceListViewModel(tabItem = ProfileFriendTabItem.VISITOR) }
        val trace by lazy { FriendVisitorTraceListViewModel(tabItem = ProfileFriendTabItem.TRACE) }
    }

    private val pager by lazy {
        configPager(PagingConfig(pageSize = 30, prefetchDistance = 1)) {
            loadData(page = it)
        }
    }

    var viewStates by mutableStateOf(FriendVisitorTraceListViewState(pagingData = pager))
        private set

    private suspend fun loadData(page: Int) : List<FriendVisitorTraceListModel> {
        val list = mutableListOf<FriendVisitorTraceListModel>()

        withContext(Dispatchers.IO) {
            val url = HTMLURL.FRIEND_LIST + "&uid=${UserInfo.instance.uid}&view=${tabItem.id}&page=${page}"
            val document = NetworkUtil.getRequest(url)
            val li = document.selectXpath("//ul[@class='buddy cl']/li")

            li.forEach {
                val friend = FriendVisitorTraceListModel()
                val avatar = it.selectXpath("div[@class='avt']/a/img").attr("src")
                if (avatar.isNotEmpty()) {
                    friend.avatar = NetworkUtil.getAvatar(avatar)
                }
                val name = it.selectXpath("h4/a").text()
                if (name.isNotEmpty()) {
                    friend.name = name
                }
                val time = it.selectXpath("h4/span[@class='xg1 xw0 y']").text()
                if (time.isNotEmpty()) {
                    friend.time = time.replace("\n", "")
                }
                val uid = it.selectXpath("h4/a").attr("href")
                if (uid.contains("uid-")) {
                    friend.uid = NetworkUtil.getUid(uid)
                }
                val topic = it.selectXpath("p[@class='maxh']").text()
                if (topic.isNotEmpty()) {
                    friend.topic = topic
                }

                list.add(friend)
            }
        }

        return list
    }
}

data class FriendVisitorTraceListViewState(
    val pagingData: Flow<PagingData<FriendVisitorTraceListModel>>,
)

class FriendVisitorTraceListModel {
    val uuid = UUID.randomUUID()
    var name = ""
    var uid = ""
    var avatar = ""
    var time = ""
    var topic = ""
}