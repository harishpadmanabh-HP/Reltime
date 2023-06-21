package com.accubits.reltime.activity.rto.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.activity.rto.model.RtoP2PRequestModel
import com.accubits.reltime.activity.rto.model.RtoP2PResponseModel
import com.accubits.reltime.activity.rto.model.RtoSignTransactionResponseModel
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.*
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SendRtoViewModel"

@HiltViewModel
class SendRtoViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val repository: ReltimeRepository
) : ViewModel() {
    val p2pTransactionResponseDataFlow = MutableStateFlow<ApiMapper<RtoP2PResponseModel>>(
        ApiMapper(
            ApiCallStatus.LOADING,
            null,
            null
        )
    )
    val jointTransactionCheckResponseDataFlow = MutableStateFlow<ApiMapper<JointAccountCheckSuccessModel>>(
        ApiMapper(
            ApiCallStatus.LOADING,
            null,
            null
        )
    )
    val transactionApprovalResponseDataFlow = MutableStateFlow<ApiMapper<TransactionApprovalSuccessModel>>(
        ApiMapper(
            ApiCallStatus.LOADING,
            null,
            null
        )
    )
    val signTxnApprovalResponseFlow = MutableStateFlow<ApiMapper<RtoSignTransactionResponseModel>>(
        ApiMapper(
            ApiCallStatus.LOADING,
            null,
            null
        )
    )
    val signTransactionResponseFlow = MutableStateFlow<ApiMapper<RtoSignTransactionResponseModel>>(
        ApiMapper(
            ApiCallStatus.LOADING,
            null,
            null
        )
    )

    fun jointTransactionCheck(
        address: String
    ) {
        viewModelScope.launch {
            val joinTransactionRequestModel = JointTransactionRequestModel(
                address = address
            )
            //Log.e(TAG, "sendP2PTransaction: $rtoP2PRequestModel", )
            val response = repository.jointTransactionCheck(
                token = preferenceManager.getApiKey(),
                joinTransactionRequestModel
            )
            when (response.status) {
                ApiCallStatus.SUCCESS -> {
                    jointTransactionCheckResponseDataFlow.value =
                        ApiMapper(ApiCallStatus.SUCCESS, response.data, null)

                }
                ApiCallStatus.ERROR -> {
                    jointTransactionCheckResponseDataFlow.value =
                        ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                }
                else -> {}
            }
        }
    }

    fun transactionApproval(
        receiver: Any, amount: Float,
        coinCode: String
    ) {
        viewModelScope.launch {
            val rtoP2PRequestModel = TransactionApprovalRequestModel(
                jointAccount = receiver.toString(),
                amount = amount,
                coinCode = coinCode
            )
            val response = repository.transactionApproval(
                token = preferenceManager.getApiKey(),
                rtoP2PRequestModel
            )
            when (response.status) {
                ApiCallStatus.SUCCESS -> {
                    transactionApprovalResponseDataFlow.value =
                        ApiMapper(ApiCallStatus.SUCCESS, response.data, null)

                }
                ApiCallStatus.ERROR -> {
                    transactionApprovalResponseDataFlow.value =
                        ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                }
                else -> {}
            }
        }
    }

    fun sendP2PTransaction(
        receiver: Any, amount: Float, contactType: String,
        coinCode: String
    ) {
        viewModelScope.launch {
            val rtoP2PRequestModel = RtoP2PRequestModel(
                receiver = receiver.toString(),
                amount = amount,
                contactType = contactType,
                coinCode = coinCode
            )
            //Log.e(TAG, "sendP2PTransaction: $rtoP2PRequestModel", )
            val response = repository.sendRtoP2PRequestModel(
                token = preferenceManager.getApiKey(),
                rtoP2PRequestModel
            )
            when (response.status) {
                ApiCallStatus.SUCCESS -> {
                    p2pTransactionResponseDataFlow.value =
                        ApiMapper(ApiCallStatus.SUCCESS, response.data, null)

                }
                ApiCallStatus.ERROR -> {
                    p2pTransactionResponseDataFlow.value =
                        ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                }
                else -> {}
            }
        }
    }

    fun signRtoTransaction(
        response: RtoP2PResponseModel,
        type: String = "transferCoin"
    ) {
        viewModelScope.launch {
            if(response.success){
                if(response.result!=null) {
                    val signTransactionRequestModel =
                        SignTransactionRequest(
                            Utils.getKeyHasForTransactionSecond(
                                response.result!!,
                                preferenceManager
                            ),
                            type, response.result.id, null
                        )
                    val response = repository.signRtoTransaction(
                        preferenceManager.getApiKey(), signTransactionRequestModel
                    )
                    when (response.status) {
                        ApiCallStatus.SUCCESS -> {
                            signTransactionResponseFlow.value =
                                ApiMapper(ApiCallStatus.SUCCESS, response.data, null)

                        }
                        ApiCallStatus.ERROR -> {
                            signTransactionResponseFlow.value =
                                ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                        }
                        else -> {}
                    }
                }else{
                    signTransactionResponseFlow.value =
                        ApiMapper(ApiCallStatus.ERROR, null, response.message)
                }
        }else{
                signTransactionResponseFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, response.message)
            }
        }
    }

    fun signTransactionApproval(
        response: TransactionApprovalSuccessModel,
        type: String = ""
    ) {
        viewModelScope.launch {

            val signTransactionRequestModel =
                SignTransactionRequest(
                    Utils.getKeyHasForTransaction(response.result!!.data, preferenceManager),
                    null, null, null
                )
            val response = repository.signRtoTransaction(
                preferenceManager.getApiKey(), signTransactionRequestModel
            )
            when (response.status) {
                ApiCallStatus.SUCCESS -> {
                    signTxnApprovalResponseFlow.value =
                        ApiMapper(ApiCallStatus.SUCCESS, response.data, null)

                }
                ApiCallStatus.ERROR -> {
                    signTxnApprovalResponseFlow.value =
                        ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                }
                else -> {}
            }
        }
    }

}