package com.accubits.reltime.views.lend.pageing

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.RowAllLending
import com.accubits.reltime.repository.ReltimeRepository

class LendingHistoryPagingSource(
    val repository: ReltimeRepository,
    val preferenceManager: PreferenceManager,
) : PagingSource<Int, RowAllLending>() {
    private var onItemCount: ((Int) -> Unit)? = null


    override fun getRefreshKey(state: PagingState<Int, RowAllLending>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RowAllLending> {
        val nextPage = params.key ?: 1
        return try {
            val response = Utils.LendId?.let {
                repository.getLendingHistory(
                    preferenceManager.getApiKey(), it
                )
            }

            onItemCount?.let { it1 ->
                if (response != null) {
                    it1(response.result.count)
                }
            }

            LoadResult.Page(
                data = response?.result?.row!!,
                prevKey = if (response.result.num_pages == 0) null else {
                    if (nextPage == 1) null else nextPage - 1
                },
                nextKey = if (response.result.num_pages == 0) null else {
                    if (nextPage == response.result.num_pages) null else response.result.current_page.plus(
                        1
                    )
                }
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }


    }

    fun getCountItem(listener: (Int) -> Unit) {
        onItemCount = listener
    }
}