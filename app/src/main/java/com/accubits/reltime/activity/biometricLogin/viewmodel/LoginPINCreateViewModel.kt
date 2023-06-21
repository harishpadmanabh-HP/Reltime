package com.accubits.reltime.activity.biometricLogin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.activity.biometricLogin.model.LoginPINCreateRequest
import com.accubits.reltime.activity.biometricLogin.model.SmartLoginRequest
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.models.LoginSuccessModel
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginPINCreateViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val repository: ReltimeRepository
) : ViewModel() {
    var loginResponseFlow =
        MutableStateFlow<ApiMapper<LoginSuccessModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )


    fun createLoginPIN(pin: String) {
        loginResponseFlow.value = ApiMapper(
            ApiCallStatus.LOADING,
            null,
            null
        )
        val loginPINCreateRequest= LoginPINCreateRequest(pin =pin )
        viewModelScope.launch {
            try {
                val loginResponse = repository.createLoginPIN(preferenceManager.getApiKey(),loginPINCreateRequest)
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
                if (e !is CancellationException)
                loginResponseFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, e.localizedMessage)
            }

        }
    }


}