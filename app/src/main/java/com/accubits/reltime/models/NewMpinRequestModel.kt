package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NewMpinRequestModel(
    @Expose
    @SerializedName("emailOrPhone")
    val email: String,
    @Expose
    @SerializedName("pin")
    val new_password: String,
    @Expose
    @SerializedName("confirm_pin")
    val confirm_password: String
)
