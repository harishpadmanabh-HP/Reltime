package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VerifyRequestEmail(
    @Expose
    @SerializedName("emailOrPhone")
    val email: String,
    @Expose
    @SerializedName("otp")
    val otp: String
)
