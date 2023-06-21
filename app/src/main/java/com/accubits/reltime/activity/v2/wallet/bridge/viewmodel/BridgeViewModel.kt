package com.accubits.reltime.activity.v2.wallet.bridge.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.BuildConfig
import com.accubits.reltime.activity.v2.wallet.swap.model.CryptoCurrency
import com.accubits.reltime.activity.v2.wallet.swap.model.CurrencyListResponse
import com.accubits.reltime.activity.v2.wallet.swap.model.SwapApprovalRequest
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.WalletSignInModel
import com.accubits.reltime.models.WalletSigninRequest
import com.accubits.reltime.repository.ReltimeRepository
import com.accubits.reltime.utils.cryptoutils.BitcoinUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BridgeViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val repository: ReltimeRepository,private val bitcoinUtils: BitcoinUtils
) : ViewModel() {
    val bridgeTokensFlow =
        MutableStateFlow(ApiMapper<CurrencyListResponse>(ApiCallStatus.EMPTY, null, null))

    val selectedFromToken = MutableStateFlow<CryptoCurrency?>(null)

    val bridgeConfirmRequestFlow = MutableStateFlow<ApiMapper<WalletSignInModel>>(
        ApiMapper(
            ApiCallStatus.EMPTY,
            null,
            null
        )
    )

    fun setSelectedBridgeFromToken(currency: CryptoCurrency?) {
        selectedFromToken.value = currency
    }

    fun getBridgeTokenList() {
        viewModelScope.launch {
            bridgeTokensFlow.value =
                ApiMapper(ApiCallStatus.LOADING, null, null)
            val response = repository.getBridgeTokenList(preferenceManager.getApiKey())
            try {
                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        bridgeTokensFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                    }
                    ApiCallStatus.ERROR -> {
                        bridgeTokensFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                    }
                    else -> {}
                }
            } catch (ex: Exception) {
                bridgeTokensFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, ex.message)
            }
        }
    }

    fun doBridge(amount: String?) {
        viewModelScope.launch {
            bridgeConfirmRequestFlow.value =
                ApiMapper(ApiCallStatus.LOADING, null, null)
            val response = repository.doBridge(
                preferenceManager.getApiKey(),
                SwapApprovalRequest(
                    coinCode = selectedFromToken.value!!.coinCode,
                    amount = amount//?.toDoubleOrNull()
                )
            )
            var chain:String?=null
            val chainId =  when (selectedFromToken.value!!.coinCode) {
                "RTC","ETH","USDT","USDC" -> {
                    chain="ETH"
                    BuildConfig.ETH_CHAIN_ID
                }
                "BNB" -> {
                    chain="BNB"
                    BuildConfig.BNB_CHAIN_ID
                }
                "BTC" -> {
                    chain="BTC"
                    BuildConfig.BNB_CHAIN_ID
                }
                else -> BuildConfig.CHAIN_ID
            }

            try {
                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        if (response.data?.status == 200 && response.data.success && response.data.result != null)
                            signBridge(
                                WalletSigninRequest(
                                    id = response.data.result!!.Id, type = "bridging",
                                    signedTxn =if(selectedFromToken.value!!.coinCode!="BTC") Utils.getKeyHasForTransaction(
                                        response.data.result!!.data,
                                        preferenceManager,chainId=chainId
                                    ) else bitcoinUtils.signBtcTransaction(response.data.result!!.data),
                                    chain = chain
                                )
                            )
                        else bridgeConfirmRequestFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.data?.message)
                    }

                    ApiCallStatus.ERROR -> {
                        bridgeConfirmRequestFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                    }
                    else -> {}
                }
            } catch (ex: Exception) {
                bridgeConfirmRequestFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, ex.message)
            }
        }
    }

    private fun signBridge(data: WalletSigninRequest) {
        viewModelScope.launch {
            try {
                val response =
                    repository.performWalletSigin(
                        preferenceManager.getApiKey(), request = data
                    )
                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        if (response.data?.status == 200 && response.data.success)
                            bridgeConfirmRequestFlow.value =
                                ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                        else bridgeConfirmRequestFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.data?.message)
                    }
                    ApiCallStatus.ERROR -> {
                        bridgeConfirmRequestFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                    }
                    else -> {}
                }
            } catch (ex: Exception) {
                bridgeConfirmRequestFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, ex.message)
            }
        }
    }


}