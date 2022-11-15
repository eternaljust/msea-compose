package com.eternaljust.msea.ui.page.profile.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.eternaljust.msea.utils.HTMLURL
import com.eternaljust.msea.utils.NetworkUtil
import com.eternaljust.msea.utils.configPager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CreditLogViewModel : ViewModel() {
    private val pager by lazy {
        configPager(PagingConfig(pageSize = 30, prefetchDistance = 1)) {
            loadData(page = it)
        }
    }

    var viewStates by mutableStateOf(CreditLogListViewState(pagingData = pager))
        private set

    private suspend fun loadData(page: Int) : List<CreditLogListModel> {
        val list = mutableListOf<CreditLogListModel>()

        withContext(Dispatchers.IO) {
            val url = HTMLURL.CREDIT_LIST + "&op=log&page=${page}"
            val document = NetworkUtil.getRequest(url)
            val tr = document.selectXpath("//table[@class='dt']/tbody/tr")

            tr.forEach {
                if (it.html().contains("td")) {
                    var log = CreditLogListModel()
                    val action = it.selectXpath("td[1]").text()
                    if (action.isNotEmpty()) {
                        log.action = action
                    }
                    val bit = it.selectXpath("td[2]/span").text()
                    if (bit.isNotEmpty()) {
                        log.bit = bit
                        log.isAdd = bit.contains("+")
                    }
                    val content = it.selectXpath("td[3]").text()
                    if (content.isNotEmpty()) {
                        log.content = content
                    }
                    val time = it.selectXpath("td[4]").text()
                    if (time.isNotEmpty()) {
                        log.time = time.replace(" ", "\n")
                    }

                    list.add(log)
                }
            }
        }

        return list
    }
}

data class CreditLogListViewState(
    val pagingData: Flow<PagingData<CreditLogListModel>>
)

class CreditLogListModel {
    var action = ""
    var bit = ""
    var content = ""
    var time = ""
    var isAdd = true
}

class CreditSystemViewModel : ViewModel() {
    private val pager by lazy {
        configPager(PagingConfig(pageSize = 30, prefetchDistance = 1)) {
            loadData(page = it)
        }
    }

    var viewStates by mutableStateOf(CreditSystemListViewState(pagingData = pager))
        private set

    private suspend fun loadData(page: Int) : List<CreditSystemListModel> {
        val list = mutableListOf<CreditSystemListModel>()
        if (page > 1) {
            return list
        }

        withContext(Dispatchers.IO) {
            val url = HTMLURL.CREDIT_LIST + "&op=log&suboperation=creditrulelog"
            val document = NetworkUtil.getRequest(url)
            val tr = document.selectXpath("//table[@class='dt']/tbody/tr")

            tr.forEach {
                if (it.html().contains("td")) {
                    var system = CreditSystemListModel()
                    val action = it.selectXpath("td[1]/a").text()
                    if (action.isNotEmpty()) {
                        system.action = action
                    }
                    val cycles = it.selectXpath("td[3]").text()
                    if (cycles.isNotEmpty()) {
                        system.cycles = cycles
                    }
                    val count = it.selectXpath("td[2]").text()
                    if (count.isNotEmpty()) {
                        system.count = count
                    }
                    val bit = it.selectXpath("td[4]").text()
                    if (bit.isNotEmpty()) {
                        system.bit = bit
                    }
                    val violation = it.selectXpath("td[5]").text()
                    if (violation.isNotEmpty()) {
                        system.violation = violation
                    }
                    val time = it.selectXpath("td[6]").text()
                    if (time.isNotEmpty()) {
                        system.time = time.replace(" ", "\n")
                    }

                    list.add(system)
                }
            }
        }

        return list
    }
}

data class CreditSystemListViewState(
    val pagingData: Flow<PagingData<CreditSystemListModel>>
)

class CreditSystemListModel {
    var rid = ""
    var action = ""
    var count = ""
    var cycles = ""
    var bit = ""
    var violation = ""
    var time = ""
}

class CreditRuleViewModel : ViewModel() {
    private val pager by lazy {
        configPager(PagingConfig(pageSize = 30, prefetchDistance = 1)) {
            loadData(page = it)
        }
    }

    var viewStates by mutableStateOf(CreditRuleListViewState(pagingData = pager))
        private set

    private suspend fun loadData(page: Int) : List<CreditRuleListModel> {
        val list = mutableListOf<CreditRuleListModel>()
        if (page > 1) {
            return list
        }

        withContext(Dispatchers.IO) {
            val url = HTMLURL.CREDIT_LIST + "&op=rule"
            val document = NetworkUtil.getRequest(url)
            val tr = document.selectXpath("//table[@class='dt valt']/tbody/tr")

            tr.forEach {
                if (it.html().contains("td")) {
                    var rule = CreditRuleListModel()
                    val action = it.selectXpath("td[1]").text()
                    if (action.isNotEmpty()) {
                        rule.action = action
                    }
                    val cycles = it.selectXpath("td[2]").text()
                    if (cycles.isNotEmpty()) {
                        rule.cycles = cycles
                    }
                    val count = it.selectXpath("td[3]").text()
                    if (count.isNotEmpty()) {
                        rule.count = count
                    }
                    val bit = it.selectXpath("td[4]").text()
                    if (bit.isNotEmpty()) {
                        rule.bit = bit
                    }
                    val violation = it.selectXpath("td[5]").text()
                    if (violation.isNotEmpty()) {
                        rule.violation = violation
                    }

                    list.add(rule)
                }
            }
        }

        return list
    }
}

data class CreditRuleListViewState(
    val pagingData: Flow<PagingData<CreditRuleListModel>>
)

class CreditRuleListModel {
    var action = ""
    var count = ""
    var cycles = ""
    var bit = ""
    var violation = ""
}