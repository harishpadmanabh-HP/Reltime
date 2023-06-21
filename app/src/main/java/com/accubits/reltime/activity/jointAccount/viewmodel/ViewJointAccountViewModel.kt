package com.accubits.reltime.activity.jointAccount.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.activity.jointAccount.model.*
import com.accubits.reltime.activity.jointAccount.model.singleJointAccountModel.SingleJointAccountModel
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
class ViewJointAccountViewModel @Inject constructor(
    private val repository: ReltimeRepository,
    private val preferenceManager: PreferenceManager
): ViewModel() {
    val jointAccountDetailsFlow =
        MutableStateFlow(ApiMapper<SingleJointAccountModel>(ApiCallStatus.LOADING, null, null))

    val removeAccountResponseFlow =
        MutableStateFlow(ApiMapper<CreateJointAccountTxnResponse>(ApiCallStatus.LOADING, null, null))
    val confirmationResponseFlow = MutableStateFlow(
        ApiMapper<CreateJointAccountConfirmationResponse>(
            ApiCallStatus.LOADING,
            null,
            null
        )
    )
    fun fetchJointAccounts(accountId: Int) {
        viewModelScope.launch {
            val response = repository.fetchJointAccounts(preferenceManager.getApiKey(), accountId)
            when(response.status) {
                ApiCallStatus.SUCCESS -> {
                    jointAccountDetailsFlow.value = ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                }
                ApiCallStatus.ERROR -> {
                    jointAccountDetailsFlow.value = ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
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
                    removeAccountResponseFlow.value =
                        ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                }
                ApiCallStatus.ERROR -> {
                    removeAccountResponseFlow.value = ApiMapper(ApiCallStatus.ERROR, null, null)
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
}