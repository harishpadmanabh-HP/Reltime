package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LogoutRequest(
    @Expose
    @SerializedName("id")
    var id: Int?
)
