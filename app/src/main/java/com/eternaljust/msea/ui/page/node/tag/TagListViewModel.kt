package com.eternaljust.msea.ui.page.node.tag

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.eternaljust.msea.ui.page.home.sign.SignViewAction
import com.eternaljust.msea.utils.HTMLURL
import com.eternaljust.msea.utils.NetworkUtil
import com.eternaljust.msea.utils.configPager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TagListViewModel : ViewModel() {
    private var tid: String = ""

    private val pager by lazy {
        configPager(PagingConfig(pageSize = 30, prefetchDistance = 1)) {
            loadData(tid = tid, page = it)
        }
    }

    var viewStates by mutableStateOf(TagListViewState(pagingData = pager))
        private set

    private val _viewEvents = Channel<TagListViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: TagListViewAction) {
        when (action) {
            is TagListViewAction.PopBack -> popBack()
            is TagListViewAction.SetTid -> tid = action.id
        }
    }

    private suspend fun loadData(tid: String, page: Int) : List<TagListModel> {
        val list = mutableListOf<TagListModel>()
        if (page > 1) {
            return list
        }

        withContext(Dispatchers.IO) {
            val url = HTMLURL.TAG_LIST + "&id=$tid"
            val document = NetworkUtil.getRequest(url)
            var tr = document.selectXpath("//div[@class='bm_c']/table/tbody/tr")
            tr.forEach {
                println("td---${it.html()}")
                var tag = TagListModel()

                val gif = it.selectXpath("td[@class='icn']/a/img").text()
                if (gif.isNotEmpty()) {
                    tag.gif = HTMLURL.BASE + "/${gif}"
                }

                list.add(tag)
            }
        }

        return list
    }

    private fun popBack() {
        viewModelScope.launch {
            _viewEvents.send(TagListViewEvent.PopBack)
        }
    }
}

data class TagListViewState(
    val pagingData: Flow<PagingData<TagListModel>>
)

sealed class TagListViewEvent {
    object PopBack : TagListViewEvent()
}

sealed class TagListViewAction {
    object PopBack: TagListViewAction()

    data class SetTid(val id: String) : TagListViewAction()
}

class TagListModel {
    var uid = ""
    var fid = ""
    var title = ""
    var tid = ""
    var gif = ""
    var plate = ""
    var name = ""
    var time = ""
    var examine = ""
    var reply = ""
    var lastName = ""
    var lastTime = ""
}
