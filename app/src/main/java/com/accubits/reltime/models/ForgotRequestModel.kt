package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ForgotRequestModel(
    @Expose
    @SerializedName("emailOrPhone")
    val email: String
)