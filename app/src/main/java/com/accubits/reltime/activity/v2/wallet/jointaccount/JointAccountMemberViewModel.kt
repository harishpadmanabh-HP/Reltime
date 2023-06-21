package com.accubits.reltime.activity.v2.wallet.jointaccount

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.activity.jointAccount.model.*
import com.accubits.reltime.activity.jointAccount.model.singleJointAccountModel.SingleJointAccountModel
import com.accubits.reltime.activity.v2.wallet.accountDetail.AccountTransactionPagingSourceV2
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
class JointAccountMemberViewModel @Inject constructor(
    private val repository: ReltimeRepository,
    private val preferenceManager: PreferenceManager,
    private val transactionPagingSource: AccountTransactionPagingSourceV2
) : ViewModel() {

    val removeUserResponseFlow = MutableStateFlow(
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

    val jointAccountDetailsFlow =
        MutableStateFlow(ApiMapper<SingleJointAccountModel>(ApiCallStatus.LOADING, null, null))

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
}