package com.accubits.reltime.views.rto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.activity.jointAccount.model.CreateJointAccountTxnResponse
import com.accubits.reltime.activity.rto.model.RtoSignTransactionResponseModel
import com.accubits.reltime.activity.withdraw.model.JointAccountWithdrawRequestNew
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.models.*
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuyRtoViewModel @Inject constructor(
    private var reltimeRepository: ReltimeRepository
) : ViewModel() {
    var addCheckoutResponseFlow =
        MutableStateFlow<ApiMapper<CheckoutSuccessModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )
    var getCheckoutResponseFlow =
        MutableStateFlow<ApiMapper<CheckoutSuccessModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )
    var sendWyreResponseFlow =
        MutableStateFlow<ApiMapper<SendWyreSuccessModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )

    var currencyConversionResponseFlow =
        MutableStateFlow<ApiMapper<CurrencyConversionSuccessModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )

    var wyreCheckoutResponseFlow =
        MutableStateFlow<ApiMapper<WyreCheckoutResponse>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )

    var wyreCheckoutStatusResponseFlow =
        MutableStateFlow<ApiMapper<WyreCheckoutStatusResponse>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )

    fun walletAddCheckout(token: String,request : CheckoutApiRequest) {
        /*addCheckoutResponseFlow = MutableStateFlow<ApiMapper<CheckoutSuccessModel>>(
            ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
        )*/
        viewModelScope.launch {
            val loginResponse = reltimeRepository.walletAddCheckout(token,request)
            when (loginResponse.status) {
                ApiCallStatus.SUCCESS -> {
                    addCheckoutResponseFlow.value =
                        ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                }
                ApiCallStatus.ERROR -> {
                    addCheckoutResponseFlow.value =
                        ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                }
                else -> {}
            }
        }
    }

    fun walletGetCheckout(token: String,id:Int) {
        getCheckoutResponseFlow.value = ApiMapper(
            ApiCallStatus.LOADING,
            null,
            null
        )
        viewModelScope.launch {
            val loginResponse = reltimeRepository.walletGetCheckout(token,id)
            when (loginResponse.status) {
                ApiCallStatus.SUCCESS -> {
                    getCheckoutResponseFlow.value =
                        ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                }
                ApiCallStatus.ERROR -> {
                    getCheckoutResponseFlow.value =
                        ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                }
                else -> {}
            }
        }
    }

    fun convertCurrency(token: String,coinCode: String,amount:String) {
        /*getCheckoutResponseFlow = MutableStateFlow<ApiMapper<CheckoutSuccessModel>>(
            ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
        )*/
        viewModelScope.launch {
            val loginResponse = reltimeRepository.convertCurrency(token,coinCode,amount)
            when (loginResponse.status) {
                ApiCallStatus.SUCCESS -> {
                    currencyConversionResponseFlow.value =
                        ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                }
                ApiCallStatus.ERROR -> {
                    currencyConversionResponseFlow.value =
                        ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                }
                else -> {}
            }
        }
    }

    fun wyrePayment(token: String,request : SendWyreApiRequest) {
        /*loginResponseFlow = MutableStateFlow<ApiMapper<CheckMpinSuccessModel>>(
            ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
        )*/
        viewModelScope.launch {
            val loginResponse = reltimeRepository.wyrePayment(token,request)
            when (loginResponse.status) {
                ApiCallStatus.SUCCESS -> {
                    sendWyreResponseFlow.value =
                        ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                }
                ApiCallStatus.ERROR -> {
                    sendWyreResponseFlow.value =
                        ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                }
                else -> {}
            }
        }
    }


    fun createWyreDepositCheckout(token: String,amount: String,coinCode:String) {
        val wyreCheckoutRequest=WyreCheckoutRequest(amount=amount,coinCode=coinCode)
        viewModelScope.launch {
            wyreCheckoutResponseFlow.value = ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
            val response = reltimeRepository.createWyreDepositCheckout(
                token, wyreCheckoutRequest
            )
            when (response.status) {
                ApiCallStatus.SUCCESS -> {
                    wyreCheckoutResponseFlow.value =
                        ApiMapper(ApiCallStatus.SUCCESS, response.data, null)

                }
                ApiCallStatus.ERROR -> {
                    wyreCheckoutResponseFlow.value =
                        ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                }
                else -> {}
            }
        }
    }

    fun checkWyreDepositStatus(token: String,id: Int) {
        val wyreCheckoutRequest=WyreCheckoutStatusRequest(id = id)
        viewModelScope.launch {
            wyreCheckoutStatusResponseFlow.value = ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
            val response = reltimeRepository.checkWyreDepositStatus(
                token, wyreCheckoutRequest
            )
            when (response.status) {
                ApiCallStatus.SUCCESS -> {
                    wyreCheckoutStatusResponseFlow.value =
                        ApiMapper(ApiCallStatus.SUCCESS, response.data, null)

                }
                ApiCallStatus.ERROR -> {
                    wyreCheckoutStatusResponseFlow.value =
                        ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                }
                else -> {}
            }
        }
    }

}