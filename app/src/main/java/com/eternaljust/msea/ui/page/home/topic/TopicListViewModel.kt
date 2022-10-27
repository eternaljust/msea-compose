package com.eternaljust.msea.ui.page.home.topic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
                val examine = it.selectXpath("tr/td[@class='num']/a").text()
                if (examine.isNotEmpty()) {
                    topic.examine = examine
                }
                val reply = it.selectXpath("tr/td[@class='num']/em").text()
                if (reply.isNotEmpty()) {
                    topic.reply = reply
                }
                val title = it.selectXpath("tr/th[@class='common']/a").text()
                if (title.isNotEmpty()) {
                    topic.title = title
                }

                list.add(topic)
            }
        }

        return list
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