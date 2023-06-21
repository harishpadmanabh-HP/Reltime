package com.accubits.reltime.models

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class JointTransactionRequestModel(
    @Expose
    @SerializedName("address")
    val address: String
)