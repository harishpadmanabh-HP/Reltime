package com.accubits.reltime.activity.addBankAccount.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.activity.addBankAccount.model.AddBankAccountResponse
import com.accubits.reltime.activity.addBankAccount.model.BankListResponse
import com.accubits.reltime.activity.addBankAccount.model.PostBankAccountRequest
import com.accubits.reltime.activity.rto.model.RtoSignTransactionResponseModel
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBankAccountViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val repository: ReltimeRepository
) : ViewModel() {

    val bankListResponseFlow =
        MutableStateFlow(ApiMapper<BankListResponse>(ApiCallStatus.LOADING, null, null))

    val addBankAccountRequestFlow = MutableStateFlow<ApiMapper<AddBankAccountResponse>>(
        ApiMapper(
            ApiCallStatus.EMPTY,
            null,
            null
        )
    )

    fun getBankList() {
        viewModelScope.launch {
            val response = repository.getBankList(preferenceManager.getApiKey())
            when (response.status) {
                ApiCallStatus.SUCCESS -> {
                    bankListResponseFlow.value =
                        ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                }
                ApiCallStatus.ERROR -> {
                    bankListResponseFlow.value =
                        ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                }
                else -> {}
            }
        }
    }

    fun postBankAccount(
        bankName: String? = null, accountNumber: String? = null,
        swiftCode: String? = null,
        fullName: String? = null,
        poNumber: String? = null,
        address: String? = null,
        address2: String? = null,
        contactNumber: String? = null,
        accountTpe: String? = null,
        documentFile: ByteArray,
        documentFileName: String
    ) {
        viewModelScope.launch {
            addBankAccountRequestFlow.value = ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )

            val response = repository.postBankAccount(
                preferenceManager.getApiKey(), bankName,
                accountNumber,
                fullName, swiftCode,poNumber=poNumber,
                address=address,address2=address2, contactNumber, accountTpe, documentFile, documentFileName
            )
            when (response.status) {
                ApiCallStatus.SUCCESS -> {
                    addBankAccountRequestFlow.value =
                        ApiMapper(ApiCallStatus.SUCCESS, response.data, null)

                }
                ApiCallStatus.ERROR -> {
                    addBankAccountRequestFlow.value =
                        ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                }
                else -> {}
            }
        }
    }
}
