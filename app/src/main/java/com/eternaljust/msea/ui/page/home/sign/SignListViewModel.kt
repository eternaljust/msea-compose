package com.eternaljust.msea.ui.page.home.sign

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.eternaljust.msea.utils.HTMLURL
import com.eternaljust.msea.utils.NetworkUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow

class SignListViewModel : ViewModel() {
    private val pager by lazy {
        Pager(
            PagingConfig(pageSize = 14)
        ) {
            SignListSource()
        }.flow.cachedIn(viewModelScope)
    }

    var viewStates by mutableStateOf(SignListViewState(pagingData = pager))
        private set

    fun dispatch(action: SignListViewAction) {
//        when (action) {
//            is SignListViewAction.LoadList -> loadList()
//            is SignListViewAction.LoadMoreList -> loadMoreList()
//        }
    }
}

data class SignListViewState(
    val listState: LazyListState = LazyListState(),
    val pagingData: PagingSignList
)

sealed class SignDayListViewAction {
    object LoadList : SignDayListViewAction()
}

sealed class SignListViewAction {
    object LoadList : SignListViewAction()
    object LoadMoreList : SignListViewAction()
}

typealias PagingSignList = Flow<PagingData<SignListModel>>

class SignDayListViewModel(
    val tabItem: SignTabItem
) : ViewModel() {
    companion object {
        val days by lazy { SignDayListViewModel(tabItem = SignTabItem.TOTAL_DAYS) }
        val reward by lazy { SignDayListViewModel(tabItem = SignTabItem.TOTAL_REWARD) }
    }

    var viewStates by mutableStateOf(SignDayListViewState())
        private set

    private var page = 1

    fun dispatch(action: SignDayListViewAction) {
        when (action) {
            is SignDayListViewAction.LoadList -> loadList()
        }
    }

    private fun loadList() {
        page = 1
    }
}

data class SignDayListViewState(
    val list: List<SignDayListModel> = emptyList(),
    val isRefreshing: Boolean = false,
)

class SignListModel {
    var uid = ""
    var no = ""
    var name = ""
    var content = ""
    var time = ""
    var bits = ""
}

class SignDayListModel {
    var uid = ""
    var no = ""
    var name = ""
    var time = ""
    var bits = ""
    var continuous = ""
    var month = ""
    var total = ""
}

class SignListSource: PagingSource<Int, SignListModel>() {
    private suspend fun loadData(page: Int = 1) : List<SignListModel> {
        val list = mutableListOf<SignListModel>()

        withContext(Dispatchers.IO) {
            val url = HTMLURL.SIGN_LIST + "&ac=daysign&page=${page}"
            val document = NetworkUtil.getRequest(url)
            val trs = document.selectXpath("//div[@class='wqpc_sign_table']/div/table//tr")
            trs.forEach {
                println("tr=${it.html()}")
                val signModel = SignListModel()
                val no = it.selectXpath("td[1]").text()
                if (no.isNotEmpty() && no.contains("NO.")) {
                    signModel.no = no.replace("NO.", "")

                    val name = it.selectXpath("td[2]//a").text()
                    if (name.isNotEmpty()) {
                        signModel.name = name
                    }

                    val href = it.selectXpath("td[2]//a").attr("href")
                    if (href.contains("uid-")) {
                        val uid = href.split("uid-")[1]
                        if (uid.contains(".html")) {
                            signModel.uid = uid.split(".").first()
                        } else {
                            signModel.uid = uid
                        }
                    }

                    val content = it.selectXpath("td[3]/p").text()
                    if (content.isNotEmpty()) {
                        signModel.content = content
                    }

                    val bits = it.selectXpath("td[4]/span").text()
                    if (bits.isNotEmpty()) {
                        signModel.bits = bits
                    }

                    val time = it.selectXpath("td[5]/span").text()
                    if (time.isNotEmpty()) {
                        signModel.time = time
                    }

                    list.add(signModel)
                }
            }
        }

        return list
    }

    override fun getRefreshKey(state: PagingState<Int, SignListModel>): Int? =null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SignListModel> {
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
