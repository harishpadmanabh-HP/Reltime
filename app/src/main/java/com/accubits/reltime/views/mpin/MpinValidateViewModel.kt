package com.accubits.reltime.views.mpin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.models.CommonModel
import com.accubits.reltime.models.MpinValidateRequestModel
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MpinValidateViewModel @Inject constructor(private var reltimeRepository: ReltimeRepository) :
    ViewModel() {
    var createMpinSuccessModel =
        MutableStateFlow<ApiMapper<CommonModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )

    fun perfomMpinValidation(token: String, mpinCreation: MpinValidateRequestModel) {
        createMpinSuccessModel = MutableStateFlow<ApiMapper<CommonModel>>(
            ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
        )
        viewModelScope.launch {
            val loginResponse = reltimeRepository.validateMpin(token, mpinCreation)
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
}