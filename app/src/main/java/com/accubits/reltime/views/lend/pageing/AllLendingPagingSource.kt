package com.accubits.reltime.views.lend.pageing

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.models.RowAllLending
import com.accubits.reltime.repository.ReltimeRepository

class AllLendingPagingSource(
    val repository: ReltimeRepository,
    val preferenceManager: PreferenceManager,
) : PagingSource<Int, RowAllLending>() {
    private var onItemCount: ((Int) -> Unit)? = null
    var searchQuerry: String = ""
    var amount_from = ""
    var amount_to = ""
    var installments_from = ""
    var installments_to = ""
    var interest_rate = ""
    var no_collateral = ""
    var collateral_from = ""
    var collateral_to = ""
    var amount_sort = ""
    var interest_rate_sort = ""
    var loan_term_sort = ""
    fun setData(
        data: String,
         amount_from: String,
         amount_to : String,
         installments_from: String,
         installments_to: String,
         interest_rate: String,
         no_collateral : String,
         collateral_from : String,
         collateral_to: String,
         amount_sort: String,
         interest_rate_sort: String,
         loan_term_sort: String,
    ) {
        searchQuerry = data
        this.amount_from =amount_from
        this.amount_to =amount_to
        this.installments_from = installments_from
       this.installments_to = installments_to
       this.interest_rate =interest_rate
       this.no_collateral =no_collateral
       this.collateral_from = collateral_from
       this.collateral_to = collateral_to
       this.amount_sort = amount_sort
       this.interest_rate_sort = interest_rate_sort
       this.loan_term_sort = loan_term_sort
    }

    override fun getRefreshKey(state: PagingState<Int, RowAllLending>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RowAllLending> {
        val nextPage = params.key ?: 1
        return try {
            val response = repository.getPaginatedAllLending(
                preferenceManager.getApiKey(),
                searchQuerry,
                nextPage.toString(),  amount_from,
                amount_to ,
                installments_from,
                installments_to,
                interest_rate,
                no_collateral ,
                collateral_from ,
                collateral_to,
                amount_sort,
                interest_rate_sort,
                loan_term_sort,
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