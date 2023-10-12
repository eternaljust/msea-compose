package com.eternaljust.msea.ui.page.node.tag

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
import java.util.UUID

class TagListViewModel : ViewModel() {
    private var tid: String = ""

    private val pager by lazy {
        configPager(PagingConfig(pageSize = 30, prefetchDistance = 1)) {
            loadData(tid = tid, page = it)
        }
    }

    var viewStates by mutableStateOf(TagListViewState(pagingData = pager))
        private set

    private val _viewEvents = Channel<TagListViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: TagListViewAction) {
        when (action) {
            is TagListViewAction.PopBack -> popBack()
            is TagListViewAction.SetTid -> tid = action.id
        }
    }

    private suspend fun loadData(tid: String, page: Int) : List<TagListModel> {
        val list = mutableListOf<TagListModel>()
        if (page > 1) {
            return list
        }

        withContext(Dispatchers.IO) {
            val url = HTMLURL.TAG_LIST + "&id=$tid"
            val document = NetworkUtil.getRequest(url)
            val tr = document.selectXpath("//div[@class='bm_c']/table/tbody/tr")
            tr.forEach {
                val tag = TagListModel()

                val gif = it.selectXpath("td[@class='icn']/a/img").attr("src")
                if (gif.isNotEmpty()) {
                    tag.gif = HTMLURL.BASE + "/${gif}"
                }
                val title = it.selectXpath("th/a").text()
                if (title.isNotEmpty()) {
                    tag.title = title
                }
                val id = it.selectXpath("th/a").attr("href")
                if (id.isNotEmpty()) {
                    tag.tid = id.split("thread-").last().split("-").first()
                }
                val forum = it.selectXpath("td[@class='by']/a").text()
                if (forum.isNotEmpty()) {
                    tag.forum = forum
                }
                val fid = it.selectXpath("td[@class='by']/a").attr("href")
                println("fid---$fid")
                if (fid.isNotEmpty()) {
                    tag.fid = NetworkUtil.getFid(fid)
                }
                val name = it.selectXpath("td[@class='by'][2]/cite/a").text()
                if (name.isNotEmpty()) {
                    tag.name = name
                }
                val uid = it.selectXpath("td[@class='by'][2]/cite/a").attr("href")
                if (uid.isNotEmpty()) {
                    tag.uid = uid
                }
                val time = it.selectXpath("td[@class='by']/em/span").text()
                if (time.isNotEmpty()) {
                    tag.time = time
                }
                val reply = it.selectXpath("td[@class='num']/a").text()
                if (reply.isNotEmpty()) {
                    tag.reply = reply
                }
                val examine = it.selectXpath("td[@class='num']/em").text()
                if (examine.isNotEmpty()) {
                    tag.examine = examine
                }
                val lastName = it.selectXpath("td[@class='by'][last()]/cite/a").text()
                if (lastName.isNotEmpty()) {
                    tag.lastName = lastName
                    tag.lastUserName = "space-username-${lastName}.html"
                }
                val lastTime = it.selectXpath("td[@class='by'][last()]/em/a").text()
                if (lastTime.isNotEmpty()) {
                    tag.lastTime = lastTime
                }

                list.add(tag)
            }
        }

        return list
    }

    private fun popBack() {
        viewModelScope.launch {
            _viewEvents.send(TagListViewEvent.PopBack)
        }
    }
}

data class TagListViewState(
    val pagingData: Flow<PagingData<TagListModel>>
)

sealed class TagListViewEvent {
    object PopBack : TagListViewEvent()
}

sealed class TagListViewAction {
    object PopBack: TagListViewAction()

    data class SetTid(val id: String) : TagListViewAction()
}

class TagListModel {
    val uuid = UUID.randomUUID()
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
     * 帖子作者
     */
    var name = ""
    /**
     * 帖子作者 id
     */
    var uid = ""
    /**
     * 帖子发表时间
     */
    var time = ""
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
     * 最后发表的用户
     */
    var lastUserName = ""
    /**
     * 最后发表的时间
     */
    var lastTime = ""
}
