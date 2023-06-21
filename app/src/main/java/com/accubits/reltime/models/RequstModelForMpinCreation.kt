package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RequstModelForMpinCreation(
    @Expose
    @SerializedName("mpin1")
    val mpin1: String,
    @Expose
    @SerializedName("mpin2")
    val mpin2: String
)
