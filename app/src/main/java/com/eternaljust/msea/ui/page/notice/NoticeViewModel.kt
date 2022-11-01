package com.eternaljust.msea.ui.page.notice

import androidx.lifecycle.ViewModel

class NoticeViewModel : ViewModel() {
    val items: List<NoticeTabItem>
    get() = listOf(
        NoticeTabItem.MYPOST,
        NoticeTabItem.INTERACTIVE,
        NoticeTabItem.SYSTEM
    )
}

interface NoticeTab {
    val id: String
    val title: String
}

enum class NoticeTabItem : NoticeTab {
    MYPOST {
        override val id: String
            get() = "mypost"

        override val title: String
            get() = "我的帖子"
    },

    INTERACTIVE {
        override val id: String
            get() = "interactive"

        override val title: String
            get() = "坛友互动"
    },

    SYSTEM {
        override val id: String
            get() = "system"

        override val title: String
            get() = "系统提醒"
    }
}