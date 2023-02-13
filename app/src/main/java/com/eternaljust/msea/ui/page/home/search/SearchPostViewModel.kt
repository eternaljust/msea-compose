package com.eternaljust.msea.ui.page.home.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eternaljust.msea.utils.HTMLURL
import com.eternaljust.msea.utils.NetworkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchPostViewModel: ViewModel() {
    private var keyword = ""
    private var page = 1
    private var pageLoadCompleted = true
    private var href = ""

    var viewStates by mutableStateOf(SearchPostState())
        private set
    val pageSize: Int
        get() = 18

    fun dispatch(action: SearchPostAction) {
        when (action) {
            is SearchPostAction.SearchKeyword -> {
                if (keyword != action.content) {
                    keyword = action.content
                    if (action.content.isNotEmpty()) {
                        page = 1
                        href = ""
                        loadMoreData()
                    }
                }
            }
            is SearchPostAction.LoadMoreData -> {
                if (keyword.isNotEmpty()) {
                    page += 1
                    loadMoreData()
                }
            }
        }
    }

    private fun loadMoreData() {
        println("---开始搜索帖子：$keyword")
        pageLoadCompleted = false
        if (page == 1) {
            viewStates = viewStates.copy(list = emptyList(), isRefreshing = true)
        }

        var list = mutableListOf<SearchPostListModel>()

        viewModelScope.launch(Dispatchers.IO) {
            val srchtxt = NetworkUtil.urlEncode(keyword)
            var url = HTMLURL.SEARCH_POST + "&srchtxt=${srchtxt}"
            if (page > 1 && href.isNotEmpty()) {
                url = "${HTMLURL.BASE}/$href"
            }
            val document = NetworkUtil.getRequest(url)
            val a = document.selectXpath("//div[@class='pgs cl mbm']/div/a")
            a.forEach {
                val text = it.text()
                if (text == (page + 1).toString()) {
                    val aHref = it.attr("href")
                    if (aHref.isNotEmpty()) {
                        href = aHref
                    }
                }
            }

            val li = document.selectXpath("//ul/li[@class='pbw']")
            li.forEach {
                val search = SearchPostListModel()
                search.keyword = keyword
                val tid = it.attr("id")
                if (tid.isNotEmpty()) {
                    search.tid = tid
                }
                val title = it.selectXpath("h3/a[1]").text()
                if (title.isNotEmpty()) {
                    search.title = title
                }
                val views = it.selectXpath("p[@class='xg1']").text()
                if (views.isNotEmpty()) {
                    search.replyViews = views
                }
                val content = it.selectXpath("p[2]").text()
                if (content.isNotEmpty()) {
                    search.content = content
                }
                val time = it.selectXpath("p[3]/span[1]").text()
                if (time.isNotEmpty()) {
                    search.time = time
                }
                val name = it.selectXpath("p[3]/span[2]/a").text()
                if (name.isNotEmpty()) {
                    search.name = name
                }
                val plate = it.selectXpath("p[3]/span[3]/a").text()
                if (plate.isNotEmpty()) {
                    search.plate = plate
                }
                val fid = it.selectXpath("p[3]/span[3]/a").attr("href")
                if (fid.isNotEmpty()) {
                    search.fid = NetworkUtil.getFid(fid)
                }
                list.add(search)

                if (list.count() == 9) {
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
                li.last()?.let { last ->
                    if (last == it) {
                        pageLoadCompleted = true
                        if (li.count() != pageSize) {
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

data class SearchPostState(
    val list: List<SearchPostListModel> = emptyList(),
    val isRefreshing: Boolean = false
)

sealed class SearchPostAction {
    object LoadMoreData: SearchPostAction()

    data class SearchKeyword(val content: String) : SearchPostAction()
}

class SearchPostListModel {
    var fid = ""
    var tid = ""
    var title = ""
    var content = ""
    var time = ""
    var replyViews = ""
    var name = ""
    var plate = ""
    var keyword = ""
}
