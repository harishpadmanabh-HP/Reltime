package com.accubits.reltime.models


import com.google.gson.annotations.SerializedName

data class EmailVerificationRequest(
    @SerializedName("email") val email: String
)