package com.accubits.reltime.activity.v2.wallet.swap.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.activity.v2.wallet.swap.model.*
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.WalletSignInModel
import com.accubits.reltime.models.WalletSigninRequest
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SwapViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager, private val repository: ReltimeRepository
) : ViewModel() {
    val swapFromCurrencyFlow =
        MutableStateFlow(ApiMapper<CurrencyListResponse>(ApiCallStatus.EMPTY, null, null))
    val swapToCurrencyFlow =
        MutableStateFlow(ApiMapper<CurrencyListResponse>(ApiCallStatus.EMPTY, null, null))

    val selectedFromCurrency = MutableStateFlow<CryptoCurrency?>(null)
    val selectedToCurrency = MutableStateFlow<CryptoCurrency?>(null)

    private var inputAmount: String? = null

    val currencyStatisticsResponseFlow = MutableStateFlow<ApiMapper<CryptoConversionResponse>>(
        ApiMapper(
            ApiCallStatus.EMPTY, null, null
        )
    )
    val swapConfirmRequestFlow = MutableStateFlow<ApiMapper<WalletSignInModel>>(
        ApiMapper(
            ApiCallStatus.EMPTY, null, null
        )
    )

    fun setSelectedFromCurrency(currency: CryptoCurrency?) {
        selectedFromCurrency.value = currency
        getToCoins(currency?.coinCode)
        selectedToCurrency.value = null
    }

    fun setSelectedToCurrency(currency: CryptoCurrency?) {
        selectedToCurrency.value = currency
    }


    fun getFromCoins() {
        viewModelScope.launch {
            swapFromCurrencyFlow.value = ApiMapper(ApiCallStatus.LOADING, null, null)
            val response = repository.getCurrencyList(preferenceManager.getApiKey())
            try {
                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        swapFromCurrencyFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                    }

                    ApiCallStatus.ERROR -> {
                        swapFromCurrencyFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                    }
                    else -> {}
                }
            } catch (ex: Exception) {
                swapFromCurrencyFlow.value = ApiMapper(ApiCallStatus.ERROR, null, ex.message)
            }
        }
    }

    private fun getToCoins(coinCode: String?) {
        viewModelScope.launch {
            swapToCurrencyFlow.value = ApiMapper(ApiCallStatus.LOADING, null, null)

            val response = repository.getCurrencyList(preferenceManager.getApiKey(), coinCode)
            try {
                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        swapToCurrencyFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                    }

                    ApiCallStatus.ERROR -> {
                        swapToCurrencyFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                    }
                    else -> {}
                }
            } catch (ex: Exception) {
                swapToCurrencyFlow.value = ApiMapper(ApiCallStatus.ERROR, null, ex.message)
            }
        }
    }

    fun getCryptoConversionStatistics(coinCode: String?, amount: String, sourceCoinCode: String?) {
        viewModelScope.launch {
            currencyStatisticsResponseFlow.value = ApiMapper(ApiCallStatus.LOADING, null, null)
            val response = repository.getCryptoConversionStatistics(
                preferenceManager.getApiKey(), CryptoConversionRequest(
                    coinCode = sourceCoinCode,
                    to_coinCode = coinCode,
                    amount = if (amount.toDoubleOrNull() != null) amount else "0"
                )
            )
            try {
                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        currencyStatisticsResponseFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                    }

                    ApiCallStatus.ERROR -> {
                        currencyStatisticsResponseFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                    }
                    else -> {}
                }
            } catch (ex: Exception) {
                currencyStatisticsResponseFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, ex.message)
            }
        }
    }

    fun doSwap(amount: String?){
        inputAmount = amount
        if (selectedFromCurrency.value?.coinCode=="RTC")
        doSwap()
        else swapApproval()
    }

   private fun swapApproval() {
        viewModelScope.launch {
            try {
                swapConfirmRequestFlow.value = ApiMapper(ApiCallStatus.LOADING, null, null)
                val response = repository.swapApproval(
                    preferenceManager.getApiKey(), SwapApprovalRequest(
                        coinCode = selectedFromCurrency.value!!.coinCode,
                        amount = inputAmount//?.toDoubleOrNull()
                    )
                )
                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        if (response.data?.status == 200 && response.data.success &&
                            response.data.result != null) signSwap(
                            WalletSigninRequest(
                                signedTxn = Utils.getKeyHasForTransaction(
                                    response.data.result!!.data, preferenceManager
                                )
                            )
                        )
                        else swapConfirmRequestFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.data?.message)
                    }

                    ApiCallStatus.ERROR -> {
                        swapConfirmRequestFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                    }
                    else -> {}
                }
            } catch (ex: Exception) {
                swapConfirmRequestFlow.value = ApiMapper(ApiCallStatus.ERROR, null, ex.message)
            }
        }
    }

    private fun signSwap(data: WalletSigninRequest) {
        viewModelScope.launch {
            try {
                val response = repository.performWalletSigin(
                    preferenceManager.getApiKey(), request = data
                )
                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        if (response.data?.status == 200 && response.data.success) if (response.data.transactionItem.txnId == null) doSwap()
                        else swapConfirmRequestFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                        else swapConfirmRequestFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.data?.message)
                    }
                    ApiCallStatus.ERROR -> {
                        swapConfirmRequestFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                    }
                    else -> {}
                }
            } catch (ex: Exception) {
                swapConfirmRequestFlow.value = ApiMapper(ApiCallStatus.ERROR, null, ex.message)
            }
        }
    }

    private fun doSwap() {
        viewModelScope.launch {
            if(swapConfirmRequestFlow.value != ApiMapper(
                    ApiCallStatus.LOADING,
                    null,
                    null
                ))
                swapConfirmRequestFlow.value = ApiMapper(
                    ApiCallStatus.LOADING,
                    null,
                    null
                )
            val response = repository.doSwap(
                preferenceManager.getApiKey(), SwapApprovalRequest(
                    coinCode = selectedFromCurrency.value!!.coinCode,
                    to_coinCode = selectedToCurrency.value!!.coinCode,
                    amount = inputAmount,//?.toDoubleOrNull(),
                    converted_amount = currencyStatisticsResponseFlow.value.data?.result?.converted_amount,
                )
            )
            try {
                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        if (response.data?.status == 200 && response.data.success && response.data.result != null) signSwap(
                            WalletSigninRequest(
                                id = response.data.result!!.Id,
                                type = "swapCoin",
                                signedTxn = Utils.getKeyHasForTransaction(
                                    response.data.result!!.data, preferenceManager
                                )
                            )
                        )
                        else swapConfirmRequestFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.data?.message)
                    }

                    ApiCallStatus.ERROR -> {
                        swapConfirmRequestFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                    }
                    else -> {}
                }
            } catch (ex: Exception) {
                swapConfirmRequestFlow.value = ApiMapper(ApiCallStatus.ERROR, null, ex.message)
            }
        }
    }

}