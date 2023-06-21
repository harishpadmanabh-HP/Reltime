package com.accubits.reltime.activity.signup.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.activity.signup.model.body.OtpData
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.models.OtpVerifySuccessModel
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupOtpViewModel @Inject constructor(
    private val repository: ReltimeRepository,
) : ViewModel() {

    var otpValidateFlow =
        MutableStateFlow<ApiMapper<OtpVerifySuccessModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )
    var generateOTPFlow =
        MutableStateFlow<ApiMapper<OtpVerifySuccessModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )

    fun validateOTPV2(otpData: OtpData) {
        viewModelScope.launch {
            otpValidateFlow.value = ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
            try {
                val response = repository.validateOTPV2(otpData)
                when (response.status) {
                    ApiCallStatus.SUCCESS ->
                        otpValidateFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, response.data, null)

                    ApiCallStatus.ERROR ->
                        otpValidateFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)

                    else -> {}
                }
            } catch (e: Exception) {
                otpValidateFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, e.localizedMessage)
            }
        }
    }

    fun regenerateOtpV2(otpData: OtpData,deviceId:String) {
        viewModelScope.launch {
            generateOTPFlow.value = ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
            try {
                val response = repository.regenerateOtpV2(otpData, deviceId = deviceId)
                when (response.status) {
                    ApiCallStatus.SUCCESS ->
                        generateOTPFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, response.data, null)

                    ApiCallStatus.ERROR ->
                        generateOTPFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)

                    else -> {}
                }
            } catch (e: Exception) {
                generateOTPFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, e.localizedMessage)
            }
        }
    }
}