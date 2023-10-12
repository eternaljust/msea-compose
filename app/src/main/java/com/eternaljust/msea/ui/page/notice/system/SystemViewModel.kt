package com.eternaljust.msea.ui.page.notice.system

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.paging.*
import com.eternaljust.msea.utils.HTMLURL
import com.eternaljust.msea.utils.NetworkUtil
import com.eternaljust.msea.utils.configPager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class SystemViewModel : ViewModel() {
    private val pager by lazy {
        configPager(PagingConfig(pageSize = 30, prefetchDistance = 1)) {
            loadData(page = it)
        }
    }

    var viewStates by mutableStateOf(SystemListViewState(pagingData = pager))
        private set

    private suspend fun loadData(page: Int) : List<SystemListModel> {
        val list = mutableListOf<SystemListModel>()

        withContext(Dispatchers.IO) {
            val url = HTMLURL.SYSTEM_LIST + "&page=${page}"
            val document = NetworkUtil.getRequest(url)
            val dl = document.selectXpath("//div[@class='nts']/dl")

            dl.forEach {
                val system = SystemListModel()
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
}

data class SystemListViewState(
    val pagingData: Flow<PagingData<SystemListModel>>
)

class SystemListModel {
    val uuid = UUID.randomUUID()
    var time = ""
    var content = ""
}