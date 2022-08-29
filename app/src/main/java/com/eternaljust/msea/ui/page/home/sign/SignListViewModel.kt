package com.eternaljust.msea.ui.page.home.sign

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eternaljust.msea.utils.HTMLURL
import com.eternaljust.msea.utils.NetworkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignListViewModel : ViewModel() {
    var viewStates by mutableStateOf(SignListViewState())
        private set

    private var page = 1

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
            var list = mutableListOf<SignListModel>()
            trs.forEach {
                println("tr=${it.html()}")
                var signModel = SignListModel()
                val no = it.selectXpath("td[1]").text()
                if (no.isNotEmpty() && no.contains("NO.")) {
                    signModel.no = no

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
            if (page == 1) {
                viewStates = viewStates.copy(list = list)
            } else {
                viewStates = viewStates.copy(list = viewStates.list + list)
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

class SignListModel {
    var uid = ""
    var no = ""
    var name = ""
    var content = ""
    var time = ""
    var bits = ""
}
