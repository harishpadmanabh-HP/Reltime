package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class MpinValidateRequestModel(
    @Expose
    @SerializedName("mpin")
    val mpin: String
)