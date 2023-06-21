package com.accubits.reltime.stripe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.models.*
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StripeViewModel @Inject constructor(private var reltimeRepository: ReltimeRepository) :
    ViewModel() {

    var transactionSuccesFlow = MutableStateFlow<ApiMapper<TrasactionSuccessModel>>(
        ApiMapper(
            ApiCallStatus.EMPTY,
            null,
            null
        )
    )
    var loanCloseSuccessModel = MutableStateFlow<ApiMapper<TransactionApprovalSuccessModel>>(
        ApiMapper(
            ApiCallStatus.EMPTY,
            null,
            null
        )
    )

    fun getTransaction(token: String, reccipetApiRequest: ReccipetApiRequest) {
        transactionSuccesFlow = MutableStateFlow<ApiMapper<TrasactionSuccessModel>>(
            ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
        )
        viewModelScope.launch {
            val response = reltimeRepository.setTransactionToServer(token, reccipetApiRequest)
            when (response.status) {
                ApiCallStatus.SUCCESS -> {
                    transactionSuccesFlow.value =
                        ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                }
                ApiCallStatus.ERROR -> {
                    transactionSuccesFlow.value =
                        ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                }
                else -> {}
            }
        }
    }

    fun doCloseLoan(token: String, loanRequest: RequestLoanUpdateRequest) {
        loanCloseSuccessModel = MutableStateFlow<ApiMapper<TransactionApprovalSuccessModel>>(
            ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
        )
        viewModelScope.launch {
            val response = reltimeRepository.postLoanClose(token, loanRequest)
            when (response.status) {
                ApiCallStatus.SUCCESS -> {
                    loanCloseSuccessModel.value =
                        ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                }
                ApiCallStatus.ERROR -> {
                    loanCloseSuccessModel.value =
                        ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                }
                else -> {}
            }
        }
    }
    fun doCloseLoanv2(token: String, loanRequest: NewInstallementRequest) {
        loanCloseSuccessModel = MutableStateFlow<ApiMapper<TransactionApprovalSuccessModel>>(
            ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
        )
        viewModelScope.launch {
            val response = reltimeRepository.postLoanCloseV2(token, loanRequest)
            when (response.status) {
                ApiCallStatus.SUCCESS -> {
                    loanCloseSuccessModel.value =
                        ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                }
                ApiCallStatus.ERROR -> {
                    loanCloseSuccessModel.value =
                        ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                }
                else -> {}
            }
        }
    }
}