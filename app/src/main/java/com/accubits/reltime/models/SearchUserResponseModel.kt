package com.accubits.reltime.models


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class SearchUserResponseModel(
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: Result,
    @SerializedName("status") val status: Int,
    @SerializedName("success") val success: Boolean
) {
    @Keep
    data class Result(
        @SerializedName("contact_number") val contactNumber: String,
        @SerializedName("email") val email: String,
        @SerializedName("is_exist") val isExist: Boolean,
        @SerializedName("name") val name: String,
        @SerializedName("profile_image") val profileImage: String? = null,
        @SerializedName("user_id") val userId: String,
        @SerializedName("wallet_address") val walletAddress: String
    )
}