package com.accubits.reltime.activity.kyc.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.accubits.reltime.activity.kyc.model.KYCData
import com.accubits.reltime.activity.kyc.model.KYCResponseModel
import com.accubits.reltime.api.ApiCallStatus
import com.accubits.reltime.api.ApiMapper
import com.accubits.reltime.helpers.PreferenceManager
import com.accubits.reltime.repository.ReltimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KYCViewModel @Inject constructor(
    private val repository: ReltimeRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {
    private val _kycDetails = mutableStateOf(KYCData.KycDetails())
    val kycDetails: MutableState<KYCData.KycDetails> get() = _kycDetails

    private val _kycResponseFlow: MutableStateFlow<ApiMapper<KYCResponseModel>> =
        MutableStateFlow(ApiMapper(ApiCallStatus.LOADING, null, null))
    val kycResponseFlow: MutableStateFlow<ApiMapper<KYCResponseModel>> get() = _kycResponseFlow

    fun uploadKyc(
        documentFile: ByteArray,
        videoFile: ByteArray,
        documentFileName: String,
        videoFileName: String
    ) {
        viewModelScope.launch {
            /*val j = JsonParser().parse("{ \"document_type\": \"Driving Licence\", \"full_name\": " +
                     "\"ANANDHU G\", \"document_id\": \"333333333333332\", \"date_of_birth\": \"1995-01-01\", \"issued_date\": " +
                     "\"1995-01-01\", \"expired_date\": \"1995-01-01\" }").asJsonObject
             val t = Gson().fromJson(j, KYCData.KycDetails::class.java)*/
            val response =
                repository.uploadDocument(
                    preferenceManager.getApiKey(), documentFile,
                    documentFileName, videoFile, videoFileName, kycDetails.value
                )
            when (response.status) {
                ApiCallStatus.SUCCESS -> {
                    kycResponseFlow.value = ApiMapper(ApiCallStatus.SUCCESS, response.data, null)
                }
                ApiCallStatus.ERROR -> {
                    kycResponseFlow.value = ApiMapper(ApiCallStatus.ERROR, null, null)
                }
                else -> {}
            }
        }
    }

    fun validateFields(onValidate: (success: Boolean, message: String) -> Unit = { success, message -> }) {
        kycDetails.value.apply {
            if (fullName.isNullOrBlank()) {
                onValidate(false, "Full name shouldn't be blank")
                return
            }
            if (documentId.isNullOrBlank()) {
                onValidate(false, "Document id shouldn't be empty")
                return
            }
            if (dateOfBirth.isNullOrBlank()) {
                onValidate(false, "Date of birth shouldn't be blank")
                return
            }
            if (documentType.isNullOrBlank()) {
                onValidate(false, "Select a valid document")
                return
            }

            if (issueDate.isNullOrBlank() || expiryDate.isNullOrBlank()) {
                onValidate(false, "Issue date/Expiry date shouldn't be left blank")
                return
            }
            onValidate(true, "Validation success")
        }
    }

}