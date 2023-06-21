package com.accubits.reltime.activity.v2.signUp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.activity.v2.signUp.model.SignUpRequestModel
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.models.LoginSuccessModel
import com.accubits.reltime.models.OtpVerifySuccessModel
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "WithdrawViewModel"

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val repository: ReltimeRepository
) : ViewModel() {

    val signUpFlow =
        MutableStateFlow(ApiMapper<LoginSuccessModel>(ApiCallStatus.EMPTY, null, null))

     fun signUpV2(request: SignUpRequestModel) {
        viewModelScope.launch {
            signUpFlow.value = ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
            try {
                val response = repository.signUpV2(request)
                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        signUpFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                    }
                    ApiCallStatus.ERROR -> {
                        signUpFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                signUpFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, e.localizedMessage)
            }
        }
    }



}