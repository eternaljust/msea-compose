package com.eternaljust.msea.ui.page.home

import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    val topicItems: List<TopicTabItem>
        get() = listOf(
            TopicTabItem.NEW,
            TopicTabItem.HOT,
            TopicTabItem.NEWTHREAD,
            TopicTabItem.SOFA
        )
}

interface TopicTab {
    val id: String
    val title: String
}

enum class TopicTabItem : TopicTab {
    NEW{
        override val id: String
            get() = "new"

        override val title: String
            get() = "最新回复"
    },

    HOT{
        override val id: String
            get() = "hot"

        override val title: String
            get() = "热门"
    },

    NEWTHREAD{
        override val id: String
            get() = "newthread"

        override val title: String
            get() = "最新发表"
    },

    SOFA{
        override val id: String
            get() = "sofa"

        override val title: String
            get() = "前排"
    }
}