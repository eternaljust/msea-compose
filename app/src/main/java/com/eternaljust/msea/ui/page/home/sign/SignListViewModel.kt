package com.eternaljust.msea.ui.page.home.sign

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.eternaljust.msea.utils.HTMLURL
import com.eternaljust.msea.utils.NetworkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignListViewModel : ViewModel() {
    var viewStates by mutableStateOf(SignListViewState())
        private set

    private var page = 1

    val pager = Pager(
        PagingConfig(pageSize = 20)
    ) {
        SignListSource()
    }.flow.cachedIn(viewModelScope)

    fun dispatch(action: SignListViewAction) {
        when (action) {
            is SignListViewAction.LoadList -> loadList()
            is SignListViewAction.LoadMoreList -> loadMoreList()
        }
    }

    private fun loadList() {
        page = 1
        loadData()
    }

    private fun loadMoreList() {
        page ++
        loadData()
    }

    private fun loadData() {
        viewStates = viewStates.copy(isRefreshing = true)
        viewModelScope.launch(Dispatchers.IO) {
            val url = HTMLURL.SIGN_LIST + "&ac=daysign&page=${page}"
            val document = NetworkUtil.getRequest(url)
            val trs = document.selectXpath("//div[@class='wqpc_sign_table']/div/table//tr")
            val list = mutableListOf<SignListModel>()
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
                }

                list.add(signModel)
            }
            viewStates = if (page == 1) {
                viewStates.copy(list = list)
            } else {
                viewStates.copy(list = viewStates.list + list)
            }

            viewStates = viewStates.copy(isRefreshing = false)
        }
    }
}

data class SignListViewState(
    val list: List<SignListModel> = emptyList(),
    val isRefreshing: Boolean = false
)

sealed class SignListViewAction {
    object LoadList : SignListViewAction()
    object LoadMoreList : SignListViewAction()
}

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
        loadData()
    }

    private fun loadData() {
        viewStates = viewStates.copy(isRefreshing = true)
        viewModelScope.launch(Dispatchers.IO) {
            val url = HTMLURL.SIGN_LIST + "&ac=${tabItem.id}&page=${page}"
            val document = NetworkUtil.getRequest(url)
            val trs = document.selectXpath("//div[@class='wqpc_sign_table']/div/table//tr")
            val list = mutableListOf<SignDayListModel>()
            trs.forEach {
                println("tr=${it.html()}")
                val signModel = SignDayListModel()
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

                    val continuous = it.selectXpath("td[3]").text()
                    if (continuous.isNotEmpty()) {
                        signModel.continuous = continuous
                    }

                    val month = it.selectXpath("td[4]").text()
                    if (month.isNotEmpty()) {
                        signModel.month = month
                    }

                    val total = it.selectXpath("td[5]").text()
                    if (total.isNotEmpty()) {
                        signModel.total = total
                    }

                    val bits = it.selectXpath("td[6]").text()
                    if (bits.isNotEmpty()) {
                        signModel.bits = bits
                    }

                    val time = it.selectXpath("td[7]").text()
                    if (time.isNotEmpty()) {
                        signModel.time = time.replace(" ", "\n")
                    }
                }

                list.add(signModel)
            }
            viewStates = if (page == 1) {
                viewStates.copy(list = list)
            } else {
                viewStates.copy(list = viewStates.list + list)
            }

            viewStates = viewStates.copy(isRefreshing = false)
        }
    }
}

data class SignDayListViewState(
    val list: List<SignDayListModel> = emptyList(),
    val isRefreshing: Boolean = false
)

sealed class SignDayListViewAction {
    object LoadList : SignDayListViewAction()
}

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

class SignListSource : PagingSource<Int, SignListModel>() {
    override fun getRefreshKey(state: PagingState<Int, SignListModel>): Int? =null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SignListModel> {
        return try {
            val nextPage = params.key ?: 1
            val datas = mutableListOf(
                SignListModel()
            )
            LoadResult.Page(
                data = datas,
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = nextPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
