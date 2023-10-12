package com.eternaljust.msea.ui.page.notice.interactive

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.eternaljust.msea.utils.HTMLURL
import com.eternaljust.msea.utils.NetworkUtil
import com.eternaljust.msea.utils.configPager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class InteractiveViewModel : ViewModel() {
    private val pager by lazy {
        configPager(PagingConfig(pageSize = 30, prefetchDistance = 1)) {
            loadData(page = it)
        }
    }

    var viewStates by mutableStateOf(InteractiveFriendListViewState(pagingData = pager))
        private set

    private suspend fun loadData(page: Int) : List<InteractiveFriendListModel> {
        val list = mutableListOf<InteractiveFriendListModel>()

        withContext(Dispatchers.IO) {
            val url = HTMLURL.INTERACTIVE_LIST + "&page=${page}"
            val document = NetworkUtil.getRequest(url)
            val dl = document.selectXpath("//div[@class='nts']/dl")

            dl.forEach {
                val friend = InteractiveFriendListModel()
                val time = it.selectXpath("dt/span[@class='xg1 xw0']").text()
                if (time.isNotEmpty()) {
                    friend.time = time
                }
                val avatar = it.selectXpath("dd[@class='m avt mbn']/a/img").attr("src")
                if (avatar.isNotEmpty()) {
                    friend.avatar = NetworkUtil.getAvatar(avatar)
                }
                val name = it.selectXpath("dd[@class='ntc_body']/a[1]").text()
                if (name.isNotEmpty()) {
                    friend.name = name
                }
                val href = it.selectXpath("dd[@class='ntc_body']/a[1]").attr("href")
                if (href.isNotEmpty()) {
                    friend.uid = NetworkUtil.getUid(href)
                }
                val action = it.selectXpath("dd[@class='ntc_body']/a[2]").text()
                if (action.isNotEmpty()) {
                    friend.action = action
                }
                val text = it.selectXpath("dd[@class='ntc_body']/a[2]").attr("href")
                if (text.isNotEmpty()) {
                    friend.actionURL = text
                }
                var content = it.selectXpath("dd[@class='ntc_body']").text()
                if (content.isNotEmpty()) {
                    content = content.replace(friend.name, "")
                    content = content.replace(friend.action, "")
                    content = content.replace("\r\n", "")
                    friend.content = content
                }

                list.add(friend)
            }
        }

        return list
    }
}

data class InteractiveFriendListViewState(
    val pagingData: Flow<PagingData<InteractiveFriendListModel>>
)

class InteractiveFriendListModel {
    val uuid = UUID.randomUUID()
    var uid = ""
    var avatar = ""
    var name = ""
    var time = ""
    var content = ""
    var action = ""
    var actionURL = ""
}
