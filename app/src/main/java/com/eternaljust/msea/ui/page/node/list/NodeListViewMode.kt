package com.eternaljust.msea.ui.page.node.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.eternaljust.msea.ui.page.home.topic.TopicListModel
import com.eternaljust.msea.ui.page.home.topic.getIcon
import com.eternaljust.msea.utils.HTMLURL
import com.eternaljust.msea.utils.NetworkUtil
import com.eternaljust.msea.utils.configPager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NodeListViewModel : ViewModel() {
    /*
    石沉大海
     */
    val isNodeFid125: Boolean
        get() = fid == "125"

    private var fid: String = ""

    private val pager by lazy {
        configPager(PagingConfig(pageSize = 18, prefetchDistance = 1)) {
            loadData(fid = fid, page = it)
        }
    }

    var viewStates by mutableStateOf(NodeListViewState(pagingData = pager))
        private set

    private val _viewEvents = Channel<NodeListViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: NodeListViewAction) {
        when (action) {
            is NodeListViewAction.PopBack -> popBack()
            is NodeListViewAction.SetFid -> fid = action.id
        }
    }

    private suspend fun loadData(fid: String, page: Int) : List<TopicListModel> {
        val list = mutableListOf<TopicListModel>()

        withContext(Dispatchers.IO) {
            val url = HTMLURL.BASE + "/forum-${fid}-${page}.html"
            val document = NetworkUtil.getRequest(url)
            val title = document.selectXpath("//meta[@name='keywords']").attr("content")
            if (title.isNotEmpty()) {
                viewStates = viewStates.copy(title = title)
            }
            val header = NodeListHeaderModel()
            val today = document.selectXpath("//h1[@class='xs2']/span[@class='xs1 xw0 i']/strong[1]").text()
            if (today.isNotEmpty()) {
                header.today = today
            }
            val topicText = document.selectXpath("//h1[@class='xs2']/span[@class='xs1 xw0 i']/strong[2]").text()
            if (topicText.isNotEmpty()) {
                header.topic = topicText
            }
            val rank = document.selectXpath("//h1[@class='xs2']/span[@class='xs1 xw0 i']/strong[3]").text()
            if (rank.isNotEmpty()) {
                header.rank = rank
            }
            val image1 = document.selectXpath("//h1[@class='xs2']/span[@class='xs1 xw0 i']/span[1]").attr("class")
            if (image1.isNotEmpty()) {
                if (image1.contains("paixu")) {
                    header.todayImage = "down"
                } else if (image1.contains("xiangshang")) {
                    header.todayImage = "up"
                }
            }
            val image2 = document.selectXpath("//h1[@class='xs2']/span[@class='xs1 xw0 i']/span[last()]").attr("class")
            if (image2.isNotEmpty()) {
                if (image2.contains("paixu")) {
                    header.rankImage = "down"
                } else if (image2.contains("xiangshang")) {
                    header.rankImage = "up"
                }
            }

            viewStates = viewStates.copy(header = header)
            withContext(Dispatchers.Default) {
                val tbody = document.selectXpath("//div[@id='threadlist']//table/tbody")
                tbody.forEach {
                    val topic = TopicListModel()

                    val avatar = it.selectXpath("tr/td[@class='icn']/a/img").attr("src")
                    if (avatar.isNotEmpty()) {
                        topic.avatar = NetworkUtil.getAvatar(avatar)
                    }
                    val name = it.selectXpath("tr/td[@class='by']/cite/a").text()
                    if (name.isNotEmpty()) {
                        topic.name = name
                    }
                    val uid = it.selectXpath("tr/td[@class='by']/cite/a").attr("href")
                    if (uid.contains("uid-")) {
                        topic.uid = NetworkUtil.getUid(uid)
                    }
                    val time = it.selectXpath("tr/td[@class='by']/em/a").text()
                    if (time.isNotEmpty()) {
                        topic.time = time
                    }
                    val reply = it.selectXpath("tr/td[@class='num']/a").text()
                    if (reply.isNotEmpty()) {
                        topic.reply = reply
                    }
                    val examine = it.selectXpath("tr/td[@class='num']/em").text()
                    if (examine.isNotEmpty()) {
                        topic.examine = examine
                    }
                    val title1 = it.selectXpath("tr/th[@class='new']/a[@class='s xst']").text()
                    if (title1.isNotEmpty()) {
                        topic.title = title1
                    }
                    val thread = it.selectXpath("tr/th[@class='new']/a[@class='s xst']").attr("href")
                    if (thread.contains("thread-")) {
                        topic.tid = thread.split("thread-").last().split("-").first()
                    }

                    val icon1 = it.selectXpath("tr/th[@class='new']/span[1]").attr("class")
                    if (icon1.isNotEmpty()) {
                        topic.icon1 = getIcon(icon1)
                    }
                    val icon2 = it.selectXpath("tr/th[@class='new']/span[2]").attr("class")
                    if (icon2.isNotEmpty()) {
                        topic.icon2 = getIcon(icon2)
                    }
                    val icon3 = it.selectXpath("tr/th[@class='new']/span[3]").attr("class")
                    if (icon3.isNotEmpty()) {
                        topic.icon3 = getIcon(icon3)
                    }
                    val icon4 = it.selectXpath("tr/th[@class='new']/span[4]").attr("class")
                    if (icon4.isNotEmpty()) {
                        topic.icon4 = getIcon(icon4)
                    }

                    var attachment = ""
                    var text = it.selectXpath("tr/th[@class='new']").text()
                    val isLine = text.contains("- [")
                    if (text.count() != topic.title.count()) {
                        text = text.replace("\r\n", "")
                        text = text.split("- [").last()
                        var attachment1 = if (isLine) "[$text" else text.replace(topic.title, "")
                        val num = it.selectXpath("tr/th[@class='new']/span[@class='tps']").text()
                        if (num.isNotEmpty()) {
                            attachment1 = attachment1.replace(num, "")
                        }
                        val new = it.selectXpath("tr/th[@class='new']/a[@class='xi1']").text()
                        attachment = if (new == "New" && !isLine) {
                            "New"
                        } else {
                            attachment1
                        }
                    }
                    if (attachment.isNotEmpty()) {
                        topic.attachment = attachment.replace("-", "")
                        if (topic.attachment == "New" || topic.attachment == " "
                            || topic.attachment == topic.title) {
                            topic.attachment = " ${topic.attachment}"
                        } else {
                            topic.attachment = " - ${topic.attachment}"
                        }
                        topic.attachmentColorRed = topic.attachment.contains("回帖")
                                || topic.attachment.contains("悬赏")
                                || topic.attachment.contains("New")
                                || topic.attachment.contains("人参与")
                    }

                    if (topic.title.isNotEmpty()) {
                        list.add(topic)
                    }
                }
            }
        }

        return list
    }

    private fun popBack() {
        viewModelScope.launch {
            _viewEvents.send(NodeListViewEvent.PopBack)
        }
    }
}

sealed class NodeListViewEvent {
    object PopBack : NodeListViewEvent()
}

sealed class NodeListViewAction {
    object PopBack: NodeListViewAction()

    data class SetFid(val id: String) : NodeListViewAction()
}

data class NodeListViewState(
    val pagingData: Flow<PagingData<TopicListModel>>,
    val title: String = "",
    val header: NodeListHeaderModel = NodeListHeaderModel()
)

class NodeListHeaderModel {
    var today: String = ""
    var topic: String = ""
    var rank: String = ""
    var todayImage: String = ""
    var rankImage : String= ""
}