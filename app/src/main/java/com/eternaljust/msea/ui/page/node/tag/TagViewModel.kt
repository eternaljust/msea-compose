package com.eternaljust.msea.ui.page.node.tag

import android.os.Parcelable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eternaljust.msea.utils.HTMLURL
import com.eternaljust.msea.utils.NetworkUtil
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TagViewModel : ViewModel() {
    var viewStates by mutableStateOf(TagViewState())
        private set
    private val _viewEvents = Channel<TagViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: TagViewAction) {
        when (action) {
            TagViewAction.GetTagList -> loadData()
            TagViewAction.PopBack -> popBack()
        }
    }

    private fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = mutableListOf<TagItemModel>()

            val url = HTMLURL.TAG_LIST
            val document = NetworkUtil.getRequest(url)
            var taglist = document.selectXpath("//div[@class='taglist mtm mbm']/a")

            taglist.forEach {
                var item = TagItemModel()

                val title = it.attr("title")
                if (title.isNotEmpty()) {
                    item.title = title
                }
                val href = it.attr("href")
                if (href.contains("id=")) {
                    item.tid = href.split("id=").last()
                }

                list.add(item)
            }

            viewStates = viewStates.copy(list = list)
        }
    }

    private fun popBack() {
        viewModelScope.launch {
            _viewEvents.send(TagViewEvent.PopBack)
        }
    }
}

data class TagViewState(
    val list: List<TagItemModel> = emptyList()
)

sealed class TagViewEvent {
    object PopBack : TagViewEvent()
}

sealed class TagViewAction {
    object GetTagList : TagViewAction()
    object PopBack: TagViewAction()
}

@Parcelize
data class TagItemModel(
    var tid: String = "",
    var title: String = ""
) : Parcelable
