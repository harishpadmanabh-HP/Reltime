package com.accubits.reltime.activity.signup.viewmodel

import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.activity.signup.model.body.ValidationUser
import com.accubits.reltime.activity.signup.model.response.server.ValidationResponse
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SignupActivityViewModel"

@HiltViewModel
class SignupActivityViewModel @Inject constructor(
    private val repository: ReltimeRepository,
    private val connectivityManager: ConnectivityManager
) : ViewModel() {


    var loginResponseFlow =
        MutableStateFlow<ApiMapper<ValidationResponse>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )

    fun validateUser(userInput: String) {
        viewModelScope.launch {
            loginResponseFlow.value = ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
            try {
                val validationUser = ValidationUser(userInput)
                val response = repository.validateUserNew(validationUser)
                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        loginResponseFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                    }
                    ApiCallStatus.ERROR -> {
                        loginResponseFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                loginResponseFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, e.localizedMessage)
            }
        }
    }

    fun isNetworkAvailable(): Boolean {
        Log.d(TAG, "isAvailable: ${connectivityManager.activeNetworkInfo?.isConnected}")
        var isNetworkAvailable: Boolean = false
        connectivityManager?.activeNetworkInfo?.apply {
            isNetworkAvailable = isConnected
        }
        return isNetworkAvailable
    }


}