package com.eternaljust.msea.ui.page.home.topic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eternaljust.msea.ui.page.home.TopicTabItem
import com.eternaljust.msea.utils.HTMLURL
import com.eternaljust.msea.utils.NetworkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TopicListViewModel(
    val tabItem: TopicTabItem
) : ViewModel() {
    companion object {
        val new by lazy { TopicListViewModel(tabItem = TopicTabItem.NEW) }
        val hot by lazy { TopicListViewModel(tabItem = TopicTabItem.HOT) }
        val newthread by lazy { TopicListViewModel(tabItem = TopicTabItem.NEWTHREAD) }
        val sofa by lazy { TopicListViewModel(tabItem = TopicTabItem.SOFA) }
    }

    var pageSize = 50
        private set
    var isFirstLoad = true
        private set

    private var pageLoadCompleted = true
    private var page = 1

    var viewStates by mutableStateOf(TopicListViewState())
        private set

    fun dispatch(action: TopicListViewAction) {
        when (action) {
            TopicListViewAction.LoadData -> {
                if (pageLoadCompleted) {
                    isFirstLoad = false
                    page = 1
                    loadMoreData()
                }
            }
            TopicListViewAction.LoadMoreData -> {
                page += 1
                loadMoreData()
            }
        }
    }

    private fun loadMoreData() {
        pageLoadCompleted = false
        if (page == 1) {
            viewStates = viewStates.copy(isRefreshing = true)
        }
        var list = mutableListOf<TopicListModel>()

        viewModelScope.launch(Dispatchers.IO) {
            val url = HTMLURL.TOPIC_LIST + "&view=${tabItem.id}&page=${page}"
            val document = NetworkUtil.getRequest(url)
            val tbody = document.selectXpath("//div[@id='threadlist']//table/tbody")
            println("tbody---${tbody.count()}")
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
                val title = it.selectXpath("tr/th[@class='common']/a").text()
                if (title.isNotEmpty()) {
                    topic.title = title
                }
                val thread = it.selectXpath("tr/th[@class='common']/a").attr("href")
                if (thread.contains("thread-")) {
                    topic.tid = thread.split("thread-").last().split("-").first()
                }

                val icon1 = it.selectXpath("tr/th[@class='common']/span[1]").attr("class")
                if (icon1.isNotEmpty()) {
                    topic.icon1 = getIcon(icon1)
                }
                val icon2 = it.selectXpath("tr/th[@class='common']/span[2]").attr("class")
                if (icon2.isNotEmpty()) {
                    topic.icon2 = getIcon(icon2)
                }
                val icon3 = it.selectXpath("tr/th[@class='common']/span[3]").attr("class")
                if (icon3.isNotEmpty()) {
                    topic.icon3 = getIcon(icon3)
                }
                val icon4 = it.selectXpath("tr/th[@class='common']/span[4]").attr("class")
                if (icon4.isNotEmpty()) {
                    topic.icon4 = getIcon(icon4)
                }

                var attachment = it.selectXpath("tr/th[@class='common']/span[@class='xi1']").text()
                var text = it.selectXpath("tr/th[@class='common']").text()
                if (text.count() != topic.title.count()) {
                    text = text.replace("\r\n", "")
                    var attachment1 = text.replace(title, "")
                    val num = it.selectXpath("tr/th[@class='common']/span[@class='tps']").text()
                    if (num.isNotEmpty()) {
                        attachment1 = attachment1.replace(num, "")
                    }
                    attachment1 = attachment1.replace(" ", "")
                    attachment = attachment1
                }
                if (attachment.isNotEmpty()) {
                    topic.attachment = attachment.replace("-", "")
                    topic.attachment = " - ${topic.attachment}"
                    topic.attachmentColorRed = topic.attachment.contains("回帖")
                            || topic.attachment.contains("悬赏")
                }

                list.add(topic)
                println("list.count()---${list.count()}")
                if (list.count() == 10) {
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
                tbody.last()?.let { last ->
                    if (last == it) {
                        pageLoadCompleted = true
                        if (tbody.count() != pageSize) {
                            viewStates = viewStates.copy(
                                list = viewStates.list + list,
                                isRefreshing = false
                            )
                        }
                    }
                }
            }
        }
    }
}

data class TopicListViewState(
    val list: List<TopicListModel> = emptyList(),
    val isRefreshing: Boolean = false
)

sealed class TopicListViewAction {
    object LoadData : TopicListViewAction()
    object LoadMoreData : TopicListViewAction()
}

class TopicListModel {
    var uid = ""
    var tid = ""
    var name = ""
    var avatar = ""
    var title = ""
    var time = ""
    var icon1 = ""
    var icon2 = ""
    var icon3 = ""
    var icon4 = ""
    var attachment = ""
    var attachmentColorRed = false
    var examine = ""
    var reply = ""
}

fun getIcon(name: String): String = when (name) {
    "iconfont icon-image" -> "image"
    "iconfont icon-fire" -> "fire"
    "iconfont icon-guzhang1" -> "hand"
    "iconfont icon-attachment1" -> "link"
    "iconfont icon-jinghua" -> "premium"
    else -> ""
}