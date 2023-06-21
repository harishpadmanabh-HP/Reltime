package com.accubits.reltime.mnemonic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.models.CommonModel
import com.accubits.reltime.models.PublicKeyRequestModel
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImportViewModel @Inject constructor(private var reltimeRepository: ReltimeRepository) :
    ViewModel() {

    var importSuccessModel =
        MutableStateFlow<ApiMapper<CommonModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )

    fun doUpdateKey(token: String, publicKeyRequestModel: PublicKeyRequestModel) {
        importSuccessModel.value= ApiMapper(
            ApiCallStatus.LOADING,
            null,
            null
        )
        viewModelScope.launch {
            val loginResponse = reltimeRepository.postPublicKey(token, publicKeyRequestModel)
            when (loginResponse.status) {
                ApiCallStatus.SUCCESS -> {
                    importSuccessModel.value =
                        ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                }
                ApiCallStatus.ERROR -> {
                    importSuccessModel.value =
                        ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                }
                else -> {}
            }
        }
    }

    fun doRestorePublicKey(token: String, publicKeyRequestModel: PublicKeyRequestModel) {
        importSuccessModel.value= ApiMapper(
            ApiCallStatus.LOADING,
            null,
            null
        )
        viewModelScope.launch {
            val loginResponse = reltimeRepository.doRestorePublicKey(token, publicKeyRequestModel)
            when (loginResponse.status) {
                ApiCallStatus.SUCCESS -> {
                    importSuccessModel.value =
                        ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                }
                ApiCallStatus.ERROR -> {
                    importSuccessModel.value =
                        ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                }
                else -> {}
            }
        }
    }

}