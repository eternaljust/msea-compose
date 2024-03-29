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
import java.util.UUID

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
                val isForum = it.html().contains("您的主题") && it.html().contains("移动到")
                val post = PostListModel()
                val time = it.selectXpath("dt/span[@class='xg1 xw0']").text()
                if (time.isNotEmpty()) {
                    post.time = time
                }
                if (isForum) {
                    val avatar = it.selectXpath("dd[@class='m avt mbn']/img").attr("src")
                    if (avatar.isNotEmpty()) {
                        post.avatar = NetworkUtil.getAvatar(avatar)
                    }
                } else {
                    val avatar = it.selectXpath("dd[@class='m avt mbn']/a/img").attr("src")
                    if (avatar.isNotEmpty()) {
                        post.avatar = NetworkUtil.getAvatar(avatar)
                    }
                }
                val namePath = if (!isForum) "dd[@class='ntc_body']/a[1]" else
                    "dd[@class='ntc_body']/a[2]"
                val name = it.selectXpath(namePath).text()
                if (name.isNotEmpty()) {
                    post.name = name
                }
                val href = it.selectXpath(namePath).attr("href")
                if (href.contains("uid-")) {
                    post.uid = NetworkUtil.getUid(href)
                }
                val threadPath = if (!isForum) "dd[@class='ntc_body']/a[2]" else
                    "dd[@class='ntc_body']/a[1]"
                val title = it.selectXpath(threadPath).text()
                if (title.isNotEmpty()) {
                    post.title = title
                }
                val thread = it.selectXpath(threadPath).attr("href")
                if (thread.isNotEmpty()) {
                    post.ptid = NetworkUtil.getTid(thread)
                }
                if (isForum) {
                    val forumPath = "dd[@class='ntc_body']/a[3]"
                    val forum = it.selectXpath(forumPath).text()
                    if (forum.isNotEmpty()) {
                        post.forum = forum
                    }
                    val id = it.selectXpath(forumPath).attr("href")
                    post.fid = NetworkUtil.getFid(id)
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
    val uuid = UUID.randomUUID()
    var fid = ""
    var ptid = ""
    var uid = ""
    var avatar = ""
    var name = ""
    var time = ""
    var title = ""
    var forum = ""
}
