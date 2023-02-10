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

class SearchUserViewModel: ViewModel() {
    var viewStates by mutableStateOf(SearchUserViewState())
        private set
    private var keyword = ""

    fun dispatch(action: SearchUserAction) {
        when (action) {
            is SearchUserAction.SearchKeyword -> {
                if (keyword != action.content) {
                    keyword = action.content
                    if (action.content.isNotEmpty()) {
                        loadMoreData()
                    }
                }
            }
        }
    }

    private fun loadMoreData() {
        println("---开始搜索用户：$keyword")
        val list = mutableListOf<UserListModel>()
        viewModelScope.launch(Dispatchers.IO) {
            val url = HTMLURL.SEARCH_USER + "&username=$keyword"
            val document = NetworkUtil.getRequest(url)
            val lis = document.selectXpath("//li[@class='bbda cl']")
            lis.forEach {
                var user = UserListModel()
                val avatar = it.selectXpath("div[@class='avt']/a/img").attr("src")
                if (avatar.isNotEmpty()) {
                    user.avatar = NetworkUtil.getAvatar(avatar)
                }
                val name = it.selectXpath("h4/a").attr("title")
                if (name.isNotEmpty()) {
                    user.name = name
                }
                val content = it.selectXpath("p[@class='maxh']").text()
                if (content.isNotEmpty()) {
                    user.content = content.replace("\r\n", "")
                }
                val uid = it.selectXpath("h4/a").attr("href")
                if (uid.isNotEmpty()) {
                    user.uid = NetworkUtil.getUid(uid)
                }
                println("user=${user.avatar}-${user.name}-${user.content}-${user.uid}")
                list.add(user)
            }

            viewStates = viewStates.copy(list = list)
        }
    }
}

data class SearchUserViewState(
    val list: List<UserListModel> = emptyList()
)
sealed class SearchUserAction {
    data class SearchKeyword(val content: String) : SearchUserAction()
}

class UserListModel {
    var uid = ""
    var avatar = ""
    var content = ""
    var name = ""
}
