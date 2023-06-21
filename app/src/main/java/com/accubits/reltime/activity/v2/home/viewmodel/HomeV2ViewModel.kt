package com.accubits.reltime.activity.v2.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.activity.v2.wallet.home.model.WalletHomeSuccessModel
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.models.TransferTransactionResponseModelX
import com.accubits.reltime.models.WalletSuccessModel
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeV2ViewModel @Inject constructor(
    private val repository: ReltimeRepository,
    private val preferenceManager: PreferenceManager
) :
    ViewModel() {
    companion object {
        private const val HOME_TRANSFER_PAGE_NUMBER = "1"
        private const val HOME_TRANSFER_PAGE_LIMIT = "4"
    }

    val walletDetailsFlow =
        MutableStateFlow<ApiMapper<WalletHomeSuccessModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )

    val homeTransferHistoryFlow =
        MutableStateFlow<ApiMapper<TransferTransactionResponseModelX>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )

    val walletSuccessModel =
        MutableStateFlow<ApiMapper<WalletSuccessModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )

    fun getWalletHomeV2Details() {
        viewModelScope.launch {
            coroutineScope {
                walletDetailsFlow.value =
                    ApiMapper(ApiCallStatus.LOADING, null, null)
                homeTransferHistoryFlow.value =
                    ApiMapper(ApiCallStatus.LOADING, null, null)
                val call1 =
                    async { repository.getWalletHomeDetails(preferenceManager.getApiKey()) }
                val call2 = async {
                    repository.getHomeTransferList(
                        preferenceManager.getApiKey(),
                        HOME_TRANSFER_PAGE_NUMBER, HOME_TRANSFER_PAGE_LIMIT
                    )
                }
                try {
                    val accountList = call1.await()
                    val transactionList = call2.await()

                    when (accountList.status) {
                        ApiCallStatus.SUCCESS -> {
                            walletDetailsFlow.value =
                                ApiMapper(ApiCallStatus.SUCCESS, accountList.data, null)
                        }

                        ApiCallStatus.ERROR -> {
                            walletDetailsFlow.value =
                                ApiMapper(ApiCallStatus.ERROR, null, accountList.errorMessage)
                        }
                        else -> {}
                    }
                    when (transactionList.status) {
                        ApiCallStatus.SUCCESS -> {
                            homeTransferHistoryFlow.value =
                                ApiMapper(ApiCallStatus.SUCCESS, transactionList.data, null)
                        }

                        ApiCallStatus.ERROR -> {
                            homeTransferHistoryFlow.value =
                                ApiMapper(ApiCallStatus.ERROR, null, transactionList.errorMessage)
                        }
                        else -> {}
                    }
                } catch (ex: Exception) {
                    if (ex !is CancellationException)
                        walletDetailsFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, ex.message)
                }
            }
        }
    }


    fun getWalletDetails() {
        viewModelScope.launch {
            val loginResponse = repository.getWalletDetails(preferenceManager.getApiKey())


            if (loginResponse.status==ApiCallStatus.SUCCESS) {
                walletSuccessModel.value =
                    ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
            }
        }
    }



}