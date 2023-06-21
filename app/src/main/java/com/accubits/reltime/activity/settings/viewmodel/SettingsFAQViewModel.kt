package com.accubits.reltime.activity.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.models.CardSuccessModel
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsFAQViewModel @Inject constructor(
    val repository: ReltimeRepository
) : ViewModel() {


    var cardDetailsModel =
        MutableStateFlow<ApiMapper<CardSuccessModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )

    fun getCardDetails(token: String) {
        cardDetailsModel = MutableStateFlow<ApiMapper<CardSuccessModel>>(
            ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
        )
        viewModelScope.launch {
            val loginResponse = repository.getCardDetails(token)
            when (loginResponse.status) {
                ApiCallStatus.SUCCESS -> {
                    cardDetailsModel.value =
                        ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                }
                ApiCallStatus.ERROR -> {
                    cardDetailsModel.value =
                        ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                }
                else -> {}
            }
        }
    }
}