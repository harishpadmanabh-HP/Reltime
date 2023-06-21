package com.accubits.reltime.activity.v2.common.accountpicker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.activity.v2.common.accountpicker.AccountPickerComposeActivity.Companion.ACCOUNT_TYPE_MOVE_FROM
import com.accubits.reltime.activity.withdraw.model.AccountListResponse
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountPicketViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val repository: ReltimeRepository
) : ViewModel() {

    val listResponseFlow =
        MutableStateFlow(ApiMapper<AccountListResponse>(ApiCallStatus.LOADING, null, null))

    fun getWithdrawToAccounts(type:String?,withdrawFrom: String?) {
        viewModelScope.launch {
            val response=  if (type==ACCOUNT_TYPE_MOVE_FROM) repository.getWithdrawFromAccounts(preferenceManager.getApiKey())
            else repository.getWithdrawToAccounts(preferenceManager.getApiKey(),withdrawFrom)
            // val response = repository.getWithdrawToAccounts(preferenceManager.getApiKey(),withdrawFrom)
          //  val response = repository.getWithdrawFromAccounts(preferenceManager.getApiKey())

            when (response.status) {
                ApiCallStatus.SUCCESS -> {
                    listResponseFlow.value = ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                }
                ApiCallStatus.ERROR -> {
                    listResponseFlow.value =
                        ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                }
                else -> {}
            }
        }
    }


}