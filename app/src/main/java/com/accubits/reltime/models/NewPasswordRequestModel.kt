package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class NewPasswordRequestModel(
    @Expose
    @SerializedName("emailOrPhone")
    val email: String,
    @Expose
    @SerializedName("new_password")
    val new_password: String,
    @Expose
    @SerializedName("confirm_password")
    val confirm_password: String
)
