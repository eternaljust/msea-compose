package com.eternaljust.msea.ui.page.home.topic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.eternaljust.msea.ui.page.node.tag.TagItemModel
import com.eternaljust.msea.utils.HTMLURL
import com.eternaljust.msea.utils.NetworkUtil
import com.eternaljust.msea.utils.configPager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TopicDetailViewModel : ViewModel() {
    private var tid = ""

    private val pager by lazy {
        configPager(PagingConfig(pageSize = 30, prefetchDistance = 1)) {
            loadData(page = it)
        }
    }

    var viewStates by mutableStateOf(TopicDetailViewState(pagingData = pager))
        private set
    private val _viewEvents = Channel<TopicDetailViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: TopicDetailViewAction) {
        when (action) {
            is TopicDetailViewAction.PopBack -> popBack()
            is TopicDetailViewAction.SetTid -> tid = action.tid
        }
    }

    private fun popBack() {
        viewModelScope.launch {
            _viewEvents.send(TopicDetailViewEvent.PopBack)
        }
    }

    private suspend fun loadData(page: Int) : List<TopicCommentModel> {
        val list = mutableListOf<TopicCommentModel>()

        withContext(Dispatchers.IO) {
            val url = HTMLURL.BASE + "/thread-$tid-${page}-1.html"
            val document = NetworkUtil.getRequest(url)
            withContext(Dispatchers.Default) {
                if (page == 1) {
                    val topic = TopicDetailModel()
                    val title = document.selectXpath("//td[@class='plc ptm pbn vwthd']/h1/span").text()
                    if (title.isNotEmpty()) {
                        topic.title = title
                    }
                    val text1 = document.selectXpath("//td[@class='plc ptm pbn vwthd']/div[@class='ptn']/span[2]").text()
                    val text2 = document.selectXpath("//td[@class='plc ptm pbn vwthd']/div[@class='ptn']/span[5]").text()
                    if (text1.isNotEmpty() && text2.isNotEmpty()) {
                        topic.commentCount = "查看: $text1  |  回复: $text2  |  tid($tid)"
                    }
                    val title1 = document.selectXpath("//div[@class='bm cl']/div[@class='z']/a[3]").text()
                    if (title1.isNotEmpty()) {
                        topic.indexTitle = title1
                    }
                    val gid = document.selectXpath("//div[@class='bm cl']/div[@class='z']/a[3]").attr("href")
                    if (gid.contains("gid=")) {
                        topic.gid = gid.split("gid=").last()
                    }
                    val title2 = document.selectXpath("//div[@class='bm cl']/div[@class='z']/a[4]").text()
                    if (title2.isNotEmpty()) {
                        topic.nodeTitle = title2
                    }
                    val fid = document.selectXpath("//div[@class='bm cl']/div[@class='z']/a[4]").attr("href")
                    if (fid.contains("forum-")) {
                        topic.nodeFid = fid.split("forum-").last().split("-").first()
                    } else if (fid.contains("fid=")) {
                        topic.nodeFid = fid.split("fid=").last()
                    }

                    viewStates = viewStates.copy(topic = topic)
                }
                val node = document.selectXpath("//table[@class='plhin']")
                node.forEach {
                    var comment = TopicCommentModel()

                    val avatar = it.selectXpath("tr/td[@class='icn']/a/img").attr("src")
                    if (avatar.isNotEmpty()) {
                        comment.avatar = HTMLURL.BASE + "/" + avatar
                    }
                    val name = it.selectXpath("tr/td[@class='by']/cite/a").text()
                    if (name.isNotEmpty()) {
                        comment.name = name
                    }
                    val uid = it.selectXpath("tr/td[@class='by']/cite/a").attr("href")
                    if (uid.contains("uid-")) {
                        comment.uid = NetworkUtil.getUid(uid)
                    }
                    val time = it.selectXpath("tr/td[@class='by']/em/a").text()
                    if (time.isNotEmpty()) {
                        comment.time = time
                    }
                    val reply = it.selectXpath("tr/td[@class='num']/a").text()
                    if (reply.isNotEmpty()) {
                        comment.reply = reply
                    }

                    list.add(comment)
                }
            }
        }

        return list
    }
}

data class TopicDetailViewState(
    val topic: TopicDetailModel = TopicDetailModel(),
    val pagingData: Flow<PagingData<TopicCommentModel>>
)

sealed class TopicDetailViewEvent {
    object PopBack : TopicDetailViewEvent()
}

sealed class TopicDetailViewAction {
    object PopBack : TopicDetailViewAction()

    data class SetTid(val tid: String) : TopicDetailViewAction()
}

class TopicDetailModel {
    var indexTitle = ""
    var gid = ""
    var nodeTitle = ""
    var nodeFid = ""
    var title = ""
    var commentCount = ""
    var tags: List<TagItemModel> = emptyList()
}

class TopicCommentModel {
    var uid = ""
    var pid = ""
    var reply = ""
    var favorite = ""
    var name = ""
    var avatar = ""
    var lv = ""
    var time = ""
    var content = ""
    var isText = true
    var webViewHeight: Float = 0f
}