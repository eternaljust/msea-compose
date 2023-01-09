package com.eternaljust.msea.ui.page.home.topic

import android.os.Parcelable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eternaljust.msea.ui.page.node.tag.TagItemModel
import com.eternaljust.msea.utils.HTMLURL
import com.eternaljust.msea.utils.NetworkUtil
import com.eternaljust.msea.utils.UserInfo
import kotlinx.parcelize.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.Instant

class TopicDetailViewModel : ViewModel() {
    private var tid = ""

    private var pageSize = 30
    var isFirstLoad = true
        private set

    var page = 1
        private set

    var viewStates by mutableStateOf(TopicDetailViewState())
        private set
    private val _viewEvents = Channel<TopicDetailViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: TopicDetailViewAction) {
        when (action) {
            is TopicDetailViewAction.PopBack -> popBack()
            is TopicDetailViewAction.Favorite -> favorite()
            is TopicDetailViewAction.Share -> share()
            is TopicDetailViewAction.SetTid -> tid = action.tid
            is TopicDetailViewAction.ShowPageNumberMenu -> showPageNumberMenu(action.isShow)
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
            is TopicDetailViewAction.Support -> support(action.action)
            is TopicDetailViewAction.RecommendAdd -> recommendAdd()
            is TopicDetailViewAction.RecommendSubtract -> recommendSubtract()
            is TopicDetailViewAction.GetReplyParam -> getReplyParam(
                action = action.action,
                username = action.username
            )
            is TopicDetailViewAction.Reply -> reply()
            is TopicDetailViewAction.LoadData -> {
                if (viewStates.pageLoadCompleted) {
                    isFirstLoad = false
                    page = 1
                    loadMoreData()
                }
            }
            is TopicDetailViewAction.LoadMoreData -> {
                if (page < viewStates.pageNumber) {
                    page += 1
                    loadMoreData()
                }
            }
            is TopicDetailViewAction.LoadPageNumber -> {
                viewStates = viewStates.copy(list = emptyList())
                if (action.page <= viewStates.pageNumber) {
                    page = action.page
                    loadMoreData()
                }
            }
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
                val count = viewStates.favoriteCount.toInt() + 1
                viewStates = viewStates.copy(favoriteCount = count.toString())
                _viewEvents.send(TopicDetailViewEvent.Message("收藏成功"))
            } else if (result.contains("已收藏")) {
                _viewEvents.send(TopicDetailViewEvent.Message("抱歉，您已收藏，请勿重复收藏"))
            } else {
                _viewEvents.send(TopicDetailViewEvent.Message("收藏失败，请稍后重试"))
            }
        }
    }

    private fun support(action: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val url = HTMLURL.BASE + "/$action"
            val document = NetworkUtil.getRequest(url)
            val result = document.html()
            if (result.contains("投票成功")) {
                _viewEvents.send(TopicDetailViewEvent.Message("投票成功"))
                _viewEvents.send(TopicDetailViewEvent.Refresh)
            } else if (result.contains("投过票了")) {
                _viewEvents.send(TopicDetailViewEvent.Message("您已经对此回帖投过票了"))
            } else {
                _viewEvents.send(TopicDetailViewEvent.Message("投票失败，请稍后重试"))
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

    private fun recommendAdd() {
        viewModelScope.launch(Dispatchers.IO) {
            if (UserInfo.instance.auth.isEmpty()) {
                _viewEvents.send(TopicDetailViewEvent.Login)
                return@launch
            }

            val url = HTMLURL.BASE + "/${viewStates.topic.recommendAdd}"
            val document = NetworkUtil.getRequest(url)
            val result = document.selectXpath("//div[@class='wp cl w']/div[@class='nfl']").text()
            if (result.contains("+")) {
                if (viewStates.recommendAddCount == "0") {
                    val count = viewStates.recommendAddCount.toInt() + 1
                    viewStates = viewStates.copy(recommendAddCount = count.toString())
                }
                _viewEvents.send(TopicDetailViewEvent.Message("已顶"))
            } else if (result.contains("已评价")) {
                _viewEvents.send(TopicDetailViewEvent.Message("您已评价过本主题"))
            } else {
                _viewEvents.send(TopicDetailViewEvent.Message("评价失败，请稍后重试"))
            }
        }
    }

    private fun recommendSubtract() {
        viewModelScope.launch(Dispatchers.IO) {
            if (UserInfo.instance.auth.isEmpty()) {
                _viewEvents.send(TopicDetailViewEvent.Login)
                return@launch
            }

            val url = HTMLURL.BASE + "/${viewStates.topic.recommendSubtract}"
            val document = NetworkUtil.getRequest(url)
            val result = document.selectXpath("//div[@class='wp cl w']/div[@class='nfl']").text()
            if (result.contains("-")) {
                _viewEvents.send(TopicDetailViewEvent.Message("已踩"))
            } else if (result.contains("已评价")) {
                _viewEvents.send(TopicDetailViewEvent.Message("您已评价过本主题"))
            } else {
                _viewEvents.send(TopicDetailViewEvent.Message("评价失败，请稍后重试"))
            }
        }
    }

    private fun getReplyParam(
        action: String,
        username: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val params = TopicReplyParamModel()
            params.username = username

            val url = HTMLURL.BASE + "/$action"
            val document = NetworkUtil.getRequest(url)
            val noticeauthor = document.selectXpath("//input[@name='noticeauthor']")
                .attr("value")
            if (noticeauthor.isNotEmpty()) {
                params.noticeauthor = noticeauthor
            }
            val noticeauthormsg = document.selectXpath("//input[@name='noticeauthormsg']")
                .attr("value")
            if (noticeauthormsg.isNotEmpty()) {
                params.noticeauthormsg = noticeauthormsg
            }
            val reppid = document.selectXpath("//input[@name='reppid']")
                .attr("value")
            if (reppid.isNotEmpty()) {
                params.reppid = reppid
            }
            val reppost = document.selectXpath("//input[@name='reppost']")
                .attr("value")
            if (reppost.isNotEmpty()) {
                params.reppost = reppost
            }
            val noticetrimstr = document.selectXpath("//input[@name='noticetrimstr']")
                .attr("value")
            if (noticetrimstr.isNotEmpty()) {
                params.noticetrimstr = noticetrimstr
            }
            val form = document.selectXpath("//form[@id='postform']")
                .attr("action")
            if (form.isNotEmpty()) {
                params.action = form
            }

            viewStates = viewStates.copy(replyParams = params)
            commentDialog(isShow = true)
        }
    }

    private fun reply() {
        viewModelScope.launch(Dispatchers.IO) {
            val time = Instant.now().epochSecond
            val url = HTMLURL.BASE + "/${viewStates.replyParams.action}"
            val message = NetworkUtil.urlEncode(viewStates.commentText)
            val msg = NetworkUtil.urlEncode(viewStates.replyParams.noticeauthormsg)
            val str = NetworkUtil.urlEncode(viewStates.replyParams.noticetrimstr)

            val encodedParams = mapOf(
                "message" to message,
                "noticeauthormsg" to msg,
                "noticetrimstr" to str
            )
            val params = mapOf(
                "posttime" to time.toString(),
                "reppid" to viewStates.replyParams.reppid,
                "reppost" to viewStates.replyParams.reppost,
                "noticeauthor" to viewStates.replyParams.noticeauthor,
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

    private fun showPageNumberMenu(isShow: Boolean) {
        viewStates = viewStates.copy(showPageNumberMenuExpanded = isShow)
    }

    private fun commentDialog(isShow: Boolean) {
        viewStates = viewStates.copy(showCommentDialog = isShow)
        if (!isShow) {
            viewStates = viewStates.copy(replyParams = TopicReplyParamModel())
        }
    }

    private fun commentChange(text: String) {
        viewStates = viewStates.copy(commentText = text)
    }

    private fun loadMoreData() {
        println("page---$page")
        viewStates = viewStates.copy(pageLoadCompleted = false)
        if (page == 1) {
            viewStates = viewStates.copy(isRefreshing = true)
        }
        var list = mutableListOf<TopicCommentModel>()

        viewModelScope.launch(Dispatchers.IO) {
            val url = HTMLURL.BASE + "/thread-$tid-${page}-1.html"
            val document = NetworkUtil.getRequest(url)
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
                val tags = mutableListOf<TagItemModel>()
                span.forEach {
                    val tag = TagItemModel()
                    val tagTitle = it.attr("title")
                    if (tagTitle.isNotEmpty()) {
                        tag.title = tagTitle
                    }
                    val id = it.attr("href")
                    if (id.contains("id=")) {
                        tag.tid = id.split("id=").last()
                    }

                    tags.add(tag)
                }
                topic.tags = tags
                val xpath = "//div[@class='pob cl']//a[@id='k_favorite']"
                val favorite = document.selectXpath(xpath)
                    .attr("href")
                if (favorite.contains("ac=favorite")) {
                    topic.favorite = favorite
                }
                val count = document.selectXpath("$xpath//span[@id='favoritenumber']").text()
                if (count.isNotEmpty()) {
                    viewStates = viewStates.copy(favoriteCount = count)
                }

                val pageNumber = document.selectXpath("//div[@class='pgs mtm mbm cl']//label/span").text()
                if (pageNumber.isNotEmpty()) {
                    val number = pageNumber.replace("/ ", "").replace(" 页", "")
                    viewStates = viewStates.copy(pageNumber = number.toInt())
                    println("pageNumber---${viewStates.pageNumber}")
                }

                val action = document.selectXpath("//div[@id='f_pst']/form")
                    .attr("action")
                if (action.isNotEmpty()) {
                    topic.action = action
                }
                val addXpath = "//div[@class='mtw mbm hm cl']/a[@id='recommend_add']"
                val add = document.selectXpath(addXpath).attr("href")
                if (add.isNotEmpty()) {
                    topic.recommendAdd = add
                }
                val addCount = document.selectXpath("${addXpath}//span[@id='recommendv_add']").text()
                if (addCount.isNotEmpty()) {
                    viewStates = viewStates.copy(recommendAddCount = addCount)
                }
                val subtractXpath = "//div[@class='mtw mbm hm cl']/a[@id='recommend_subtract']"
                val subtract = document.selectXpath(subtractXpath).attr("href")
                if (subtract.isNotEmpty()) {
                    topic.recommendSubtract = subtract
                }

                viewStates = viewStates.copy(topic = topic)
            }

            val node = document.selectXpath("//div/table[@class='plhin']/tbody")
            pageSize = if (node.count() > 30) {
                node.count()
            } else {
                30
            }

            node.forEach {
                val comment = TopicCommentModel()
                val avatar = it.selectXpath("tr/td[@class='pls']//div[@class='avatar']/a/img")
                    .attr("src")
                if (avatar.isNotEmpty()) {
                    comment.avatar = NetworkUtil.getAvatar(avatar)
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
                val reply = it.selectXpath("tr/td[@class='plc']//div[@class='pob cl']//a[@class='iconfont icon-message-fill']")
                    .attr("href")
                if (reply.contains("action=reply")) {
                    comment.reply = reply
                }
                val xpath = "tr/td[@class='plc']//div[@class='pob cl']//a[@class='iconfont icon-like-fill']"
                val support = it.selectXpath(xpath)
                    .attr("href")
                if (support.contains("do=support")) {
                    comment.support = support
                }
                val count = it.selectXpath("$xpath/span").text()
                if (count.isNotEmpty()) {
                    comment.supportCount = count
                }

                val td = it.selectXpath("tr/td[@class='plc']//td[@class='t_f']")
                val content = td.html()
                if (content.contains("font") || content.contains("strong")
                    || content.contains("color") || content.contains("quote")
                    || content.contains("</a>") || content.contains("<img")) {
                    if (content.contains("quote")) {
                        comment.isText = true
                        val xpath1 = "tr/td[@class='plc']//td[@class='t_f']/div[@class='quote']/blockquote"
                        val time1 = it.selectXpath("$xpath1//font[@color='#999999']").text()
                        println("time---$time1")
                        comment.blockquoteTime = time1
                        val text = it.selectXpath(xpath1).text()
                        comment.blockquoteContent = text.replace(time1, "").removePrefix(" ")
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
                    println("list.count()---${list.count()}")
                    if (viewStates.list.count() < 10 &&
                        list.count() == viewStates.list.count()) {
                        if (page == 1 && viewStates.isRefreshing) {
                            viewStates = viewStates.copy(list = emptyList())
                        }
                    } else if (list.count() == 10) {
                        if (page == 1 && viewStates.isRefreshing) {
                            viewStates = viewStates.copy(list = emptyList())
                        }
                        viewStates = viewStates.copy(
                            list = viewStates.list + list,
                            isRefreshing = false
                        )
                        list = mutableListOf()
                    }
                    // 列表最后一个
                    node.last()?.let { last ->
                        if (last == it) {
                            viewStates = viewStates.copy(
                                list = viewStates.list + list,
                                isRefreshing = false,
                                pageLoadCompleted = true
                            )
                        }
                    }
                }
            }
        }
    }
}

data class TopicDetailViewState(
    val topic: TopicDetailModel = TopicDetailModel(),
    val list: List<TopicCommentModel> = emptyList(),
    val isRefreshing: Boolean = false,
    val pageNumber: Int = 1,
    val showPageNumberMenuExpanded: Boolean = false,
    val pageLoadCompleted: Boolean = true,
    val showCommentDialog: Boolean = false,
    val commentText: String = "",
    var favoriteCount: String = "",
    var recommendAddCount: String = "",
    var replyParams: TopicReplyParamModel = TopicReplyParamModel()
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
    object LoadData : TopicDetailViewAction()
    object LoadMoreData : TopicDetailViewAction()
    object Favorite : TopicDetailViewAction()
    object Share : TopicDetailViewAction()
    object Comment : TopicDetailViewAction()
    object RecommendAdd : TopicDetailViewAction()
    object RecommendSubtract : TopicDetailViewAction()
    object Reply : TopicDetailViewAction()

    data class SetTid(val tid: String) : TopicDetailViewAction()
    data class CommentShowDialog(val isShow: Boolean) : TopicDetailViewAction()
    data class CommentTextChange(val text: String) : TopicDetailViewAction()
    data class Support(val action: String) : TopicDetailViewAction()
    data class GetReplyParam(val action: String, val username: String) : TopicDetailViewAction()
    data class ShowPageNumberMenu(val isShow: Boolean) : TopicDetailViewAction()
    data class LoadPageNumber(val page: Int) : TopicDetailViewAction()
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
    var recommendAdd = ""
    var recommendSubtract = ""
    var tags: List<TagItemModel> = emptyList()
}

class TopicCommentModel {
    var uid = ""
    var reply = ""
    var support = ""
    var supportCount = ""
    var name = ""
    var avatar = ""
    var time = ""
    var content = ""
    var isText = true
    var blockquoteTime = ""
    var blockquoteContent = ""
    var sup = ""
}

class TopicReplyParamModel {
    var noticeauthor = ""
    var noticetrimstr = ""
    var noticeauthormsg = ""
    var reppid = ""
    var reppost = ""
    var username = ""
    var action = ""
}

@Parcelize
data class TopicDetailRouteModel(
    var tid: String = "",
    var isNodeFid125: Boolean = false
) : Parcelable