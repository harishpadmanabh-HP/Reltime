package com.accubits.reltime.activity.jointAccount.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.activity.jointAccount.model.*
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.PermissionResponseModel
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditJointAccountViewModel @Inject constructor(
    val repository: ReltimeRepository,
    val preferenceManager: PreferenceManager
) : ViewModel() {
    val addUserResponseFlow = MutableStateFlow(
        ApiMapper<CreateJointAccountTxnResponse>(
            ApiCallStatus.LOADING,
            null,
            null
        )
    )
    val removeUserResponseFlow = MutableStateFlow(
        ApiMapper<CreateJointAccountTxnResponse>(
            ApiCallStatus.LOADING,
            null,
            null
        )
    )
    val getPermissionListResponseFlow = MutableStateFlow(
        ApiMapper<PermissionResponseModel>(
            ApiCallStatus.LOADING,
            null,
            null
        )
    )
    val getPermissionResponseFlow = MutableStateFlow(
        ApiMapper<CreateJointAccountTxnResponse>(
            ApiCallStatus.LOADING,
            null,
            null
        )
    )
    val confirmationResponseFlow = MutableStateFlow(
        ApiMapper<CreateJointAccountConfirmationResponse>(
            ApiCallStatus.LOADING,
            null,
            null
        )
    )

    fun editJointAccount(requestData: EditJointAccountRequestModel) {
        viewModelScope.launch {
            val response = repository.editJointAccount(
                preferenceManager.getApiKey(),
                requestData
            )
            when (response.status) {
                ApiCallStatus.LOADING -> {}
                ApiCallStatus.SUCCESS -> {
                    addUserResponseFlow.value =
                        ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                }
                ApiCallStatus.ERROR -> {
                    addUserResponseFlow.value = ApiMapper(ApiCallStatus.ERROR, null, null)
                }
                else -> {}
            }
        }
    }

    fun removeUserFromJointAccount(requestData: DeleteUserRequestModel) {
        viewModelScope.launch {
            val response = repository.removeUserFromJointAccount(
                preferenceManager.getApiKey(),
                requestData
            )
            when (response.status) {
                ApiCallStatus.LOADING -> {}
                ApiCallStatus.SUCCESS -> {
                    removeUserResponseFlow.value =
                        ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                }
                ApiCallStatus.ERROR -> {
                    removeUserResponseFlow.value = ApiMapper(ApiCallStatus.ERROR, null, null)
                }
                else -> {}
            }
        }
    }

    fun setAccountPermission(requestData: PermissionRequestModel) {
        viewModelScope.launch {
            val response = repository.setAccountPermission(
                preferenceManager.getApiKey(),
                requestData
            )
            when (response.status) {
                ApiCallStatus.LOADING -> {}
                ApiCallStatus.SUCCESS -> {
                    getPermissionResponseFlow.value =
                        ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                }
                ApiCallStatus.ERROR -> {
                    getPermissionResponseFlow.value = ApiMapper(ApiCallStatus.ERROR, null, null)
                }
                else -> {}
            }
        }
    }

    fun getAccountPermission() {
        viewModelScope.launch {
            val response = repository.getAccountPermission(
                preferenceManager.getApiKey()
            )
            when (response.status) {
                ApiCallStatus.LOADING -> {}
                ApiCallStatus.SUCCESS -> {
                    getPermissionListResponseFlow.value =
                        ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                }
                ApiCallStatus.ERROR -> {
                    getPermissionListResponseFlow.value = ApiMapper(ApiCallStatus.ERROR, null, null)
                }
                else -> {}
            }
        }
    }

    fun signJoinAccountHash(response: CreateJointAccountTxnResponse,type:String) {
        viewModelScope.launch {
            val txnHash =
                Utils.getKeyHasForTransaction(response.result.data, preferenceManager)
            val id = response.result.jointAccountId

           val response = repository.signJointAccountTransaction(
                preferenceManager.getApiKey(),
                SignJointAccountTxnRequestModel(txnHash = txnHash, id = id, type = type)
            )
            when (response.status) {
                ApiCallStatus.SUCCESS -> {
                    confirmationResponseFlow.value = ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                }
                ApiCallStatus.ERROR -> {
                    confirmationResponseFlow.value = ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                }
                else -> {}
            }
        }
    }

    fun removeJointAccount(requestData: DeleteUserRequestModel) {
        viewModelScope.launch {
            val response = repository.removeJointAccount(
                preferenceManager.getApiKey(),
                requestData
            )
            when (response.status) {
                ApiCallStatus.LOADING -> {}
                ApiCallStatus.SUCCESS -> {
                    removeUserResponseFlow.value =
                        ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                }
                ApiCallStatus.ERROR -> {
                    removeUserResponseFlow.value = ApiMapper(ApiCallStatus.ERROR, null, null)
                }
                else -> {}
            }
        }
    }
}