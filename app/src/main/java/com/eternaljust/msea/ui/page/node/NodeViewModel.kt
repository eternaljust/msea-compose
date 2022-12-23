package com.eternaljust.msea.ui.page.node

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.eternaljust.msea.utils.HTMLURL
import com.eternaljust.msea.utils.NetworkUtil
import com.eternaljust.msea.utils.configPager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NodeViewModel : ViewModel() {
    private var gid = ""

    private val pager by lazy {
        configPager(PagingConfig(pageSize = 30, prefetchDistance = 1)) {
            loadData(page = it)
        }
    }

    var viewStates by mutableStateOf(NodeViewState(pagingData = pager))
        private set
    private val _viewEvents = Channel<NodeViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: NodeViewAction) {
        when (action) {
            is NodeViewAction.PopBack -> popBack()
            is NodeViewAction.SetGid -> gid = action.gid
        }
    }

    private fun popBack() {
        viewModelScope.launch {
            _viewEvents.send(NodeViewEvent.PopBack)
        }
    }

    private suspend fun loadData(page: Int) : List<NodeModel> {
        val list = mutableListOf<NodeModel>()
        if (page > 1) {
            return list
        }

        withContext(Dispatchers.IO) {
            val url = HTMLURL.NODE_LIST + gid
            val document = NetworkUtil.getRequest(url)
            var category = document.selectXpath("//div[@class='bm bmw  flg cl']")
            if (category.isEmpty()) {
                category = document.selectXpath("//div[@class='bm bmw  cl']")
            }
            category.forEach { ct ->
                var node = NodeModel()

                val title = ct.selectXpath("div[@class='bm_h cl']/h2/a").text()
                if (title.isNotEmpty()) {
                    node.title = title
                }

                val span = ct.selectXpath("div[@class='bm_h cl']/span/a")
                var users = mutableListOf<String>()
                span.forEach { a ->
                    val text = a.text()
                    if (text.isNotEmpty()) {
                        users.add(text)
                    }
                }
                node.moderators = users

                if (gid == "92") {
                    val tr = ct.selectXpath("div[@class='bm_c']/table/tbody/tr")
                    var models = mutableListOf<NodeListModel>()
                    tr.forEach { dl ->
                        var model = NodeListModel()

                        val forum = dl.selectXpath("td/h2/a").attr("href")
                        if (forum.contains("forum-")) {
                            val id = forum.split("forum-")[1].split("-")[0]
                            model.fid = id
                        } else if (forum.contains("fid=")) {
                            model.fid = forum.split("fid=")[1]
                        }
                        val title = dl.selectXpath("td/h2/a").text()
                        if (title.isNotEmpty()) {
                            model.title = title
                        }
                        val today = dl.selectXpath("td/h2/em[@class='xw0 xi1']").text()
                        if (today.isNotEmpty()) {
                            model.today = today
                        }
                        val count = dl.selectXpath("td[@class='fl_i']").text()
                        if (count.isNotEmpty()) {
                            model.count = count
                        }
                        val content = dl.selectXpath("td[@class='fl_by']/div/a").text()
                        if (content.isNotEmpty()) {
                            model.content = content
                        }
                        val tid = dl.selectXpath("td[@class='fl_by']/div/a").attr("href")
                        if (tid.contains("tid=") && tid.contains("goto=")) {
                            model.tid = tid.split("goto=")[0].split("tid=")[1]
                            model.tid = model.tid.replace("&", "")
                        }
                        val name = dl.selectXpath("td[@class='fl_by']/div/cite/a").text()
                        if (name.isNotEmpty()) {
                            model.username = name
                        }
                        val time = dl.selectXpath("td[@class='fl_by']/div/cite").text()
                        if (time.isNotEmpty()) {
                            model.time = time.replace(" ${model.username}", "")
                        }

                        if (model.title.isNotEmpty()) {
                            models.add(model)
                        }
                    }
                    node.list = models
                } else {
                    val td = ct.selectXpath("div[@class='bm_c']/table/tbody/tr/td[@class='fl_g']")
                    var models = mutableListOf<NodeListModel>()
                    td.forEach { dl ->
                        var model = NodeListModel()

                        val forum = dl.selectXpath("div[@class='fl_icn_g']/a").attr("href")
                        if (forum.contains("forum-")) {
                            val id = forum.split("forum-")[1].split("-")[0]
                            model.fid = id
                        } else if (forum.contains("fid=")) {
                            model.fid = forum.split("fid=")[1]
                        }
                        val title = dl.selectXpath("dl/dt/a").text()
                        if (title.isNotEmpty()) {
                            model.title = title
                        }
                        val today = dl.selectXpath("dl/dt/em[@class='xw0 xi1']").text()
                        if (today.isNotEmpty()) {
                            model.today = today
                        }
                        val count = dl.selectXpath("dl/dd[1]").text()
                        if (count.isNotEmpty()) {
                            model.count = count
                        }
                        val content = dl.selectXpath("dl/dd[2]/a").text()
                        if (content.isNotEmpty()) {
                            model.content = content
                        }
                        val tid = dl.selectXpath("dl/dd[2]/a").attr("href")
                        if (tid.contains("tid=") && tid.contains("goto=")) {
                            model.tid = tid.split("goto=")[0].split("tid=")[1]
                            model.tid = model.tid.replace("&", "")
                        }
                        val name = dl.selectXpath("dl/dd[2]/cite/a").text()
                        if (name.isNotEmpty()) {
                            model.username = name
                        }
                        val time = dl.selectXpath("dl/dd[2]/cite").text()
                        if (time.isNotEmpty()) {
                            model.time = time.replace(" ${model.username}", "")
                        }

                        models.add(model)
                    }
                    node.list = models
                }

                list.add(node)
            }
        }

        return list
    }
}

data class NodeViewState(
    val pagingData: Flow<PagingData<NodeModel>>
)

sealed class NodeViewEvent {
    object PopBack : NodeViewEvent()
}

sealed class NodeViewAction {
    object PopBack : NodeViewAction()

    data class SetGid(val gid: String) :  NodeViewAction()
}

class NodeListModel {
    var fid = ""
    var tid = ""
    var title = ""
    var today = ""
    var count = ""
    var content = ""
    var time = ""
    var username = ""
}

class NodeModel {
    var title = ""
    var moderators = emptyList<String>()
    var list = emptyList<NodeListModel>()
}
