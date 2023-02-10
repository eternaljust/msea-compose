package com.eternaljust.msea.ui.page.home.search

import androidx.lifecycle.ViewModel

class SearchPostViewModel: ViewModel() {
    private var keyword = ""
    private var page = 1

    fun dispatch(action: SearchPostAction) {
        when (action) {
            is SearchPostAction.SearchKeyword -> {
                if (keyword != action.content) {
                    keyword = action.content
                    if (action.content.isNotEmpty()) {
                        page = 1
                        loadMoreData()
                    }
                }
            }
            is SearchPostAction.LoadMoreData -> {
                if (keyword.isNotEmpty()) {
                    page += 1
                    loadMoreData()
                }
            }
        }
    }

    private fun loadMoreData() {
        println("---开始搜索帖子：$keyword")
    }
}

sealed class SearchPostAction {
    object LoadMoreData: SearchPostAction()

    data class SearchKeyword(val content: String) : SearchPostAction()
}