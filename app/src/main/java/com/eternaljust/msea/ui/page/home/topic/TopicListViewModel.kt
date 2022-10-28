package com.eternaljust.msea.ui.page.home.topic

import android.graphics.drawable.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.eternaljust.msea.ui.page.home.TopicTabItem
import com.eternaljust.msea.utils.HTMLURL
import com.eternaljust.msea.utils.NetworkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TopicListViewModel(
    tabItem: TopicTabItem
) : ViewModel() {
    companion object {
        val new by lazy { TopicListViewModel(tabItem = TopicTabItem.NEW) }
        val hot by lazy { TopicListViewModel(tabItem = TopicTabItem.HOT) }
        val newthread by lazy { TopicListViewModel(tabItem = TopicTabItem.NEWTHREAD) }
        val sofa by lazy { TopicListViewModel(tabItem = TopicTabItem.SOFA) }
    }

    private val pager by lazy {
        Pager(
            PagingConfig(pageSize = 50)
        ) {
            TopicListSource(tabItem = tabItem)
        }.flow.cachedIn(viewModelScope)
    }

    var viewStates by mutableStateOf(TopicListViewState(pagingData = pager))
        private set
}

data class TopicListViewState(
    val pagingData: PagingTopicList
)

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

typealias PagingTopicList = Flow<PagingData<TopicListModel>>

class TopicListSource(
    val tabItem: TopicTabItem
) : PagingSource<Int, TopicListModel>() {
    private suspend fun loadData(page: Int = 1) : List<TopicListModel> {
        val list = mutableListOf<TopicListModel>()

        withContext(Dispatchers.IO) {
            val url = HTMLURL.TOPIC_LIST + "&view=${tabItem.id}&page=${page}"
            val document = NetworkUtil.getRequest(url)
            val tbody = document.selectXpath("//div[@id='threadlist']//table/tbody")
            tbody.forEach {
                println("tbody----${it.html()}")
                var topic = TopicListModel()

                val avatar = it.selectXpath("tr/td[@class='icn']/a/img").attr("src")
                if (avatar.isNotEmpty()) {
                    topic.avatar = HTMLURL.BASE + "/" + avatar
                }
                val name = it.selectXpath("tr/td[@class='by']/cite/a").text()
                if (name.isNotEmpty()) {
                    topic.name = name
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
            }
        }

        return list
    }

    private fun getIcon(name: String): String = when (name) {
        "iconfont icon-image" -> "image"
        "iconfont icon-fire" -> "fire"
        "iconfont icon-guzhang1" -> "hand"
        "iconfont icon-attachment1" -> "link"
        "iconfont icon-jinghua" -> "premium"
        else -> ""
    }

    override fun getRefreshKey(state: PagingState<Int, TopicListModel>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TopicListModel> {
        return try {
            val nextPage = params.key ?: 1
            val data = loadData(page = nextPage)

            LoadResult.Page(
                data = data,
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = if (data.isEmpty()) null else nextPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}