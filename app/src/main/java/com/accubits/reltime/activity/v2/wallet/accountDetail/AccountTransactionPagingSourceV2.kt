package com.accubits.reltime.activity.v2.wallet.accountDetail

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.models.TransactionItem
import com.accubits.reltime.repository.ReltimeRepository

class AccountTransactionPagingSourceV2(
    val repository: ReltimeRepository,
    val preferenceManager: PreferenceManager,
) : PagingSource<Int, TransactionItem>() {
    private var onItemCount: ((Int) -> Unit)? = null
    var searchQuerry: String = ""
    var limit: Int? = null
    var account_id: Int? = null
    var type: String = ""
    var status: String = ""
 //   var date_range: Int? = null

    fun setData(
        data: String,
        limits: Int?,
        accountId: Int?,
        accountType: String,
        transactionStatus: String,
       // dateRange: Int?
    ) {
        searchQuerry = data
        limit = limits
        account_id = accountId
        type= accountType
        status = transactionStatus
      //  date_range = dateRange
    }

    override fun getRefreshKey(state: PagingState<Int, TransactionItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TransactionItem> {
        val nextPage = params.key ?: 1
        return try {
            val response = repository.getTransactionHistoryV2(
                preferenceManager.getApiKey(),
                searchQuerry,
                nextPage.toString(), limit, account_id,
                type,
                status,
             //   date_range
            )

            onItemCount?.let { it1 -> response.result?.count?.let { it1(it) } }
            response.result?.count?.toString()?.let { Log.d("count!!", it) }

            LoadResult.Page(
                data = response.result!!.row,
                prevKey = if (response.result.numPages == 0) null else {
                    if (nextPage == 1) null else nextPage - 1
                },
                nextKey = if (response.result.numPages == 0) null else {
                    if (nextPage == response.result.numPages) null else response.result.currentPage!!.plus(
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
