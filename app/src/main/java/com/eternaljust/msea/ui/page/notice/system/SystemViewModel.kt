package com.eternaljust.msea.ui.page.notice.system

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.eternaljust.msea.utils.HTMLURL
import com.eternaljust.msea.utils.NetworkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class SystemViewModel : ViewModel() {
    private val pager by lazy {
        Pager(
            PagingConfig(pageSize = 30, prefetchDistance = 1)
        ) {
            SystemListSource()
        }.flow.cachedIn(viewModelScope)
    }

    var viewStates by mutableStateOf(SystemListViewState(pagingData = pager))
        private set
}

data class SystemListViewState(
    val pagingData: SystemPostList
)

class SystemListModel {
    var time = ""
    var content = ""
}

typealias SystemPostList = Flow<PagingData<SystemListModel>>

class SystemListSource : PagingSource<Int, SystemListModel>() {
    private suspend fun loadData(page: Int) : List<SystemListModel> {
        val list = mutableListOf<SystemListModel>()

        withContext(Dispatchers.IO) {
            val url = HTMLURL.SYSTEM_LIST + "&page=${page}"
            val document = NetworkUtil.getRequest(url)
            val dl = document.selectXpath("//div[@class='nts']/dl")

            dl.forEach {
                println("mypost----")
                println(it.html())
                var system = SystemListModel()
                val time = it.selectXpath("dt/span[@class='xg1 xw0']").text()
                if (time.isNotEmpty()) {
                    system.time = time
                }
                val content = it.selectXpath("dd[@class='ntc_body']").text()
                if (content.isNotEmpty()) {
                    system.content = content
                }

                list.add(system)
            }
        }

        return list
    }

    override fun getRefreshKey(state: PagingState<Int, SystemListModel>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SystemListModel> {
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