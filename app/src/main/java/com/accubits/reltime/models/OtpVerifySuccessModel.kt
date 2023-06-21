package com.accubits.reltime.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class OtpVerifySuccessModel(
    @Expose
    @SerializedName("success")
    var success: Boolean,
    @Expose
    @SerializedName("status")
    var status: Int,
    @Expose
    @SerializedName("message")
    val message: String,
    @Expose
    @SerializedName("result")
    var result: Result,

    var exception: ErrorResponse?
)

@Keep
data class Result(
    @Expose
    @SerializedName("token_type")
    val token_type: String,
    @Expose
    @SerializedName("expiresIn")
    val expiresIn: String,
    @Expose
    @SerializedName("refresh")
    val refresh: String,
    @Expose
    @SerializedName("access")
    val access: String,
    @Expose
    @SerializedName("user_details")
    var user_details: User_details
)

@Keep
data class User_details(
    @Expose
    @SerializedName("email")
    val email: String,
    @Expose
    @SerializedName("contact_number")
    val contact_number: String,
    @Expose
    @SerializedName("name")
    val name: String,
    @Expose
    @SerializedName("joined_date")
    val joined_date: String,
    @Expose
    @SerializedName("role")
    val role: String,
    @Expose
    @SerializedName("location")
    val location: String
)