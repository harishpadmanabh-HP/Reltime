package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class CloseSuccesModel(

    @Expose
    @SerializedName("success")
    var success: Boolean?,
    @Expose
    @SerializedName("status")
    var status: Int?,
    @Expose
    @SerializedName("message")
    val message: String?,
    var error: Boolean = false,

    )

