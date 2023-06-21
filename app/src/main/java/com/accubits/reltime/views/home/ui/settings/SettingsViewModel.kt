package com.accubits.reltime.views.home.ui.settings

import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.BuildConfig
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.models.LogoutRequest
import com.accubits.reltime.models.NotificationReadSuccessResponse
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    val repository: ReltimeRepository
) : ViewModel() {

    var logoutModel =
        MutableStateFlow<ApiMapper<NotificationReadSuccessResponse>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )

    var versionFlow = MutableStateFlow<String>("")


    fun getAppVersion() {
        val version = BuildConfig.VERSION_NAME
        versionFlow.value = "Version $version"

    }

    fun doLogout( userId: Int) {
        logoutModel = MutableStateFlow(
            ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
        )
        viewModelScope.launch {
            try {

                val loginResponse =
                    repository.logout(LogoutRequest(userId))

                when (loginResponse.status) {
                    ApiCallStatus.SUCCESS -> {
                        logoutModel.value =
                            ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                    }
                    ApiCallStatus.ERROR -> {
                        logoutModel.value =
                            ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                    }
                    else -> {

                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()

            }
        }
    }
}