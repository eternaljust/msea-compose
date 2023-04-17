package com.eternaljust.msea.ui.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WebViewModel(
    var url: String = "",
    var title: String = "",
    var tid: String = ""
) : Parcelable

@Parcelize
data class TopicDetailRouteModel(
    var tid: String = "",
    var isNodeFid125: Boolean = false
) : Parcelable

@Parcelize
data class ConfigVersionModel(
    var versionCode: Int = 0,
    var versionName: String = "",
    var versionContent: String = "",
    var updateTime: String = ""
) : Parcelable

@Parcelize
data class TagItemModel(
    var tid: String = "",
    var title: String = ""
) : Parcelable
