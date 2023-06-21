package com.accubits.reltime.views.home.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.activity.settings.model.CloseRequestHome
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.models.CardSuccessModel
import com.accubits.reltime.models.CloseSuccesModel
import com.accubits.reltime.models.WalletAmountSuccessModel
import com.accubits.reltime.models.WalletSuccessModel
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(private val reltimeRepository: ReltimeRepository) :
    ViewModel() {
    var walletSuccessModel =
        MutableStateFlow<ApiMapper<WalletSuccessModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )

    var walletAmountSuccessModel =
        MutableStateFlow<ApiMapper<WalletAmountSuccessModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )

    var closeSuccesModel =
        MutableStateFlow<ApiMapper<CloseSuccesModel>>(
            ApiMapper(
                ApiCallStatus.EMPTY,
                null,
                null
            )
        )

    fun getWalletDetails(token: String) {
        walletSuccessModel.value = ApiMapper(
            ApiCallStatus.LOADING,
            null,
            null
        )
        walletAmountSuccessModel.value = ApiMapper(
            ApiCallStatus.LOADING,
            null,
            null
        )
        viewModelScope.launch {
            async {

                try {
                    val loginResponse = reltimeRepository.getWalletDetails(token)
                    val response = reltimeRepository.getWalletAmountDetails(token)

                    when (loginResponse.status) {
                        ApiCallStatus.SUCCESS -> {
                            walletSuccessModel.value =
                                ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                        }
                        ApiCallStatus.ERROR -> {
                            walletSuccessModel.value =
                                ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                        }
                        else -> {}
                    }
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

                } catch (e: Exception) {
                    if (e !is CancellationException)
                        walletSuccessModel.value =
                            ApiMapper(ApiCallStatus.ERROR, null, e.localizedMessage)
                }
            }

        }
    }

    fun getDashboardDetails(token: String){
        walletSuccessModel.value = ApiMapper(
            ApiCallStatus.LOADING,
            null,
            null
        )
        viewModelScope.launch {
           try {
               val loginResponse = reltimeRepository.getWalletDetails(token)
               when (loginResponse.status) {
                   ApiCallStatus.SUCCESS -> {
                       walletSuccessModel.value =
                           ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                   }
                   ApiCallStatus.ERROR -> {
                       walletSuccessModel.value =
                           ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                   }
                   else -> {}
               }
           }catch (e:Exception){
               if (e !is CancellationException)
                   walletSuccessModel.value =
                       ApiMapper(ApiCallStatus.ERROR, null, e.localizedMessage)
           }
        }
    }

    fun doCloseHome(token: String, closeRequestHome: CloseRequestHome) {
        closeSuccesModel = MutableStateFlow<ApiMapper<CloseSuccesModel>>(
            ApiMapper(
                ApiCallStatus.LOADING,
                null,
                null
            )
        )
        viewModelScope.launch {
            val loginResponse = reltimeRepository.doCloseHome(token, closeRequestHome)
            when (loginResponse.status) {
                ApiCallStatus.SUCCESS -> {
                    closeSuccesModel.value =
                        ApiMapper(ApiCallStatus.SUCCESS, loginResponse.data, null)
                }
                ApiCallStatus.ERROR -> {
                    closeSuccesModel.value =
                        ApiMapper(ApiCallStatus.ERROR, null, loginResponse.errorMessage)
                }
                else -> {}
            }
        }
    }

}