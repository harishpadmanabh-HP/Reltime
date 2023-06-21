package com.accubits.reltime.activity.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.models.*
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    val repository: ReltimeRepository
) : ViewModel() {

    var notificationModel =
        MutableStateFlow<ApiMapper<NotificationSuccessModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )
    var notificationReadModel =
        MutableStateFlow<ApiMapper<NotificationReadSuccessResponse>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )

    fun setReadAllNotification(token: String, notificationData : RowNotificationModel?, position :Int?) {
        notificationReadModel = MutableStateFlow<ApiMapper<NotificationReadSuccessResponse>>(
            ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
        )
        viewModelScope.launch {
            try {
                var requestModel : NotificationReadRequest
                if(notificationData?.id==null){
                    requestModel=NotificationReadRequest(isRead = true,notificationId = null)
                }else{
                    requestModel=NotificationReadRequest(isRead = null,notificationId = notificationData?.id)
                }
                val loginResponse =
                    repository.setAllNotificationRead(token, requestModel)

                when (loginResponse.status) {
                    ApiCallStatus.SUCCESS -> {
                        loginResponse.data?.notificationData=notificationData
                        loginResponse.data?.position=position
                        notificationReadModel.value =
                            ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                    }
                    ApiCallStatus.ERROR -> {
                        notificationReadModel.value =
                            ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                e.printStackTrace()

            }
        }
    }

    fun getNotificationList(token: String,page :String) {
        notificationModel = MutableStateFlow<ApiMapper<NotificationSuccessModel>>(
            ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
        )
        viewModelScope.launch {
            val loginResponse = repository.getNotificationList(token,page)
            when (loginResponse.status) {
                ApiCallStatus.SUCCESS -> {
                    notificationModel.value =
                        ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                }
                ApiCallStatus.ERROR -> {
                    notificationModel.value =
                        ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                }
                else -> {}
            }
        }
    }

}