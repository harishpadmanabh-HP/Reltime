package com.accubits.reltime.activity.v2.common.walletpicker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.activity.myAccounts.model.BankAccount
import com.accubits.reltime.activity.myAccounts.model.JointAccount
import com.accubits.reltime.activity.myAccounts.model.RTO
import com.accubits.reltime.activity.rto.model.RtoSignTransactionResponseModel
import com.accubits.reltime.activity.v2.common.walletpicker.model.WalletAddressValidationRequest
import com.accubits.reltime.activity.v2.common.walletpicker.model.WalletValidationResponse
import com.accubits.reltime.activity.v2.lending.model.LendngCalculationResponse
import com.accubits.reltime.activity.withdraw.model.*
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.*
import com.accubits.reltime.repository.ReltimeRepository
import com.accubits.reltime.utils.Extensions.accountWithdrawType
import com.accubits.reltime.utils.Extensions.getAddress
import com.accubits.reltime.utils.Extensions.getCoinCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "WithdrawViewModel"

@HiltViewModel
class WalletAddressViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val repository: ReltimeRepository
) : ViewModel() {

    val walletValidateResponseFlow =
        MutableStateFlow(
            ApiMapper<SearchUserResponseModel>(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )


    fun walletAddressValidation(address: String, coinType: String) {
        walletValidateResponseFlow.value = ApiMapper(
            ApiCallStatus.LOADING,
            null,
            null
        )
        viewModelScope.launch {
            try {
                val request = WalletAddressValidationRequest(address, coinType)
                val loginResponse =
                    repository.walletAddressValidation(preferenceManager.getApiKey(), request)
                when (loginResponse.status) {
                    ApiCallStatus.SUCCESS -> {
                        walletValidateResponseFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                    }
                    ApiCallStatus.ERROR -> {
                        walletValidateResponseFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                walletValidateResponseFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, e.message)
            }
        }
    }

}