package com.accubits.reltime.activity.settings.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.activity.settings.model.EmailPhoneUpdationResponseModel
import com.accubits.reltime.activity.settings.model.PhoneEmailModel
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.repository.ReltimeRepository
import com.accubits.reltime.utils.Extensions.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingEmailPhoneViewModel @Inject constructor(
    private val repository: ReltimeRepository,
    private val preferenceManager: PreferenceManager
) :
    ViewModel() {
    private val _phoneEmailModel = mutableStateOf(PhoneEmailModel())
    val phoneEmailModel: MutableState<PhoneEmailModel> get() = _phoneEmailModel

    val updationStateFlow = MutableStateFlow<ApiMapper<EmailPhoneUpdationResponseModel>>(
        ApiMapper(
            ApiCallStatus.LOADING, null, null
        )
    )

    /*Otp for both phone and email change can be generated with this function*/
    fun generateOtp(deviceId: String, token: String?=null) {
        val model = PhoneEmailModel(
            otp = phoneEmailModel.value.otp, newPhoneNumber = phoneEmailModel.value.newPhoneNumber,
            newEmailId = phoneEmailModel.value.newEmailId, token = token
        )
        viewModelScope.launch {
            val response = repository.generateOtp(
                preferenceManager.getApiKey(),
                model, deviceId = deviceId
            )
            when (response.status) {
                ApiCallStatus.SUCCESS -> {
                    updationStateFlow.value = ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                }
                ApiCallStatus.ERROR -> {
                    updationStateFlow.value =
                        ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                }
                else -> {}
            }
        }
    }

    fun onEmailValidated(onValidate: (success: Boolean, message: String) -> Unit = { success, message -> }) {
        phoneEmailModel.value.apply {
            if (newEmailId.toString().isNullOrBlank()) {
                onValidate(false, "Field can't be empty")
            } else if (!newEmailId.toString().isValidEmail()) {
                onValidate(false, "Please check the new email format")
            } else {
                onValidate(true, "Validation success")
            }
        }
    }

    fun onPhoneValidated(onValidate: (success: Boolean, message: String) -> Unit = { success, message -> }) {
        phoneEmailModel.value.apply {
            if (newPhoneNumber.toString().isNullOrBlank()) {
                onValidate(false, "Field can't be empty")
            } else {
                onValidate(true, "Validation success")
            }
        }
    }
}