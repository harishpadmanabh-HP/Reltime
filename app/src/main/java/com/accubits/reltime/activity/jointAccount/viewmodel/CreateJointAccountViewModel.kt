package com.accubits.reltime.activity.jointAccount.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.activity.jointAccount.model.CreateJointAccountConfirmationResponse
import com.accubits.reltime.activity.jointAccount.model.CreateJointAccountRequestModel
import com.accubits.reltime.activity.jointAccount.model.CreateJointAccountTxnResponse
import com.accubits.reltime.activity.jointAccount.model.SignJointAccountTxnRequestModel
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateJointAccountViewModel @Inject constructor(
    val repository: ReltimeRepository,
    val preferenceManager: PreferenceManager
) : ViewModel() {
    val txnBuildResponseFlow = MutableStateFlow(
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

    fun createJointAccount(requestData: CreateJointAccountRequestModel) {
        viewModelScope.launch {
            try {
                val response = repository.createJointAccount(
                    preferenceManager.getApiKey(),
                    requestData
                )
                when (response.status) {
                    ApiCallStatus.LOADING -> {}
                    ApiCallStatus.SUCCESS -> {
                        txnBuildResponseFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                    }
                    ApiCallStatus.ERROR -> {
                        txnBuildResponseFlow.value = ApiMapper(ApiCallStatus.ERROR, null, null)
                    }
                    else -> {}
                }
            } catch (ex: Exception) {
                txnBuildResponseFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, ex.message)
            }
        }
    }

    fun signJoinAccountHash(response: CreateJointAccountTxnResponse) {
        viewModelScope.launch {
            try {
            val txnHash =
                Utils.getKeyHasForTransaction(response.result.data, preferenceManager)
            val id = response.result.jointAccountId
            val type = "createJointAccount"
            val response = repository.signJointAccountTransaction(
                preferenceManager.getApiKey(),
                SignJointAccountTxnRequestModel(txnHash = txnHash, id = id, type = type)
            )
            when (response.status) {
                ApiCallStatus.SUCCESS -> {
                    confirmationResponseFlow.value =
                        ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                }
                ApiCallStatus.ERROR -> {
                    confirmationResponseFlow.value =
                        ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                }
                else -> {}
            }
        } catch (ex: Exception) {
            confirmationResponseFlow.value =
                ApiMapper(ApiCallStatus.ERROR, null, ex.message)
        }
        }
    }
}