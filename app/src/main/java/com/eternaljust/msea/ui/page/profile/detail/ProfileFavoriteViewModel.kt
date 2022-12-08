package com.eternaljust.msea.ui.page.profile.detail

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

class ProfileFavoriteViewModel : ViewModel() {
    private val pager by lazy {
        configPager(PagingConfig(pageSize = 30, prefetchDistance = 1)) {
            loadData(page = it)
        }
    }

    var viewStates by mutableStateOf(ProfileFavoriteListViewState(pagingData = pager))
        private set

    private val _viewEvents = Channel<ProfileFavoriteListViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: ProfileFavoriteListViewAction) {
        when (action) {
            is ProfileFavoriteListViewAction.PopBack -> popBack()
        }
    }

    private suspend fun loadData(page: Int) : List<ProfileFavoriteListModel> {
        val list = mutableListOf<ProfileFavoriteListModel>()

        withContext(Dispatchers.IO) {
            val url = HTMLURL.PROFILE_FAVORITE_LIST + "&page=${page}"
            val document = NetworkUtil.getRequest(url)
            val li = document.selectXpath("//ul[@id='favorite_ul']/li")

            li.forEach {
                println("li---${it.html()}")
                var system = ProfileFavoriteListModel()
                val time = it.selectXpath("span[@class='xg1']").text()
                if (time.isNotEmpty()) {
                    system.time = time
                }
                val title = it.selectXpath("a[last()]").text()
                if (title.isNotEmpty()) {
                    system.title = title
                }
                val tid = it.selectXpath("a[last()]").attr("href")
                if (tid.contains("thread-")) {
                    system.tid = tid.split("thread-").last()
                        .split("-").first()
                }

                list.add(system)
            }
        }

        return list
    }

    private fun popBack() {
        viewModelScope.launch {
            _viewEvents.send(ProfileFavoriteListViewEvent.PopBack)
        }
    }
}

data class ProfileFavoriteListViewState(
    val pagingData: Flow<PagingData<ProfileFavoriteListModel>>
)

sealed class ProfileFavoriteListViewEvent {
    object PopBack : ProfileFavoriteListViewEvent()
}

sealed class ProfileFavoriteListViewAction {
    object PopBack: ProfileFavoriteListViewAction()
}

class ProfileFavoriteListModel {
    var time = ""
    var title = ""
    var tid = ""
}