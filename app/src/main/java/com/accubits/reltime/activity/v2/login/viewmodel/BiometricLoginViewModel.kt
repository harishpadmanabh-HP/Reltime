package com.accubits.reltime.activity.v2.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.activity.biometricLogin.model.SmartLoginRequest
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.models.LoginSuccessModel
import com.accubits.reltime.repository.BiometricLoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BiometricLoginViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val repository: BiometricLoginRepository
) : ViewModel() {
    var loginResponseFlow =
        MutableStateFlow<ApiMapper<LoginSuccessModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )


    fun callBiometricLogin(phone: String?, text: String?, type: Int) {
        loginResponseFlow.value = ApiMapper(
            ApiCallStatus.LOADING,
            null,
            null
        )
        viewModelScope.launch {
            try {
                val smartLoginRequest = SmartLoginRequest(
                    emailOrPhone = phone,
                    // userId = preferenceManager.getUserId().toString(),
                    refreshToken = preferenceManager.getRefreshToken(),
                    loginType = type,
                    password = text,
                    fcmToken = preferenceManager.getFCMToken()
                )
                val loginResponse = repository.smartLogin(smartLoginRequest)
                when (loginResponse.status) {
                    ApiCallStatus.SUCCESS -> {
                        loginResponseFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                    }
                    ApiCallStatus.ERROR -> {
                        loginResponseFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                loginResponseFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, e.localizedMessage)
            }

        }
    }


}