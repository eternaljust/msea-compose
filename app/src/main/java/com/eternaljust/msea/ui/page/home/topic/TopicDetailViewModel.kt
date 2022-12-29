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
import com.eternaljust.msea.utils.UserInfo
import com.eternaljust.msea.utils.configPager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant

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
            is TopicDetailViewAction.Favorite -> favorite()
            is TopicDetailViewAction.Share -> share()
            is TopicDetailViewAction.SetTid -> tid = action.tid
            is TopicDetailViewAction.CommentShowDialog -> {
                viewModelScope.launch {
                    if (UserInfo.instance.auth.isEmpty()) {
                        _viewEvents.send(TopicDetailViewEvent.Login)
                        return@launch
                    } else {
                        commentDialog(action.isShow)
                    }
                }
            }
            is TopicDetailViewAction.CommentTextChange -> commentChange(action.text)
            is TopicDetailViewAction.Comment -> comment()
        }
    }

    private fun popBack() {
        viewModelScope.launch {
            _viewEvents.send(TopicDetailViewEvent.PopBack)
        }
    }

    private fun share() {
        viewModelScope.launch {
            _viewEvents.send(TopicDetailViewEvent.Share)
        }
    }

    private fun favorite() {
        viewModelScope.launch(Dispatchers.IO) {
            val url = HTMLURL.BASE + "/${viewStates.topic.favorite}"
            val document = NetworkUtil.getRequest(url)
            val result = document.html()
            if (result.contains("信息收藏成功")) {
                _viewEvents.send(TopicDetailViewEvent.Message("收藏成功"))
            } else if (result.contains("已收藏")) {
                _viewEvents.send(TopicDetailViewEvent.Message("抱歉，您已收藏，请勿重复收藏"))
            } else {
                _viewEvents.send(TopicDetailViewEvent.Message("收藏失败，请稍后重试"))
            }
        }
    }

    private fun comment() {
        viewModelScope.launch(Dispatchers.IO) {
            val time = Instant.now().epochSecond
            val url = HTMLURL.BASE + "/${viewStates.topic.action}"
            val message = NetworkUtil.urlEncode(viewStates.commentText)
            val encodedParams = mapOf(
                "message" to message
            )
            val params = mapOf(
                "posttime" to time.toString()
            )
            val document = NetworkUtil.postRequest(url, params, encodedParams)
            val result = document.html()
            if (result.isNotEmpty()) {
                _viewEvents.send(TopicDetailViewEvent.Message("评论成功"))
                _viewEvents.send(TopicDetailViewEvent.Refresh)
            } else {
                _viewEvents.send(TopicDetailViewEvent.Message("评论失败，请稍后重试"))
            }
            commentDialog(isShow = false)
        }
    }

    private fun commentDialog(isShow: Boolean) {
        viewStates = viewStates.copy(showCommentDialog = isShow)
    }

    private fun commentChange(text: String) {
        viewStates = viewStates.copy(commentText = text)
    }

    private suspend fun loadData(page: Int) : List<TopicCommentModel> {
        val list = mutableListOf<TopicCommentModel>()

        withContext(Dispatchers.IO) {
            val url = HTMLURL.BASE + "/thread-$tid-${page}-1.html"
            val document = NetworkUtil.getRequest(url)
            withContext(Dispatchers.Default) {
                if (page == 1) {
                    val topic = TopicDetailModel()
                    topic.url = url
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

                    val span = document.selectXpath("//span[@class='tag iconfont icon-tag-fill']/a")
                    var tags = mutableListOf<TagItemModel>()
                    span.forEach {
                        val tag = TagItemModel()
                        val title = it.attr("title")
                        if (title.isNotEmpty()) {
                            tag.title = title
                        }
                        val id = it.attr("href")
                        if (id.contains("id=")) {
                            tag.tid = id.split("id=").last()
                        }

                        tags.add(tag)
                    }
                    topic.tags = tags
                    val favorite = document.selectXpath("//div[@class='pob cl']//a[1]")
                        .attr("href")
                    if (favorite.contains("ac=favorite")) {
                        topic.favorite = favorite
                    }

                    val action = document.selectXpath("//div[@id='f_pst']/form")
                        .attr("action")
                    if (action.isNotEmpty()) {
                        topic.action = action
                    }

                    viewStates = viewStates.copy(topic = topic)
                }

                if (page > 1) {
                    val pageNumber = document.selectXpath("//div[@class='pgs mtm mbm cl']//label/span").text()
                    println("pageNumber---$pageNumber")
                    if (pageNumber.isNotEmpty()) {
                        val number = pageNumber.replace("/ ", "").replace(" 页", "")
                        if (page > number.toInt()) {
                            return@withContext list
                        }
                    } else {
                        return@withContext list
                    }
                }

                val node = document.selectXpath("//div/table[@class='plhin']/tbody")
                node.forEach { it ->
                    var comment = TopicCommentModel()
                    val avatar = it.selectXpath("tr/td[@class='pls']//div[@class='avatar']/a/img")
                        .attr("src")
                    if (avatar.isNotEmpty()) {
                        comment.avatar = avatar
                    }
                    val name = it.selectXpath("tr/td[@class='plc']//div[@class='authi']/a").text()
                    if (name.isNotEmpty()) {
                        comment.name = name
                    }
                    val uid = it.selectXpath("tr/td[@class='plc']//div[@class='authi']/a")
                        .attr("href")
                    if (uid.contains("uid-")) {
                        comment.uid = NetworkUtil.getUid(uid)
                    }
                    val time = it.selectXpath("tr/td[@class='plc']//div[@class='authi']/em")
                        .text()
                    if (time.isNotEmpty()) {
                        comment.time = time
                    }
                    val sup = it.selectXpath("tr/td[@class='plc']//div[@class='pi']/strong")
                        .text()
                    if (sup.isNotEmpty()) {
                        comment.sup = sup
                    }

                    var td = it.selectXpath("tr/td[@class='plc']//td[@class='t_f']")
                    val content = td.html()
                    if (content.contains("font") || content.contains("strong")
                        || content.contains("color") || content.contains("quote")
                        || content.contains("</a>") || content.contains("<img")) {
                        if (content.contains("quote")) {
                            comment.isText = true
                            val xpath = "tr/td[@class='plc']//td[@class='t_f']/div[@class='quote']/blockquote"
                            val time = it.selectXpath("$xpath//font[@color='#999999']").text()
                            println("time---$time")
                            comment.blockquoteTime = time
                            val text = it.selectXpath(xpath).text()
                            comment.blockquoteContent = text.replace(time, "").removePrefix(" ")
                            val quote = it.selectXpath("tr/td[@class='plc']//td[@class='t_f']").text()
                            if (quote.isNotEmpty()) {
                                comment.content = quote.replace(text, "").removePrefix(" ")
                            }
                        } else {
                            comment.content = it.selectXpath("tr/td[@class='plc']//div[@class='t_fsz']").html()
                            comment.isText = false
                        }
                        if (comment.content.contains("file=")) {
                            comment.content = comment.content.replace("file=", "src=")
                        } else if (comment.content.contains("src=")) {
                            comment.content = comment.content.replace("src=\"static/image/common/none.gif\"", "")
                        }
                    } else {
                        val text = td.text()
                        if (text.isNotEmpty()) {
                            comment.content = text
                        }
                    }
                    println("content---${comment.content}")

                    if (comment.name.isNotEmpty()) {
                        list.add(comment)
                    }
                }
            }
        }

        return list
    }
}

data class TopicDetailViewState(
    val topic: TopicDetailModel = TopicDetailModel(),
    val pagingData: Flow<PagingData<TopicCommentModel>>,
    val showCommentDialog: Boolean = false,
    val commentText: String = "",
)

sealed class TopicDetailViewEvent {
    object PopBack : TopicDetailViewEvent()
    object Share : TopicDetailViewEvent()
    object Refresh : TopicDetailViewEvent()
    object Login : TopicDetailViewEvent()

    data class Message(val message: String) : TopicDetailViewEvent()
}

sealed class TopicDetailViewAction {
    object PopBack : TopicDetailViewAction()
    object Favorite : TopicDetailViewAction()
    object Share : TopicDetailViewAction()
    object Comment : TopicDetailViewAction()

    data class SetTid(val tid: String) : TopicDetailViewAction()
    data class CommentShowDialog(val isShow: Boolean) : TopicDetailViewAction()
    data class CommentTextChange(val text: String) : TopicDetailViewAction()
}

class TopicDetailModel {
    var url = ""
    var indexTitle = ""
    var gid = ""
    var nodeTitle = ""
    var nodeFid = ""
    var title = ""
    var commentCount = ""
    var favorite = ""
    var action = ""
    var tags: List<TagItemModel> = emptyList()
}

class TopicCommentModel {
    var uid = ""
    var pid = ""
    var reply = ""
    var name = ""
    var avatar = ""
    var lv = ""
    var time = ""
    var content = ""
    var isText = true
    var blockquoteTime = ""
    var blockquoteContent = ""
    var sup = ""
}