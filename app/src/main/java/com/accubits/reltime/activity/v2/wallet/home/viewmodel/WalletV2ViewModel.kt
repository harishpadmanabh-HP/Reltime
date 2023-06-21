package com.accubits.reltime.activity.v2.wallet.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.accubits.reltime.activity.myAccounts.model.MyAccountsListModel
import com.accubits.reltime.activity.v2.wallet.TransactionPagingV2Source
import com.accubits.reltime.activity.v2.wallet.home.model.WalletHomeSuccessModel
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.models.CardSuccessModel
import com.accubits.reltime.models.WalletAmountSuccessModel
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletV2ViewModel @Inject constructor(
    private val repository: ReltimeRepository,
    private val preferenceManager: PreferenceManager,
    private val transactionPagingSource: TransactionPagingV2Source
) :
    ViewModel() {
    val walletDetailsFlow =
        MutableStateFlow<ApiMapper<WalletHomeSuccessModel>>(
            ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
        )


    fun getWalletHomeDetails(){
        viewModelScope.launch {
            try {
                val response = repository.getWalletHomeDetails(preferenceManager.getApiKey())
                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        walletDetailsFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                    }

                    ApiCallStatus.ERROR -> {
                        walletDetailsFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                walletDetailsFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, e.localizedMessage)
            }
        }
    }


    var search = MutableStateFlow<String>("")


    fun getPagedTransactionData() = search.flatMapLatest { querry ->
        Pager(PagingConfig(20), pagingSourceFactory = {
            transactionPagingSource
        }).flow
    }
}