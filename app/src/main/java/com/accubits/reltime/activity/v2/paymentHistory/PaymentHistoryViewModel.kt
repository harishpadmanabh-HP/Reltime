package com.accubits.reltime.activity.v2.paymentHistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.models.MyBorrowingsSuccessModel
import com.accubits.reltime.models.PaymentHistorySuccessModel
import com.accubits.reltime.repository.ReltimeRepository
import com.accubits.reltime.views.borrow.paging.BorrowPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentHistoryViewModel@Inject constructor(
    private var reltimeRepository: ReltimeRepository,
    private var borrowPagingSource: BorrowPagingSource
): ViewModel() {

    var paymentHistorySuccessModel =
        MutableStateFlow<ApiMapper<PaymentHistorySuccessModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )

    fun getMyLoanHistory(token: String, page: String, loanId: String) {
        paymentHistorySuccessModel = MutableStateFlow<ApiMapper<PaymentHistorySuccessModel>>(
            ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
        )
        viewModelScope.launch(Dispatchers.IO) {
            val loginResponse = reltimeRepository.getPaymentHistory(
                token =token,
                page = page,
                id = loanId,
            )
            when (loginResponse.status) {
                ApiCallStatus.SUCCESS -> {
                    paymentHistorySuccessModel.value =
                        ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                }
                ApiCallStatus.ERROR -> {
                    paymentHistorySuccessModel.value =
                        ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                }
                else -> {}
            }
        }
    }
}