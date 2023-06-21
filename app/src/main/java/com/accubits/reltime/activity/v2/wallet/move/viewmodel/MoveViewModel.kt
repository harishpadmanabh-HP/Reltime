package com.accubits.reltime.activity.v2.wallet.move.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.activity.myAccounts.model.JointAccount
import com.accubits.reltime.activity.myAccounts.model.ReltimeAccount
import com.accubits.reltime.activity.v2.wallet.swap.model.SwapApprovalRequest
import com.accubits.reltime.activity.withdraw.model.AccountListResponse
import com.accubits.reltime.activity.withdraw.model.JointAccountWithdrawRequestNew
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.CurrencyConversionSuccessModel
import com.accubits.reltime.models.TransferApprovalRequest
import com.accubits.reltime.models.WalletSignInModel
import com.accubits.reltime.models.WalletSigninRequest
import com.accubits.reltime.repository.ReltimeRepository
import com.accubits.reltime.utils.Extensions.accountWithdrawType
import com.accubits.reltime.utils.Extensions.getAddress
import com.accubits.reltime.utils.Extensions.getCoinCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoveViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val repository: ReltimeRepository
) : ViewModel() {
    var amountSent: String? = null
    val selectedFromAccount = MutableStateFlow<ReltimeAccount?>(null)
    val selectedToAccount = MutableStateFlow<ReltimeAccount?>(null)

    val withdrawFromFlow =
        MutableStateFlow(ApiMapper<AccountListResponse>(ApiCallStatus.LOADING, null, null))

    val withdrawToFlow =
        MutableStateFlow(ApiMapper<AccountListResponse>(ApiCallStatus.EMPTY, null, null))

    val paymentStatisticsResponseFlow =
        MutableStateFlow<ApiMapper<CurrencyConversionSuccessModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )
   // RtoSignTransactionResponseModel
    val withdrawConfirmRequestFlow = MutableStateFlow<ApiMapper<WalletSignInModel>>(
        ApiMapper(
            ApiCallStatus.EMPTY,
            null,
            null
        )
    )

    fun setSelectedFromAccount(account: ReltimeAccount?) {
        selectedFromAccount.value = account
        getWithdrawToAccounts(
            withdrawFrom = account?.accountWithdrawType(),
            coinCode = account?.getCoinCode(),
            address = account?.getAddress()
        )
        selectedToAccount.value = null
    }

    fun setSelectedToAccount(account: ReltimeAccount?) {
        selectedToAccount.value = account
    }

    fun getWithdrawFromAccounts() {
        viewModelScope.launch {
            val response = repository.getWithdrawFromAccounts(preferenceManager.getApiKey())
            try {
                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        withdrawFromFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                    }

                    ApiCallStatus.ERROR -> {
                        withdrawFromFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                    }
                    else -> {}
                }
            } catch (ex: Exception) {
                withdrawFromFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, ex.message)
            }
        }
    }

    fun getWithdrawToAccounts(
        withdrawFrom: String?,
        coinCode: String?,
        address: String?
    ) {
        viewModelScope.launch {
            try {
                withdrawToFlow.value = ApiMapper(
                    ApiCallStatus.LOADING,
                    null,
                    null
                )
                val response =
                    repository.getV1WithdrawToAccounts(
                        preferenceManager.getApiKey(),
                        withdrawFrom = withdrawFrom,
                        coinCode = coinCode,
                        address = address
                    )
                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        withdrawToFlow.value = ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                    }
                    ApiCallStatus.ERROR -> {
                        withdrawToFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                withdrawToFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, e.message)
            }
        }
    }

    fun getPaymentStatistics(coinCode: String?, amount: String, sourceCoinCode: String?) {
        viewModelScope.launch {
            try {
                paymentStatisticsResponseFlow.value = ApiMapper(
                    ApiCallStatus.LOADING,
                    null,
                    null
                )
                val loginResponse =
                    repository.getPaymentStatistics(
                        preferenceManager.getApiKey(),
                        coinCode,
                        amount,
                        sourceCoinCode
                    )
                when (loginResponse.status) {
                    ApiCallStatus.SUCCESS -> {
                        paymentStatisticsResponseFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                    }
                    ApiCallStatus.ERROR -> {
                        paymentStatisticsResponseFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                    }
                    else -> {}
                }
            } catch (ex: Exception) {
                paymentStatisticsResponseFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, ex.localizedMessage)
            }
        }
    }



    fun doMove(amount: String?) {
        amountSent = amount
        val fromAccount = selectedFromAccount.value
        if (fromAccount is JointAccount||fromAccount?.getCoinCode() =="RTC")
            doMove()
        else if (fromAccount?.getCoinCode() =="RTO" && selectedToAccount.value is JointAccount)
            signJointAccount()
        else moveApproval()
    }

    private fun moveApproval() {
        viewModelScope.launch {
            withdrawConfirmRequestFlow.value =
                ApiMapper(ApiCallStatus.LOADING, null, null)
            val response = repository.swapApproval(
                preferenceManager.getApiKey(),
                SwapApprovalRequest(
                    coinCode = selectedFromAccount.value!!.getCoinCode(),
                    amount = amountSent//?.toDoubleOrNull()
                )
            )
            try {
                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        if (response.data?.status == 200 && response.data.success && response.data.result != null)
                            signMove(
                                WalletSigninRequest(
                                    signedTxn = Utils.getKeyHasForTransaction(
                                        response.data.result!!.data,
                                        preferenceManager
                                    )
                                )
                            )
                        else withdrawConfirmRequestFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.data?.message)
                    }

                    ApiCallStatus.ERROR -> {
                        withdrawConfirmRequestFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                    }
                    else -> {}
                }
            } catch (ex: Exception) {
                withdrawConfirmRequestFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, ex.message)
            }
        }
    }

    private fun signMove(data: WalletSigninRequest) {
        viewModelScope.launch {
            try {
                val response =
                    repository.performWalletSigin(
                        preferenceManager.getApiKey(), request = data
                    )
                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        if (response.data?.status == 200 && response.data.success)
                            if (response.data.transactionItem.txnId == null)
                                doMove()
                                 else
                                     withdrawConfirmRequestFlow.value =
                                         ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                            else withdrawConfirmRequestFlow.value =
                                ApiMapper(ApiCallStatus.ERROR, null, response.data?.message)
                    }
                    ApiCallStatus.ERROR -> {
                        withdrawConfirmRequestFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                    }
                    else -> {}
                }
            } catch (ex: Exception) {
                withdrawConfirmRequestFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, ex.message)
            }
        }
    }

    private fun signJointAccount(){
        viewModelScope.launch {
            try {
                withdrawConfirmRequestFlow.value =
                    ApiMapper(ApiCallStatus.LOADING, null, null)
                val response =
                    repository.performTransferApproval(preferenceManager.getApiKey(), request =  TransferApprovalRequest(
                        amount = amountSent?.toDouble(),
                        coinCode =selectedFromAccount.value?.getCoinCode(),
                        joint_account =(selectedToAccount.value as JointAccount).address
                    ))
                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        if (response.data?.status == 200 && response.data.success && response.data.result != null)
                            signMove(
                                WalletSigninRequest(
                                    signedTxn = Utils.getKeyHasForTransaction(
                                        response.data.result!!.data,
                                        preferenceManager
                                    )
                                )
                            )
                        else withdrawConfirmRequestFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.data?.message)
                    }

                    ApiCallStatus.ERROR -> {
                        withdrawConfirmRequestFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                    }
                    else -> {}
                }
            } catch (ex: Exception) {
                withdrawConfirmRequestFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, ex.message)
            }
        }
    }

    private fun doMove() {
        val jointAccountWithdrawRequestNew = JointAccountWithdrawRequestNew(
            withdraw_from = selectedFromAccount.value?.getAddress(),
            withdraw_from_type = selectedFromAccount.value?.accountWithdrawType(),//"jointAccounts",
            coinCode = selectedFromAccount.value?.getCoinCode(),
            amount = amountSent,
            withdraw_to = selectedToAccount.value?.getAddress(),
            withdraw_to_type = selectedToAccount.value?.accountWithdrawType(),//"wallets",
            withdraw_to_coinCode = selectedToAccount.value?.getCoinCode()
        )
        viewModelScope.launch {
            if(withdrawConfirmRequestFlow.value != ApiMapper(
                    ApiCallStatus.LOADING,
                    null,
                    null
                ))
            withdrawConfirmRequestFlow.value = ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
            try {
                val response = repository.doMove(
                    preferenceManager.getApiKey(), jointAccountWithdrawRequestNew
                )
                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        val responseOk = response.data?.status == 200 && response.data.success
                        if (responseOk && response.data != null && response.data.result?.data != null) {
                            signMove(
                                WalletSigninRequest(
                                    id = response.data.result.id, type = "moveCoin",
                                    signedTxn = Utils.getKeyHasForTransaction(
                                        response.data.result.data,
                                        preferenceManager
                                    )
                                )
                            )
                        } else
                            response.data?.let {
                                withdrawConfirmRequestFlow.value =
                                    ApiMapper(ApiCallStatus.ERROR, null, it.message)
                            }
                    }
                    ApiCallStatus.ERROR -> {
                        withdrawConfirmRequestFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                    }
                    else -> {}
                }
            } catch (ex: Exception) {
                withdrawConfirmRequestFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, ex.message)
            }
        }
    }


}