package com.accubits.reltime.activity.v2.wallet.accountDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.accubits.reltime.activity.jointAccount.model.singleJointAccountModel.SingleJointAccountModel
import com.accubits.reltime.activity.myAccounts.model.MyAccountsListModel
import com.accubits.reltime.activity.v2.wallet.TransactionPagingV2Source
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.models.AccountDetailResponseModel
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountDetailViewModel  @Inject constructor(
    private val repository: ReltimeRepository,
    private val preferenceManager: PreferenceManager,
    private val transactionPagingSource: AccountTransactionPagingSourceV2
) : ViewModel() {

    val accountDetailResponseFlow =
        MutableStateFlow(ApiMapper<AccountDetailResponseModel>(ApiCallStatus.LOADING, null, null))

    val jointAccountDetailsFlow =
        MutableStateFlow(ApiMapper<SingleJointAccountModel>(ApiCallStatus.LOADING, null, null))

    var search = MutableStateFlow<String>("")

    fun getAccountDetails(id: Int, type: String) {

        viewModelScope.launch {
            try {
                val response = repository.getAccountDetail(preferenceManager.getApiKey(), id, type)
                when (response.status) {

                    ApiCallStatus.SUCCESS -> {
                        accountDetailResponseFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                    }

                    ApiCallStatus.ERROR -> {
                        accountDetailResponseFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, response.errorMessage)
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                accountDetailResponseFlow.value =
                    ApiMapper(ApiCallStatus.ERROR, null, e.localizedMessage)
            }
        }
    }

    fun getPagedTransactionData() = search.flatMapLatest { querry ->
        Pager(PagingConfig(20), pagingSourceFactory = {
            transactionPagingSource
        }).flow
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
}