package com.accubits.reltime.activity.v2.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.activity.v2.splash.model.InitDataResponse
import com.accubits.reltime.activity.withdraw.model.AccountListResponse
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.models.CheckMpinSuccessModel
import com.accubits.reltime.repository.ReltimeRepository
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "WithdrawViewModel"

@HiltViewModel
class AppInitViewModel @Inject constructor(
    private val repository: ReltimeRepository
) : ViewModel() {
    var initDataResponseFlow =
        MutableStateFlow<ApiMapper<InitDataResponse>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )

    fun getInitData() {
        initDataResponseFlow.value = ApiMapper(
            ApiCallStatus.LOADING,
            null,
            null
        )
        viewModelScope.launch {
            val loginResponse = repository.getInitData()
            try {
                when (loginResponse.status) {
                    ApiCallStatus.SUCCESS -> {
                        initDataResponseFlow.value =
                            ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                    }
                    ApiCallStatus.ERROR -> {
                        initDataResponseFlow.value =
                            ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                    }
                    else -> {}
                }
            } catch (ex: Exception) {
                if (ex !is CancellationException)
                    initDataResponseFlow.value =
                        ApiMapper(ApiCallStatus.ERROR, null, ex.message)
            }
        }
    }
}