package com.accubits.reltime.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class InstallementRequest(
    @Expose
    @SerializedName("amount")
    var amount: Double,
    @Expose
    @SerializedName("coinCode")
    val coinCode: String,
    @Expose
    @SerializedName("type")
    val type: String?=null,
    @Expose
    @SerializedName("joint_account")
    val joint_account: String?=null,
    )
