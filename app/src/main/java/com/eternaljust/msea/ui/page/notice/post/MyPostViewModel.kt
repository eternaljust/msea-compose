package com.eternaljust.msea.ui.page.notice.post

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.paging.*
import com.eternaljust.msea.utils.HTMLURL
import com.eternaljust.msea.utils.NetworkUtil
import com.eternaljust.msea.utils.configPager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class MyPostViewModel : ViewModel() {
    private val pager by lazy {
        configPager(PagingConfig(pageSize = 30, prefetchDistance = 1)) {
            loadData(page = it)
        }
    }

    var viewStates by mutableStateOf(MyPostListViewState(pagingData = pager))
        private set

    private suspend fun loadData(page: Int) : List<PostListModel> {
        val list = mutableListOf<PostListModel>()

        withContext(Dispatchers.IO) {
            val url = HTMLURL.MY_POST_LIST + "&page=${page}"
            val document = NetworkUtil.getRequest(url)
            val dl = document.selectXpath("//dl[@class='cl ']")

            dl.forEach {
                var post = PostListModel()
                val time = it.selectXpath("dt/span[@class='xg1 xw0']").text()
                if (time.isNotEmpty()) {
                    post.time = time
                }
                val avatar = it.selectXpath("dd[@class='m avt mbn']/a/img").attr("src")
                if (avatar.isNotEmpty()) {
                    post.avatar = NetworkUtil.getAvatar(avatar)
                }
                val name = it.selectXpath("dd[@class='ntc_body']/a[1]").text()
                if (name.isNotEmpty()) {
                    post.name = name
                }
                val href = it.selectXpath("dd[@class='ntc_body']/a[1]").attr("href")
                if (href.isNotEmpty()) {
                    post.uid = NetworkUtil.getUid(href)
                }
                val title = it.selectXpath("dd[@class='ntc_body']/a[2]").text()
                if (title.isNotEmpty()) {
                    post.title = title
                }
                val thread = it.selectXpath("dd[@class='ntc_body']/a[2]").attr("href")
                if (thread.contains("&ptid=")) {
                    post.ptid = thread.split("&ptid=").last()
                        .split("&pid=").first()
                }

                list.add(post)
            }
        }

        return list
    }
}

data class MyPostListViewState(
    val pagingData: Flow<PagingData<PostListModel>>
)

class PostListModel {
    var ptid = ""
    var uid = ""
    var avatar = ""
    var name = ""
    var time = ""
    var title = ""
}
