package com.accubits.reltime.activity.kyc.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class KYCData(
    @SerializedName("kyc_details")
    var kycDetails: KycDetails = KycDetails(),
) {
    data class KycDetails(
        @SerializedName("document_type")
        var documentType: String = "",
        @SerializedName("full_name")
        var fullName: String = "",
        @SerializedName("document_id")
        var documentId: String = "",
        @SerializedName("date_of_birth")
        var dateOfBirth: String = "",
        @SerializedName("issued_date")
        var issueDate: String? = null,
        @SerializedName("expired_date")
        var expiryDate: String? = null,
    )
}
