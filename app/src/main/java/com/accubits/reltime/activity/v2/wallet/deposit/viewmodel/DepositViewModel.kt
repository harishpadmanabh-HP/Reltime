package com.accubits.reltime.activity.v2.wallet.deposit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.activity.myAccounts.model.MyAccountsListModel
import com.accubits.reltime.activity.myAccounts.model.ReltimeAccount
import com.accubits.reltime.activity.v2.wallet.swap.model.CryptoCurrency
import com.accubits.reltime.activity.withdraw.model.AccountListResponse
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DepositViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val repository: ReltimeRepository
) : ViewModel() {
    val selectedFromAccount = MutableStateFlow<ReltimeAccount?>(null)

    val depositFromFromFlow =
        MutableStateFlow(ApiMapper<AccountListResponse>(ApiCallStatus.LOADING, null, null))

    fun setSelectedDepositFrom(currency: ReltimeAccount?) {
        selectedFromAccount.value = currency
    }

    fun getDepositAccounts(){
        viewModelScope.launch {
            val response = repository.getDepositAccounts(preferenceManager.getApiKey())
            try {
                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        depositFromFromFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                    }

                    ApiCallStatus.ERROR -> {
                        depositFromFromFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                    }
                    else -> {}
                }
            } catch (ex: Exception) {
                depositFromFromFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, ex.message)
            }
        }
    }

}