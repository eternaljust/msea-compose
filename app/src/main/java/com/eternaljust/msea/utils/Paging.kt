package com.eternaljust.msea.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import kotlinx.coroutines.flow.Flow

fun <T : Any> ViewModel.configPager(
    config: PagingConfig,
    callAction: suspend (page: Int) -> List<T>
): Flow<PagingData<T>> {
    return pager(config) {
        val nextPage = it.key ?: 1
        val response = try {
            HttpResult.Success(callAction.invoke(nextPage))
        } catch (e: Exception) {
            HttpResult.Error(e)
        }
        when (response) {
            is HttpResult.Success -> {
                val data = response.result
                PagingSource.LoadResult.Page(
                    data = data,
                    prevKey = if (nextPage == 1) null else nextPage - 1,
                    nextKey = if (data.isEmpty()) null else nextPage + 1
                )
            }
            is HttpResult.Error -> {
                PagingSource.LoadResult.Error(response.exception)
            }
        }
    }
}

fun <K : Any, V : Any> ViewModel.pager(
    config: PagingConfig,
    loadData: suspend (PagingSource.LoadParams<K>) -> PagingSource.LoadResult<K, V>
): Flow<PagingData<V>> {
    val baseConfig = PagingConfig(
        config.pageSize,
        initialLoadSize = config.initialLoadSize,
        prefetchDistance = config.prefetchDistance,
        maxSize = config.maxSize,
        enablePlaceholders = config.enablePlaceholders
    )
    return Pager(
        config = baseConfig
    ) {
        object : PagingSource<K, V>() {
            override suspend fun load(params: LoadParams<K>): LoadResult<K, V> {
                return loadData.invoke(params)
            }

            override fun getRefreshKey(state: PagingState<K, V>): K? = null
        }
    }.flow.cachedIn(viewModelScope)
}