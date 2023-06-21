package com.accubits.reltime.views.lend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.models.MyBorrowingsSuccessModel
import com.accubits.reltime.models.TransactionApprovalSuccessModel
import com.accubits.reltime.repository.ReltimeRepository
import com.accubits.reltime.views.lend.pageing.AllLendingPagingSource
import com.accubits.reltime.views.lend.pageing.LendingHistoryPagingSource
import com.accubits.reltime.views.lend.pageing.TokenPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListedTokenViewModel @Inject constructor(
    private val reltimeRepository: ReltimeRepository,
    private val tokenPagingSource: TokenPagingSource,
    private val allLendingPagingSource: AllLendingPagingSource,
    private val lendingHistoryPagingSource: LendingHistoryPagingSource
) :
    ViewModel() {
    var search = MutableStateFlow<String>("")

    var borrowingSuccess =
        MutableStateFlow<ApiMapper<MyBorrowingsSuccessModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )
    var transactionApprovalSuccessModel =
        MutableStateFlow<ApiMapper<TransactionApprovalSuccessModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )

    var getLendingSuccessModel =
        MutableStateFlow<ApiMapper<MyBorrowingsSuccessModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )


    fun getPagedData() = search.flatMapLatest { querry ->
        Pager(PagingConfig(20), pagingSourceFactory = {
            tokenPagingSource
        }).flow
    }

    fun getAllLendingPagedData() = search.flatMapLatest { querry ->
        Pager(PagingConfig(20), pagingSourceFactory = {
            allLendingPagingSource
        }).flow
    }

    fun getLendingHistoryPagedData() = search.flatMapLatest { querry ->
        Pager(PagingConfig(20), pagingSourceFactory = {
            lendingHistoryPagingSource
        }).flow
    }
    fun getBorrowing(token: String) {
        borrowingSuccess = MutableStateFlow<ApiMapper<MyBorrowingsSuccessModel>>(
            ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
        )
        viewModelScope.launch {
            val loginResponse = reltimeRepository.getBorrowing(token)
            when (loginResponse.status) {
                ApiCallStatus.SUCCESS -> {
                    borrowingSuccess.value =
                        ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                }
                ApiCallStatus.ERROR -> {
                    borrowingSuccess.value =
                        ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                }
                else -> {}
            }
        }
    }

    fun deleteItemID(token: String, id: String) {
        transactionApprovalSuccessModel =
            MutableStateFlow<ApiMapper<TransactionApprovalSuccessModel>>(
                ApiMapper(
                    ApiCallStatus.LOADING,
                    null,
                    null
                )
            )
        viewModelScope.launch {
            try {
                val loginResponse = reltimeRepository.deleteLendItem(token, id)
                when (loginResponse.status) {
                    ApiCallStatus.SUCCESS -> {
                        transactionApprovalSuccessModel.value =
                            ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                    }
                    ApiCallStatus.ERROR -> {
                        transactionApprovalSuccessModel.value =
                            ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                    }
                    else -> {}
                }
            }catch (e:Exception){
                transactionApprovalSuccessModel.value =
                    ApiMapper(ApiCallStatus.ERROR, null, e.localizedMessage)
            }
        }
    }

    fun getLendDetail(token: String, id: String) {
        getLendingSuccessModel =
            MutableStateFlow<ApiMapper<MyBorrowingsSuccessModel>>(
                ApiMapper(
                    ApiCallStatus.LOADING,
                    null,
                    null
                )
            )
        viewModelScope.launch {
            val loginResponse = reltimeRepository.getLendItem(token, id)
            when (loginResponse.status) {
                ApiCallStatus.SUCCESS -> {
                    getLendingSuccessModel.value =
                        ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                }
                ApiCallStatus.ERROR -> {
                    getLendingSuccessModel.value =
                        ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                }
                else -> {}
            }
        }
    }


}