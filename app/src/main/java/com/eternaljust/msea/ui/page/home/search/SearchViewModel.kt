package com.eternaljust.msea.ui.page.home.search

import androidx.lifecycle.ViewModel

class SearchViewModel : ViewModel() {
    val items: List<SearchTabItem>
        get() = listOf(
            SearchTabItem.POST,
            SearchTabItem.USER
        )
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