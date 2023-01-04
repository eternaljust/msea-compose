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

class ProfileTopicViewModel : ViewModel() {
    private var uid = ""

    private val pager by lazy {
        configPager(PagingConfig(pageSize = 30, prefetchDistance = 1)) {
            loadData(page = it)
        }
    }

    var viewStates by mutableStateOf(ProfileTopicViewState(pagingData = pager))
        private set

    private val _viewEvents = Channel<ProfileTopicViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: ProfileTopicViewAction) {
        when (action) {
            is ProfileTopicViewAction.PopBack -> popBack()
            is ProfileTopicViewAction.SetUid -> uid = action.uid
        }
    }

    private suspend fun loadData(page: Int) : List<ProfileTopicListModel> {
        val list = mutableListOf<ProfileTopicListModel>()

        withContext(Dispatchers.IO) {
            val url = HTMLURL.PROFILE_TOPIC_LIST + "&uid=$uid&page=${page}"
            val document = NetworkUtil.getRequest(url)
            val tr = document.selectXpath("//div[@class='bm_c']//table/tbody/tr")
            tr.forEach {
                val topic = ProfileTopicListModel()

                val gif = it.selectXpath("td[@class='icn']/a/img").attr("src")
                if (gif.isNotEmpty()) {
                    topic.gif = HTMLURL.BASE + "/${gif}"
                }
                val title = it.selectXpath("th/a").text()
                if (title.isNotEmpty()) {
                    topic.title = title
                }
                val tid = it.selectXpath("th/a").attr("href")
                if (tid.contains("thread-")) {
                    topic.tid = tid.split("thread-").last().split("-").first()
                }
                val forum = it.selectXpath("td/a[@class='xg1']").text()
                if (forum.isNotEmpty()) {
                    topic.forum = forum
                }
                val fid = it.selectXpath("td/a[@class='xg1']").attr("href")
                if (fid.isNotEmpty()) {
                    topic.fid = NetworkUtil.getFid(fid)
                }
                val reply = it.selectXpath("td[@class='num']/a").text()
                if (reply.isNotEmpty()) {
                    topic.reply = reply
                }
                val examine = it.selectXpath("td[@class='num']/em").text()
                if (examine.isNotEmpty()) {
                    topic.examine = examine
                }
                val lastName = it.selectXpath("td[@class='by'][last()]/cite/a").text()
                if (lastName.isNotEmpty()) {
                    topic.lastName = lastName
                }
                val lastTime = it.selectXpath("td[@class='by'][last()]/em/a").text()
                if (lastTime.isNotEmpty()) {
                    topic.lastTime = lastTime
                }

                if (topic.lastName.isNotEmpty()) {
                    list.add(topic)
                }
            }
        }

        return list
    }

    private fun popBack() {
        viewModelScope.launch {
            _viewEvents.send(ProfileTopicViewEvent.PopBack)
        }
    }
}

data class ProfileTopicViewState(
    val pagingData: Flow<PagingData<ProfileTopicListModel>>
)

sealed class ProfileTopicViewEvent {
    object PopBack : ProfileTopicViewEvent()
}

sealed class ProfileTopicViewAction {
    object PopBack: ProfileTopicViewAction()

    data class SetUid(val uid: String) : ProfileTopicViewAction()
}

class ProfileTopicListModel {
    /**
     * 帖子标题
     */
    var title = ""
    /**
     * 帖子链接 id
     */
    var tid = ""
    /**
     * 帖子动图
     */
    var gif = ""
    /**
     * 帖子板块
     */
    var forum = ""
    /**
     * 帖子板块 id
     */
    var fid = ""
    /**
     * 查看
     */
    var examine = ""
    /**
     * 回复
     */
    var reply = ""
    /**
     * 最后发表的昵称
     */
    var lastName = ""
    /**
     * 最后发表的时间
     */
    var lastTime = ""
}
