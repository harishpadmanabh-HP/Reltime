package com.accubits.reltime.views.biometric

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.activity.settings.model.LoginBiometricStatusRequest
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.models.BioMetricRequest
import com.accubits.reltime.models.CommonModel
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BiometricViewModel @Inject constructor(private var reltimeRepository: ReltimeRepository) :
    ViewModel() {
    var createMpinSuccessModel =
        MutableStateFlow<ApiMapper<CommonModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )

    val loginBiometricStatusFlow =
        MutableStateFlow<ApiMapper<CommonModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )

    fun doEnableOrDisableBiometric(token: String, biometricRequest: BioMetricRequest) {
        createMpinSuccessModel = MutableStateFlow<ApiMapper<CommonModel>>(
            ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
        )
        viewModelScope.launch {
            val loginResponse =
                reltimeRepository.doEnableOrDisableBiometric(token, biometricRequest)
            when (loginResponse.status) {
                ApiCallStatus.SUCCESS -> {
                    createMpinSuccessModel.value =
                        ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                }
                ApiCallStatus.ERROR -> {
                    createMpinSuccessModel.value =
                        ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                }
                else -> {}
            }
        }
    }

    fun changeLoginBiometricStatus(token: String, isChecked: Boolean) {
        loginBiometricStatusFlow.value = ApiMapper(
            ApiCallStatus.LOADING,
            null,
            null
        )
        viewModelScope.launch {
            try {
                val loginResponse =
                    reltimeRepository.changeLoginBiometricStatus(
                        token,
                        LoginBiometricStatusRequest(isChecked)
                    )
                when (loginResponse.status) {
                    ApiCallStatus.SUCCESS -> {
                        loginBiometricStatusFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                    }
                    ApiCallStatus.ERROR -> {
                        loginBiometricStatusFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                    }
                }

            } catch (ex: Exception) {
                loginBiometricStatusFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, ex.localizedMessage)
            }
        }
    }
}