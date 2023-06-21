package com.accubits.reltime.views.borrow.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.models.RowMyBorrowings
import com.accubits.reltime.repository.ReltimeRepository

class BorrowPagingSource(
    val repository: ReltimeRepository,
    val preferenceManager: PreferenceManager,
) : PagingSource<Int, RowMyBorrowings>() {
    private var onItemCount: ((Int) -> Unit)? = null
    var searchQuerry: String = ""
    var amountFrom: String = ""
    var amount_to: String = ""
    var date_from: String = ""
    var date_to: String = ""
    var instalment: String = ""
    fun setData(
        data: String,
        amountFroms: String,
        amount_tos: String,
        date_froms: String,
        date_tos: String,
        instalments: String,
    ) {
        searchQuerry = data
        amountFrom = amountFroms
        amount_to = amount_tos
        date_from = date_froms
        date_to = date_tos
        instalment = instalments
    }

    override fun getRefreshKey(state: PagingState<Int, RowMyBorrowings>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RowMyBorrowings> {
        val nextPage = params.key ?: 1
        return try {
            val response = repository.getAllPaginatedBorrowings(
                preferenceManager.getApiKey(),
                searchQuerry,
                nextPage.toString(), amountFrom, amount_to, date_from, date_to, instalment
            )

            onItemCount?.let { it1 -> it1(response.result.count) }
            LoadResult.Page(
                data = response.result.row,
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