package com.eternaljust.msea.ui.page.home.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    val items: List<SearchTabItem>
        get() = listOf(
            SearchTabItem.POST,
            SearchTabItem.USER
        )

    var viewStates by mutableStateOf(SearchViewStates())
        private set
    private val _viewEvents = Channel<SearchViewEvent>(Channel.BUFFERED)
    val viewEvents = _viewEvents.receiveAsFlow()

    fun dispatch(action: SearchViewAction) {
        when (action) {
            is SearchViewAction.PopBack -> {
                viewModelScope.launch {
                    _viewEvents.send(SearchViewEvent.PopBack)
                }
            }
            is SearchViewAction.SearchKeyboard -> {
                viewModelScope.launch {
                    _viewEvents.send(SearchViewEvent.SearchKeyboard)
                }
                viewStates = viewStates.copy(searchContent = viewStates.keyword)
            }
            is SearchViewAction.UpdateKeyword -> {
                viewStates = viewStates.copy(keyword = action.content)
            }
        }
    }
}

data class SearchViewStates(
    val keyword: String = "",
    val searchContent: String = ""
)

sealed class SearchViewEvent {
    object PopBack: SearchViewEvent()
    object SearchKeyboard: SearchViewEvent()
}

sealed class SearchViewAction {
    object PopBack: SearchViewAction()
    object SearchKeyboard: SearchViewAction()

    data class UpdateKeyword(val content: String) : SearchViewAction()
}

interface SearchTab {
    val id: String
    val title: String
}

enum class SearchTabItem : SearchTab {
    POST {
        override val id: String
            get() = "post"

        override val title: String
            get() = "帖子"
    },

    USER {
        override val id: String
            get() = "user"

        override val title: String
            get() = "用户"
    }
}