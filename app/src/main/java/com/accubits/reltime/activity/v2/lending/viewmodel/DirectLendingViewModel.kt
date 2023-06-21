package com.accubits.reltime.activity.v2.lending.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.activity.v2.lending.model.LendingCalculationRequest
import com.accubits.reltime.activity.v2.lending.model.LendngCalculationResponse
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.models.*
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DirectLendingViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val repository: ReltimeRepository
) : ViewModel() {

    val walletFlow =
        MutableStateFlow(ApiMapper<WalletSuccessModel>(ApiCallStatus.EMPTY, null, null))
    val lendingCalculationFlow =
        MutableStateFlow(ApiMapper<LendngCalculationResponse>(ApiCallStatus.EMPTY, null, null))
    val transactionApproveFlow = MutableStateFlow<ApiMapper<TransactionApprovalSuccessModel>>(
        ApiMapper(ApiCallStatus.EMPTY, null, null)
    )
    val signTransactionFlow =
        MutableStateFlow<ApiMapper<SiginInTransactionSuccessModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )
    val lendingSuccessFlow =
        MutableStateFlow<ApiMapper<LendingSuccessModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )

    fun getWallet() {
        viewModelScope.launch {
            walletFlow.value = ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
            try {
                val response = repository.getWalletDetails(preferenceManager.getApiKey())
                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        walletFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                    }
                    ApiCallStatus.ERROR -> {
                        walletFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                if (e !is CancellationException)
                walletFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, e.localizedMessage)
            }
        }
    }


    fun getLendingCalculation(request: LendingCalculationRequest) {
        viewModelScope.launch {
            lendingCalculationFlow.value = ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
            try {
                val response =
                    repository.getLendingCalculation(preferenceManager.getApiKey(), request)
                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        lendingCalculationFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                    }
                    ApiCallStatus.ERROR -> {
                        lendingCalculationFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                if (e !is CancellationException)
                lendingCalculationFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, e.localizedMessage)
            }
        }
    }


    fun transactionApproval(transactionRequestModel: TransactionRequestModel) {
        transactionApproveFlow.value = ApiMapper(
            ApiCallStatus.LOADING,
            null,
            null
        )
        viewModelScope.launch {
            try {
                val loginResponse =
                    repository.getTransactionApproval(
                        preferenceManager.getApiKey(),
                        transactionRequestModel
                    )
                when (loginResponse.status) {
                    ApiCallStatus.SUCCESS -> {
                        transactionApproveFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                    }
                    ApiCallStatus.ERROR -> {
                        transactionApproveFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                transactionApproveFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, e.localizedMessage)
            }
        }
    }

    fun doTransactionSignIn(signTransactionRequest: SignTransactionRequest) {
        signTransactionFlow.value = ApiMapper(
            ApiCallStatus.LOADING,
            null,
            null
        )
        viewModelScope.launch {
            try {
                val loginResponse =
                    repository.doTransactionSiginInApi(
                        preferenceManager.getApiKey(),
                        signTransactionRequest
                    )
                when (loginResponse.status) {
                    ApiCallStatus.SUCCESS -> {
                        signTransactionFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                    }
                    ApiCallStatus.ERROR -> {
                        signTransactionFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                signTransactionFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, e.localizedMessage)
            }
        }
    }

    fun doLending(lendingRequestModel: LendingRequestModel) {
        lendingSuccessFlow.value = ApiMapper(
            ApiCallStatus.LOADING,
            null,
            null
        )
        viewModelScope.launch {
            try {
                val loginResponse =
                    repository.doLendingRequest(preferenceManager.getApiKey(), lendingRequestModel)
                when (loginResponse.status) {
                    ApiCallStatus.SUCCESS -> {
                        lendingSuccessFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                    }
                    ApiCallStatus.ERROR -> {
                        lendingSuccessFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                lendingSuccessFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, e.localizedMessage)
            }
        }
    }

    fun doUpdateLending(lendingRequestModel: LendingRequestModel) {
        lendingSuccessFlow.value = ApiMapper(
            ApiCallStatus.LOADING,
            null,
            null
        )
        viewModelScope.launch {
            try {
                val loginResponse = repository.doUpdateLendingRequest(
                    preferenceManager.getApiKey(),
                    lendingRequestModel
                )
                when (loginResponse.status) {
                    ApiCallStatus.SUCCESS -> {
                        lendingSuccessFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                    }
                    ApiCallStatus.ERROR -> {
                        lendingSuccessFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                lendingSuccessFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, e.localizedMessage)
            }
        }
    }

}