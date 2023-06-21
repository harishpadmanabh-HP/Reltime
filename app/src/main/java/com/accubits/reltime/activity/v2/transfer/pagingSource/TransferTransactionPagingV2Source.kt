package com.accubits.reltime.activity.v2.transfer.pagingSource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.models.*
import com.accubits.reltime.repository.ReltimeRepository
import dagger.Module
import dagger.Provides

class TransferTransactionPagingV2Source(
    val repository: ReltimeRepository,
    val preferenceManager: PreferenceManager,
) : PagingSource<Int, RowData>() {
    private var onItemCount: ((Int) -> Unit)? = null
    var limit: String = ""
    fun setData(
        limits: String
    ) {
        limit = limits
    }

    override fun getRefreshKey(state: PagingState<Int, RowData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RowData> {
        val nextPage = params.key ?: 1

        return try {
            val response = repository.getPaginatedTransferTransaction(
                preferenceManager.getApiKey(),
                nextPage.toString(), limit
            )

            onItemCount?.let { it1 -> it1(response.result.count) }

            LoadResult.Page(
                data = response.result.result,
                prevKey = if (response.result.count == 0) null else {
                    if (nextPage == 1) null else nextPage - 1
                },
                nextKey = if (response.result.count.toInt() == 0) null else {
                    if (nextPage == response.result.count) null else response.result.currentPage.toInt()
                        .plus(
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
