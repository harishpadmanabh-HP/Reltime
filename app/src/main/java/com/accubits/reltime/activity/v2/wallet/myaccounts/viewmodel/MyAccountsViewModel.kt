package com.accubits.reltime.activity.v2.wallet.myaccounts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.activity.myAccounts.model.MyAccountsListModel
import com.accubits.reltime.activity.v2.wallet.myaccounts.model.ExternalAccountListResponse
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyAccountsViewModel @Inject constructor(
    private val repository: ReltimeRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    val internalResponseFlow =
        MutableStateFlow(ApiMapper<MyAccountsListModel>(ApiCallStatus.LOADING, null, null))

    val externalResponseFlow =
        MutableStateFlow(ApiMapper<ExternalAccountListResponse>(ApiCallStatus.LOADING, null, null))

    fun getAccountList() {

        viewModelScope.launch {
            try {
                val response = repository.getAccountList(preferenceManager.getApiKey())
                when (response.status) {

                    ApiCallStatus.SUCCESS -> {
                        internalResponseFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                    }

                    ApiCallStatus.ERROR -> {
                        internalResponseFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                internalResponseFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, e.localizedMessage)
            }
        }
    }


    fun getExternalAccountsList() {
        viewModelScope.launch {
            try {
                val externalResponse =
                    repository.getExternalAccountsList(preferenceManager.getApiKey())
                when (externalResponse.status) {
                    ApiCallStatus.SUCCESS -> {
                        externalResponseFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, externalResponse.data, null)
                    }

                    ApiCallStatus.ERROR -> {
                        externalResponseFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, externalResponse.errorMessage)
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                externalResponseFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, e.localizedMessage)
            }
        }
    }


}