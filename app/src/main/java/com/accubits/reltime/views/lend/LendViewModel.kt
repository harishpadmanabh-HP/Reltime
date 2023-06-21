package com.accubits.reltime.views.lend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.models.*
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LendViewModel @Inject constructor(
    private
    val reltimeRepository: ReltimeRepository
) : ViewModel() {
    var lendingSuccessModel =
        MutableStateFlow<ApiMapper<LendingSuccessModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )

    var transactionApproveSuccessModel =
        MutableStateFlow<ApiMapper<TransactionApprovalSuccessModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )
    var commissionSuccessModel =
        MutableStateFlow<ApiMapper<CommissionSuccessModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )

    var siginInTransctionSuccess =
        MutableStateFlow<ApiMapper<SiginInTransactionSuccessModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )

    var contactList =
        MutableStateFlow<ApiMapper<ContactListSuccessModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )

    fun doLending(token: String, lendingRequestModel: LendingRequestModel) {
        lendingSuccessModel = MutableStateFlow<ApiMapper<LendingSuccessModel>>(
            ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
        )
        viewModelScope.launch {
            val loginResponse = reltimeRepository.doLendingRequest(token, lendingRequestModel)
            when (loginResponse.status) {
                ApiCallStatus.SUCCESS -> {
                    lendingSuccessModel.value =
                        ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                }
                ApiCallStatus.ERROR -> {
                    lendingSuccessModel.value =
                        ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                }
                else -> {}
            }
        }
    }
    fun doUpdateLending(token: String, lendingRequestModel: LendingRequestModel) {
        lendingSuccessModel = MutableStateFlow<ApiMapper<LendingSuccessModel>>(
            ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
        )
        viewModelScope.launch {
            val loginResponse = reltimeRepository.doUpdateLendingRequest(token, lendingRequestModel)
            when (loginResponse.status) {
                ApiCallStatus.SUCCESS -> {
                    lendingSuccessModel.value =
                        ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                }
                ApiCallStatus.ERROR -> {
                    lendingSuccessModel.value =
                        ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                }
                else -> {}
            }
        }
    }

    fun doTransactionApproval(token: String, transactionRequestModel: TransactionRequestModel) {
        transactionApproveSuccessModel =
            MutableStateFlow<ApiMapper<TransactionApprovalSuccessModel>>(
                ApiMapper(
                    ApiCallStatus.LOADING,
                    null,
                    null
                )
            )
        viewModelScope.launch {
            val loginResponse =
                reltimeRepository.getTransactionApproval(token, transactionRequestModel)
            when (loginResponse.status) {
                ApiCallStatus.SUCCESS -> {
                    transactionApproveSuccessModel.value =
                        ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                }
                ApiCallStatus.ERROR -> {
                    transactionApproveSuccessModel.value =
                        ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                }
                else -> {}
            }
        }
    }


    fun doTransactionSiginIn(token: String, signTransactionRequest: SignTransactionRequest) {
        siginInTransctionSuccess = MutableStateFlow<ApiMapper<SiginInTransactionSuccessModel>>(
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
                    siginInTransctionSuccess.value =
                        ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                }
                ApiCallStatus.ERROR -> {
                    siginInTransctionSuccess.value =
                        ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                }
                else -> {}
            }
        }
    }


    fun getCommission(token: String) {
        commissionSuccessModel = MutableStateFlow<ApiMapper<CommissionSuccessModel>>(
            ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
        )
        viewModelScope.launch {
            val loginResponse = reltimeRepository.getCommissionData(token)
            when (loginResponse.status) {
                ApiCallStatus.SUCCESS -> {
                    commissionSuccessModel.value =
                        ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                }
                ApiCallStatus.ERROR -> {
                    commissionSuccessModel.value =
                        ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                }
                else -> {}
            }
        }
    }

    fun getContactList(token: String) {
        contactList = MutableStateFlow<ApiMapper<ContactListSuccessModel>>(
            ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
        )
        commissionSuccessModel = MutableStateFlow<ApiMapper<CommissionSuccessModel>>(
            ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
        )
        viewModelScope.launch {
            val loginResponse = async { reltimeRepository.getContactList(token) }
            val loginResponsecom = async { reltimeRepository.getCommissionData(token) }
            when (loginResponse.await().status) {
                ApiCallStatus.SUCCESS -> {
                    contactList.value =
                        ApiMapper(ApiCallStatus.SUCCESS, loginResponse.await().data, null)
                }
                ApiCallStatus.ERROR -> {
                    contactList.value =
                        ApiMapper(ApiCallStatus.ERROR, null, loginResponse.await().errorMessage)
                }
                else -> {}
            }

            when (loginResponsecom.await().status) {
                ApiCallStatus.SUCCESS -> {
                    commissionSuccessModel.value =
                        ApiMapper(ApiCallStatus.SUCCESS, loginResponsecom.await().data, null)
                }
                ApiCallStatus.ERROR -> {
                    contactList.value =
                        ApiMapper(ApiCallStatus.ERROR, null, loginResponsecom.await().errorMessage)
                }
                else -> {}
            }
        }
    }

}