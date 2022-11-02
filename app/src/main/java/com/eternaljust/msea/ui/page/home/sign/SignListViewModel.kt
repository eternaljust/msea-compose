package com.eternaljust.msea.ui.page.home.sign

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.paging.*
import com.eternaljust.msea.utils.HTMLURL
import com.eternaljust.msea.utils.NetworkUtil
import com.eternaljust.msea.utils.configPager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow

/**
 * 今日签到列表
 */
class SignListViewModel : ViewModel() {
    private val pager by lazy {
        configPager(PagingConfig(pageSize = 7, prefetchDistance = 1)) {
            loadData(page = it)
        }
    }

    var viewStates by mutableStateOf(SignListViewState(pagingData = pager))
        private set

    private suspend fun loadData(page: Int) : List<SignListModel> {
        val list = mutableListOf<SignListModel>()

        withContext(Dispatchers.IO) {
            val url = HTMLURL.SIGN_LIST + "&ac=daysign&page=${page}"
            val document = NetworkUtil.getRequest(url)
            val trs = document.selectXpath("//div[@class='wqpc_sign_table']/div/table//tr")
            trs.forEach {
                println("---tr=${it.html()}")
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
}

data class SignListViewState(
    val pagingData: Flow<PagingData<SignListModel>>
)

class SignListModel {
    var uid = ""
    var no = ""
    var name = ""
    var content = ""
    var time = ""
    var bits = ""
}

/**
 * 总天数、总奖励排行
 */
class SignDayListViewModel(
    val tabItem: SignTabItem
) : ViewModel() {
    companion object {
        val days by lazy { SignDayListViewModel(tabItem = SignTabItem.TOTAL_DAYS) }
        val reward by lazy { SignDayListViewModel(tabItem = SignTabItem.TOTAL_REWARD) }
    }

    private val pager by lazy {
        configPager(PagingConfig(pageSize = 7, initialLoadSize = 1)) {
            loadData(page = it)
        }
    }

    var viewStates by mutableStateOf(SignDayListViewState(pagingData = pager))
        private set

    private suspend fun loadData(page: Int) : List<SignDayListModel> {
        val list = mutableListOf<SignDayListModel>()

        withContext(Dispatchers.IO) {
            val url = HTMLURL.SIGN_LIST + "&ac=daysign&ac=${tabItem.id}&page=${page}"
            val document = NetworkUtil.getRequest(url)
            val trs = document.selectXpath("//div[@class='wqpc_sign_table']/div/table//tr")
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

                    val bits = it.selectXpath("td[6]/span").text()
                    if (bits.isNotEmpty()) {
                        signModel.bits = bits
                    }

                    val time = it.selectXpath("td[7]").text()
                    if (time.isNotEmpty()) {
                        signModel.time = time.replace(" ", "\n")
                    }

                    list.add(signModel)
                }
            }
        }

        return list
    }
}

data class SignDayListViewState(
    val pagingData: Flow<PagingData<SignDayListModel>>
)

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
