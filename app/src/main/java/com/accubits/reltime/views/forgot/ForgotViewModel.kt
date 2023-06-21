package com.accubits.reltime.views.forgot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.models.ForgotRequestModel
import com.accubits.reltime.models.ForgotSuccessModel
import com.accubits.reltime.models.OtpVerifySuccessModel
import com.accubits.reltime.models.VerifyRequestEmail
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotViewModel @Inject constructor(
    private val repository: ReltimeRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    val otpGenerateFlow =
        MutableStateFlow(ApiMapper<ForgotSuccessModel>(ApiCallStatus.EMPTY, null, null))

    fun otpGenerate(token:String?,deviceId: String, forgotRequestModel: ForgotRequestModel) {
        viewModelScope.launch {
            otpGenerateFlow.value = ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
            try {
                val response = repository.otpGenerate(
                    token = token,
                    deviceId = deviceId,
                    forgotRequestModel
                )
                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        otpGenerateFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                    }
                    ApiCallStatus.ERROR -> {
                        otpGenerateFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                otpGenerateFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, e.localizedMessage)
            }
        }
    }


    val otpVerificationFlow =
        MutableStateFlow(ApiMapper<OtpVerifySuccessModel>(ApiCallStatus.EMPTY, null, null))

    fun doVerifyEmail(verifySuccessModel: VerifyRequestEmail) {
        viewModelScope.launch {
            otpVerificationFlow.value = ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
            try {
                val response = repository.doVerifyEmail(
                    token = preferenceManager.getApiKey().ifEmpty { null },
                    verifySuccessModel
                )
                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        otpVerificationFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                    }
                    ApiCallStatus.ERROR -> {
                        otpVerificationFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                otpVerificationFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, e.localizedMessage)
            }
        }
    }

}