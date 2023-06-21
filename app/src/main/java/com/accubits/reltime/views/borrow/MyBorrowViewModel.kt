package com.accubits.reltime.views.borrow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.models.*
import com.accubits.reltime.repository.ReltimeRepository
import com.accubits.reltime.views.borrow.paging.BorrowPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyBorrowViewModel @Inject constructor(
    private var reltimeRepository: ReltimeRepository,
    private var borrowPagingSource: BorrowPagingSource
) :
    ViewModel() {
    var myBorrowSuccessModel =
        MutableStateFlow<ApiMapper<MyBorrowSuccessModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )
    var siginInTransactionSuccessModel =
        MutableStateFlow<ApiMapper<SiginInTransactionSuccessModel>>(
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
    var transactionApprovalSuccessModelInstalment =
        MutableStateFlow<ApiMapper<TransactionApprovalSuccessModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )
    var transactionApprovalFIrstSuccessModel =
        MutableStateFlow<ApiMapper<TransactionApprovalSuccessModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )
    var search = MutableStateFlow<String>("")


    fun getBorrowList() = search.flatMapLatest { querry ->
        Pager(PagingConfig(20), pagingSourceFactory = {
            borrowPagingSource
        }).flow
    }

    fun getMyBorrow(token: String, id: String) {
        myBorrowSuccessModel = MutableStateFlow<ApiMapper<MyBorrowSuccessModel>>(
            ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
        )
        viewModelScope.launch(Dispatchers.IO) {
            val loginResponse = reltimeRepository.getMyBorrowingItem(token, id)
            when (loginResponse.status) {
                ApiCallStatus.SUCCESS -> {
                    myBorrowSuccessModel.value =
                        ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                }
                ApiCallStatus.ERROR -> {
                    myBorrowSuccessModel.value =
                        ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                }
                else -> {}
            }
        }
    }

    fun doCloseOrAcceptColatral(token: String, collatralRequestModel: CollatralRequestModel) {
        transactionApprovalSuccessModel =
            MutableStateFlow<ApiMapper<TransactionApprovalSuccessModel>>(
                ApiMapper(
                    ApiCallStatus.LOADING,
                    null,
                    null
                )
            )
        viewModelScope.launch(Dispatchers.IO) {
            val loginResponse =
                reltimeRepository.doAcceptOrRejectCollatral(token, collatralRequestModel)
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
        }
    }

    fun doAcceptOrRejectBorrow(token: String, collatralRequestModel: NormalBorrowRequestModel) {
        transactionApprovalSuccessModel =
            MutableStateFlow<ApiMapper<TransactionApprovalSuccessModel>>(
                ApiMapper(
                    ApiCallStatus.LOADING,
                    null,
                    null
                )
            )
        viewModelScope.launch(Dispatchers.IO) {
            val loginResponse =
                reltimeRepository.doAcceptOrRejectBorrow(token, collatralRequestModel)
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
        }
    }

    fun doWalletApproval(token: String, walletCollatral: WalletCollatral) {
        transactionApprovalSuccessModel =
            MutableStateFlow<ApiMapper<TransactionApprovalSuccessModel>>(
                ApiMapper(
                    ApiCallStatus.LOADING,
                    null,
                    null
                )
            )
        viewModelScope.launch(Dispatchers.IO) {
            val loginResponse = reltimeRepository.doWalletCollatral(token, walletCollatral)
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
        }
    }


    fun doWalletApprovalInstallement(token: String, walletCollatral: InstallementRequest) {
        transactionApprovalSuccessModelInstalment =
            MutableStateFlow<ApiMapper<TransactionApprovalSuccessModel>>(
                ApiMapper(
                    ApiCallStatus.LOADING,
                    null,
                    null
                )
            )
        viewModelScope.launch(Dispatchers.IO) {
            val loginResponse = reltimeRepository.doInstallmentAndCloseLoan(token, walletCollatral)
            when (loginResponse.status) {
                ApiCallStatus.SUCCESS -> {
                    transactionApprovalSuccessModelInstalment.value =
                        ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                }
                ApiCallStatus.ERROR -> {
                    transactionApprovalSuccessModelInstalment.value =
                        ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                }
                else -> {}
            }
        }
    }


    fun doSiginTransaction(token: String, signTransactionRequest: SignTransactionRequest) {
        siginInTransactionSuccessModel =
            MutableStateFlow<ApiMapper<SiginInTransactionSuccessModel>>(
                ApiMapper(
                    ApiCallStatus.LOADING,
                    null,
                    null
                )
            )
        viewModelScope.launch {
            val loginResponse =
                reltimeRepository.doTransactionSiginInApi(token, signTransactionRequest)
            when (loginResponse.status) {
                ApiCallStatus.SUCCESS -> {
                    siginInTransactionSuccessModel.value =
                        ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                }
                ApiCallStatus.ERROR -> {
                    siginInTransactionSuccessModel.value =
                        ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                }
                else -> {}
            }
        }
    }

}