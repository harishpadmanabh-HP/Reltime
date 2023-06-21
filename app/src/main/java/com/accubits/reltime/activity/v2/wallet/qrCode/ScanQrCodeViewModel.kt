package com.accubits.reltime.activity.v2.wallet.qrCode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.activity.v2.common.walletpicker.model.WalletAddressValidationRequest
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.models.SearchUserResponseModel
import com.accubits.reltime.models.WalletValidationAddressResponse
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScanQrCodeViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val repository: ReltimeRepository
) : ViewModel() {

    var coinType = "RTO"

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
                val request = WalletAddressValidationRequest(coinType, address)
                val response =
                    repository.walletAddressValidation(preferenceManager.getApiKey(), request)
                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        walletValidateResponseFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                    }
                    ApiCallStatus.ERROR -> {
                        walletValidateResponseFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
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