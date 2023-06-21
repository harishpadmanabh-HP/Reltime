package com.accubits.reltime.activity.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.activity.jointAccount.model.CreateJointAccountConfirmationResponse
import com.accubits.reltime.activity.jointAccount.model.CreateJointAccountTxnResponse
import com.accubits.reltime.activity.jointAccount.model.SignJointAccountTxnRequestModel
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.AcceptRejectRequest
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationDetailsViewModel @Inject constructor(
    val repository: ReltimeRepository,
    val preferenceManager: PreferenceManager
) : ViewModel() {

    var acceptRejectModelModel =
        MutableStateFlow<ApiMapper<CreateJointAccountTxnResponse>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )

    var confirmationResponseFlow = MutableStateFlow(
        ApiMapper<CreateJointAccountConfirmationResponse>(
            ApiCallStatus.EMPTY,
            null,
            null
        )
    )

    fun getAcceptRejectJointAccountRequest(token: String, request: AcceptRejectRequest) {
        acceptRejectModelModel = MutableStateFlow<ApiMapper<CreateJointAccountTxnResponse>>(
            ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
        )


        viewModelScope.launch {
            try {
                val acceptRejectResponse = repository.acceptRejectJointAccount(token, request)
                when (acceptRejectResponse.status) {
                    ApiCallStatus.SUCCESS -> {
                        acceptRejectModelModel.value =
                            ApiMapper(ApiCallStatus.SUCCESS, acceptRejectResponse.data, null)
                    }
                    ApiCallStatus.ERROR -> {
                        acceptRejectModelModel.value =
                            ApiMapper(ApiCallStatus.ERROR, null, acceptRejectResponse.errorMessage)
                    }
                    else -> {}
                }
            } catch (ex: Exception) {
                acceptRejectModelModel.value =
                    ApiMapper(ApiCallStatus.ERROR, null, ex.message)
            }
        }
    }

    fun signJoinAccountHash(response: CreateJointAccountTxnResponse) {
        confirmationResponseFlow =
            MutableStateFlow<ApiMapper<CreateJointAccountConfirmationResponse>>(
                ApiMapper(
                    ApiCallStatus.LOADING,
                    null,
                    null
                )
            )
        viewModelScope.launch {
            try {
                val txnHash =
                    Utils.getKeyHasForTransaction(response.result.data, preferenceManager)
                val id = response.result.jointAccountId
                val type = "manageInvite"
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
                acceptRejectModelModel.value =
                    ApiMapper(ApiCallStatus.ERROR, null, ex.message)
            }
        }
    }
}