package com.accubits.reltime.activity.v2.transfer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig

import com.accubits.reltime.activity.myAccounts.model.MyAccountsListModel
import com.accubits.reltime.activity.myAccounts.model.ReltimeAccount
import com.accubits.reltime.activity.v2.common.walletpicker.model.WalletAddressValidationRequest
import com.accubits.reltime.activity.v2.transfer.TransferObject
import com.accubits.reltime.activity.v2.transfer.pagingSource.TransferTransactionPagingV2Source
import com.accubits.reltime.activity.v2.wallet.swap.model.CryptoCurrency
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.helpers.Utils
import com.accubits.reltime.models.*
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val repository: ReltimeRepository,
    private val preferenceManager: PreferenceManager,
    private val transactionPagingSource: TransferTransactionPagingV2Source,

    ) : ViewModel() {

    var userName: String = ""
    var profileImage = ""
    var userId: String = ""
    var mobileNumber: String = ""
    var amount = ""
    var description = ""
    var coinType = ""
    var contactType = ""
    var address = ""
    var receiver = ""
    var method = ""
    var timeStamp = ""
    var coinCode = ""
    var mode = ""
    var reciver = ""
    var senderName = ""
    var senderNumber = ""
    var senderEmail = ""
    var senderAddress = ""
    var receiverEmail = ""
    var receiverNumber = ""
    var receiverAddress = ""
    var balance = ""

    val selectedFromAccount = MutableStateFlow<ReltimeAccount?>(null)

    fun setSelectedFromAccount(reltimeAccount: ReltimeAccount){
        selectedFromAccount.value=reltimeAccount
    }

    var walletAmountSuccessModel =
        MutableStateFlow<ApiMapper<WalletAmountSuccessModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )


    var walletSiginFlow =
        MutableStateFlow<ApiMapper<WalletSignInModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )


    var intialwalletSiginFlow =
        MutableStateFlow<ApiMapper<WalletSignInModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )


    var transferApprovalSiginFlow =
        MutableStateFlow<ApiMapper<TransactionApprovalSuccessModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )


    var transferResponseFlow =
        MutableStateFlow<ApiMapper<TransferResponseModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )

    val listAccountsResponseFlow =
        MutableStateFlow(ApiMapper<MyAccountsListModel>(ApiCallStatus.EMPTY, null, null))
    var search = MutableStateFlow<String>("")

    var searchContactResponseFlow =
        MutableStateFlow(ApiMapper<SearchUserResponseModel>(ApiCallStatus.EMPTY, null, null))

    val walletValidateResponseFlow =
        MutableStateFlow(
            ApiMapper<SearchUserResponseModel>(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )

    val tokenResponseFlow =
        MutableStateFlow(ApiMapper<TokenCodeResponse>(ApiCallStatus.EMPTY, null, null))

    val verifyEmailFlow =
        MutableStateFlow(ApiMapper<SearchUserResponseModel>(ApiCallStatus.EMPTY, null, null))


    fun getAccountList() {

        viewModelScope.launch {
            try {
                listAccountsResponseFlow.value=ApiMapper(ApiCallStatus.LOADING, null, null)
                val response = repository.getTransferAccountList(preferenceManager.getApiKey())
                when (response.status) {

                    ApiCallStatus.SUCCESS -> {
                        listAccountsResponseFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                    }

                    ApiCallStatus.ERROR -> {
                        listAccountsResponseFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                    }
                }
            } catch (e: Exception) {
                listAccountsResponseFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, e.localizedMessage)
            }
        }
    }

    fun getPagedTransactionData() = search.flatMapLatest { querry ->
        Pager(PagingConfig(20), pagingSourceFactory = {
            transactionPagingSource
        }).flow
    }


    fun searchContact(phoneNumber: String) {
        searchContactResponseFlow.value = ApiMapper(
            ApiCallStatus.LOADING,
            null,
            null
        )
        var request = SearchUserRequest(phoneNumber)
        viewModelScope.launch {
            try {
                val loginResponse =
                    repository.searchUserContact(preferenceManager.getApiKey(), request)
                when (loginResponse.status) {
                    ApiCallStatus.SUCCESS -> {
                        searchContactResponseFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                    }
                    ApiCallStatus.ERROR -> {
                        searchContactResponseFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                searchContactResponseFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, e.message)
            }
        }
    }


    fun walletAddressValidation(address: String, coinType: String) {
        walletValidateResponseFlow.value = ApiMapper(
            ApiCallStatus.LOADING,
            null,
            null
        )
        viewModelScope.launch {
            try {
                val request = WalletAddressValidationRequest(coinType, address)
                val response =
                    repository.walletAddressValidation(preferenceManager.getApiKey(), request)
                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        walletValidateResponseFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                    }
                    ApiCallStatus.ERROR -> {
                        walletValidateResponseFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                walletValidateResponseFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, e.message)
            }
        }
    }


    fun getTokenCoins() {
        tokenResponseFlow.value = ApiMapper(
            ApiCallStatus.LOADING,
            null,
            null
        )
        viewModelScope.launch {
            try {
                val response =
                    repository.getCoinTokens(preferenceManager.getApiKey())
                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        tokenResponseFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                    }
                    ApiCallStatus.ERROR -> {
                        tokenResponseFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                tokenResponseFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, e.message)
            }
        }
    }


    fun verifyEmail(emailId: String) {
        verifyEmailFlow.value = ApiMapper(
            ApiCallStatus.LOADING,
            null,
            null
        )

        viewModelScope.launch {
            try {
                val request = EmailVerificationRequest(email = emailId)
                val response = repository.emailVerification(preferenceManager.getApiKey(), request)

                when (response.status) {
                    ApiCallStatus.SUCCESS -> {
                        verifyEmailFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                    }
                    ApiCallStatus.ERROR -> {
                        verifyEmailFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                    }
                    else -> {

                    }
                }
            } catch (e: Exception) {
                verifyEmailFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, e.message)
            }
        }
    }

    fun getWalletDetailsRTORTC() {
        walletAmountSuccessModel.value = ApiMapper(
            ApiCallStatus.LOADING,
            null,
            null
        )
        viewModelScope.launch {
            val response = repository.getWalletAmountDetails(preferenceManager.getApiKey())
            when (response.status) {
                ApiCallStatus.SUCCESS -> {
                    walletAmountSuccessModel.value =
                        ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                }
                ApiCallStatus.ERROR -> {
                    walletAmountSuccessModel.value =
                        ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                }
                else -> {}
            }

        }
    }


    fun performTransfer(request: TransferRequest) {

        transferResponseFlow.value = ApiMapper(
            ApiCallStatus.LOADING,
            null,
            null
        )
        viewModelScope.launch {
            val response =
                repository.performTransfer(preferenceManager.getApiKey(), request = request)
            when (response.status) {
                ApiCallStatus.SUCCESS -> {
                    transferResponseFlow.value =
                        ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                }
                ApiCallStatus.ERROR -> {
                    transferResponseFlow.value =
                        ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                }
                else -> {}
            }

        }
    }

    fun walletSigin(id: Int, data: TransferResponseModel.Data) {

        val signTxn =
            Utils.getKeyHasForTransferTransaction(data.data, preferenceManager)

        val request = WalletSigninRequest(id, signTxn, "transferCoin")
        walletSiginFlow.value = ApiMapper(
            ApiCallStatus.LOADING,
            null,
            null
        )
        viewModelScope.launch {

            val response =
                repository.performWalletSigin(preferenceManager.getApiKey(), request = request)
            when (response.status) {
                ApiCallStatus.SUCCESS -> {
                    walletSiginFlow.value =
                        ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                }
                ApiCallStatus.ERROR -> {
                    walletSiginFlow.value =
                        ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                }
                else -> {}
            }

        }
    }


    fun performTransferApproval(request: TransferApprovalRequest) {


        transferApprovalSiginFlow.value = ApiMapper(
            ApiCallStatus.LOADING,
            null,
            null
        )
        viewModelScope.launch {

            val response =
                repository.performTransferApproval(preferenceManager.getApiKey(), request = request)
            when (response.status) {
                ApiCallStatus.SUCCESS -> {
                    transferApprovalSiginFlow.value =
                        ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                }
                ApiCallStatus.ERROR -> {
                    transferApprovalSiginFlow.value =
                        ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                }
                else -> {}
            }

        }
    }

    fun intialWalletSigIn(data: Data) {

        val signTxn =
            Utils.getKeyHasForTransaction(data, preferenceManager)

        val request = WalletSigninRequest(signedTxn = signTxn)
        intialwalletSiginFlow.value = ApiMapper(
            ApiCallStatus.LOADING,
            null,
            null
        )
        viewModelScope.launch {

            val response =
                repository.performWalletSigin(preferenceManager.getApiKey(), request = request)
            when (response.status) {
                ApiCallStatus.SUCCESS -> {
                    intialwalletSiginFlow.value =
                        ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                }
                ApiCallStatus.ERROR -> {
                    intialwalletSiginFlow.value =
                        ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                }
                else -> {}
            }

        }
    }


}