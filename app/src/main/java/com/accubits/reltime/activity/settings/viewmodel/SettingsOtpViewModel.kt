package com.accubits.reltime.activity.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.activity.settings.model.EmailPhoneUpdationResponseModel
import com.accubits.reltime.activity.settings.model.PhoneEmailModel
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsOtpViewModel @Inject constructor(
    private val repository: ReltimeRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    val resendOtpFlow = MutableStateFlow<ApiMapper<EmailPhoneUpdationResponseModel>>(
        ApiMapper(ApiCallStatus.EMPTY, null, null)
    )
    val otpResponseFlow = MutableStateFlow<ApiMapper<EmailPhoneUpdationResponseModel>>(
        ApiMapper(ApiCallStatus.LOADING, null, null)
    )


    /*Otp for both phone and email change can be generated with this function*/
    fun resendOtp(phone: String?, email: String?,deviceId:String) {

        viewModelScope.launch {
            resendOtpFlow.value =
                ApiMapper(ApiCallStatus.LOADING, null,null)
            val response = repository.generateOtp(
                preferenceManager.getApiKey(),
                PhoneEmailModel(null, phone, email), deviceId = deviceId
            )
            when (response.status) {
                ApiCallStatus.SUCCESS -> {
                    resendOtpFlow.value = ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                }
                ApiCallStatus.ERROR -> {
                    resendOtpFlow.value =
                        ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                }
                else -> {}
            }
        }
    }


    fun updateEmailorPhone(otp: String, phone: String?, email: String?) {
        viewModelScope.launch {
            val response = repository.updateEmailorPhone(
                preferenceManager.getApiKey(),
                PhoneEmailModel(otp = otp, newPhoneNumber = phone, newEmailId = email)
            )
            when (response.status) {
                ApiCallStatus.SUCCESS -> {
                    otpResponseFlow.value = ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                }
                ApiCallStatus.ERROR -> {
                    otpResponseFlow.value =
                        ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                }
                else -> {}
            }
        }
    }
}