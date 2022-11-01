package com.eternaljust.msea.ui.page.notice.post

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.eternaljust.msea.utils.HTMLURL
import com.eternaljust.msea.utils.NetworkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class MyPostViewModel : ViewModel() {
    private val pager by lazy {
        Pager(
            PagingConfig(pageSize = 30, prefetchDistance = 1)
        ) {
            MyPostListSource()
        }.flow.cachedIn(viewModelScope)
    }

    var viewStates by mutableStateOf(MyPostListViewState(pagingData = pager))
        private set
}

data class MyPostListViewState(
    val pagingData: PagingPostList
)

class PostListModel {
    var pid = ""
    var uid = ""
    var avatar = ""
    var name = ""
    var time = ""
    var title = ""
}

typealias PagingPostList = Flow<PagingData<PostListModel>>

class MyPostListSource : PagingSource<Int, PostListModel>() {
    private suspend fun loadData(page: Int) : List<PostListModel> {
        val list = mutableListOf<PostListModel>()

        withContext(Dispatchers.IO) {
            val url = HTMLURL.MY_POST_LIST + "&page=${page}"
            val document = NetworkUtil.getRequest(url)
            val dl = document.selectXpath("//dl[@class='cl ']")

            dl.forEach {
                println("mypost----")
                println(it.html())
                var post = PostListModel()
                val time = it.selectXpath("dt/span[@class='xg1 xw0']").text()
                if (time.isNotEmpty()) {
                    post.time = time
                }
                val avatar = it.selectXpath("dd[@class='m avt mbn']/a/img").attr("src")
                println("avatar---${avatar}")
                if (avatar.isNotEmpty()) {
                    post.avatar = NetworkUtil.getAvatar(avatar)
                    println("post.avatar---${post.avatar}")
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
                // forum.php?mod=redirect&goto=findpost&ptid=11825&pid=183809
                val thread = it.selectXpath("dd[@class='ntc_body']/a[2]").attr("href")
                if (thread.isNotEmpty() && thread.contains("&pid=")) {
                    post.pid = thread.split("&pid=").last()
                }

                list.add(post)
            }
        }

        return list
    }

    override fun getRefreshKey(state: PagingState<Int, PostListModel>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PostListModel> {
        return try {
            val nextPage = params.key ?: 1
            val data = loadData(page = nextPage)

            LoadResult.Page(
                data = data,
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = if (data.isEmpty()) null else nextPage + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}