package com.accubits.reltime.models


import com.google.gson.annotations.SerializedName

data class EmailVerificationResponse(
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: Result,
    @SerializedName("status") val status: Int,
    @SerializedName("success") val success: Boolean
) {

    data class Result(
        @SerializedName("contact_number") val contactNumber: String,
        @SerializedName("is_exist") val isExist: Boolean,
        @SerializedName("name") val name: String,
        @SerializedName("profile_image") val profileImage: String,
        @SerializedName("user_id") val userId: String
    )
}